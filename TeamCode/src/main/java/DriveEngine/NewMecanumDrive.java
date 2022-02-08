package DriveEngine;

import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.robotcore.external.Func;

import java.util.List;

import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.JSONReader;
import UtilityClasses.Location;
import UtilityClasses.Matrix;
import UtilityClasses.PIDController;

/**
 * @author Alex Prichard
 * @version 1.0
 * @since 5/1/2022
 * This class is used to follow a Trajectory generated by the TrajectoryBuilder class.
 */
public class NewMecanumDrive {
	private MotorController[] motors = new MotorController[4];
	private static final String[] MOTOR_NAMES = {
			"frontLeftDriveMotor",
			"backLeftDriveMotor",
			"backRightDriveMotor",
			"frontRightDriveMotor"
	};
	private long previousTime;
	
	private LinearOpMode mode;
	
	private boolean slowMode;
	private boolean fastMode;
	
	private double Kt;
	private double R;
	private double Kv;
	
	private Localizer localizer;
	private Location currentLocation;
	public Location targetLocation;
	private Trajectory currentTrajectory;
	private boolean currentlyMoving;
	private static final double buffer = 0.1;
	private static final double maxTorque = 1;
	public static final double MAX_SPEED = 24;
	public static final double MAX_ANGULAR = 90;
	private SplineCurve path;
	
	private PIDCoefficients xCoefficients = new PIDCoefficients(0.08, 0.01, 0.02);
	private PIDCoefficients yCoefficients = new PIDCoefficients(0.08, 0.01, 0.02);
	private PIDCoefficients headingCoefficients = new PIDCoefficients(0.02, 0.006, 0.005);
	private PIDController xController = new PIDController(xCoefficients);
	private PIDController yController = new PIDController(yCoefficients);
	private PIDController hController = new PIDController(headingCoefficients);

	public NewMecanumDrive(HardwareMap hw, String fileName,
	                       Location startLocation, LinearOpMode m) {
		mode = m;
		
		initFromConfig(hw, fileName);
		
		slowMode = false;
		fastMode = false;

		localizer = new Localizer(hw, fileName, startLocation, mode);
		
		currentlyMoving = false;
		currentLocation = startLocation;
		previousTime = System.nanoTime();
	}
	
	private void initFromConfig(HardwareMap hw, String fileName) {
		JSONReader reader = new JSONReader(hw, fileName);
		for (int i = 0; i < 4; i++) {
			String motorName = reader.getString(MOTOR_NAMES[i] + "Name");
			motors[i] = new MotorController(hw, motorName, mode, true);
			motors[i].setDirection(
					reader.getString(MOTOR_NAMES[i] + "Direction").equals("forward") ?
							DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE
			);
			motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
			motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		}
		
		JSONReader motorReader = new JSONReader(hw, reader.getString("driveMotorFile"));
		double stallCurrent = motorReader.getDouble("stall_current");
		Kt = motorReader.getDouble("stall_torque") / stallCurrent;
		R = 12 / stallCurrent;
		Kv = motorReader.getDouble("max_rpm") /
				(12 - motorReader.getDouble("no_load_current") * R);
	}

	public void followTrajectory(Trajectory trajectory) {
		currentTrajectory = trajectory;
		currentlyMoving = true;
	}

	public void followPath(SplineCurve path) {
		this.path = path;
		currentlyMoving = true;
		xController.reset();
		yController.reset();
		hController.reset();
		previousTime = System.nanoTime();
	}
	
	public void updateLocation() {
		localizer.updateLocation();
		currentLocation = localizer.getCurrentLocation();
	}

	private double[] getPowerRange(double currentRPM) {
		double lower = (currentRPM / Kv - buffer * maxTorque * R / Kt) / 12.0;
		double upper = (currentRPM / Kv + buffer * maxTorque * R / Kt) / 12.0;
		return new double[]{ lower, upper };
	}

	private void adjustWheelPowers(double[] wheelPowers) {
		double[][] wheelRanges = {
				getPowerRange(localizer.motorRPMs[0]),
				getPowerRange(localizer.motorRPMs[1]),
				getPowerRange(localizer.motorRPMs[2]),
				getPowerRange(localizer.motorRPMs[3])
		};
		double[] scaleFactors = new double[4];
		for (int i = 0; i < 4; i++) {
			double clampedValue =
					Math.min(wheelRanges[i][1], Math.max(wheelRanges[i][0], wheelPowers[i]));
			scaleFactors[i] = clampedValue / wheelPowers[i];
		}
		double maxDiff = 0;
		int index = 0;
		for (int i = 0; i < 4; i++) {
			if (Math.abs(scaleFactors[i] - 1) > maxDiff) {
				maxDiff = Math.abs(scaleFactors[i] - 1);
				index = i;
			}
		}
		for (int i = 0; i < 4; i++) {
			wheelPowers[i] /= scaleFactors[index];
		}
	}

	public void ram() {
		rawMove(0, 1, 0);
	}

	public void brake() {
		rawMove(0, 0, 0);
	}

	public void rawMove(double x, double y, double h) {
		double[] powers = new double[]{
				x + y - h,
				-x + y - h,
				x + y + h,
				-x + y + h
		};
		double max = 1;
		for (double power : powers) {
			max = Math.max(max, Math.abs(power));
		}
		for (int i = 0; i < 4; i++) {
			powers[i] /= max;
		}
		for (int i = 0; i < 4; i++) {
			motors[i].setPower(powers[i]);
		}
	}
	
	public void move(double x, double y, double h) {
		rawMove(x, y, h);
	}
	
	public void moveTrueNorth(double x, double y, double h) {
		Matrix vec = new Matrix(new double[][]{ { x }, { y } });
		double heading = -Math.toRadians(currentLocation.getHeading());
		double sin = Math.sin(heading);
		double cos = Math.cos(heading);
		Matrix rotation = new Matrix(new double[][]{
				{ cos, sin },
				{ -sin,  cos }
		});
		double[] result = rotation.mul(vec).transpose().getData()[0];
		move(result[0], result[1], h);
	}
	
	private void calculateMovement() {
		long currentTime = System.nanoTime();
		double time = (currentTime - previousTime) / 1_000_000_000.0;
		previousTime = currentTime;
		TrajectoryPoint point = currentTrajectory.getMotion(time);
		
//		xController.setTargetPoint(point.x);
//		yController.setTargetPoint(point.y);
		hController.setTargetPoint(point.h);
//		double x = xController.calculateAdjustment(currentLocation.getX());
//		double y = yController.calculateAdjustment(currentLocation.getY());
		double h = hController.calculateAdjustment(currentLocation.getHeading());
		
//		moveTrueNorth(point.vx + x, point.vy + y, point.vh + h);
		if (time > currentTrajectory.getDuration()) {
			currentlyMoving = false;
		}
	}

	private void altMovement() {
		long currentTime = System.nanoTime();
		double time = (currentTime - previousTime) / 1_000_000_000.0;
		double inches = Math.min(time * MAX_SPEED, path.getLength());
		targetLocation = path.getPoint(inches, 0.1);
		targetLocation = new Location(0, 1, 0);
		Location base =
				path.getAccelControlVelocity(inches / path.getLength(), MAX_SPEED, MAX_ANGULAR);
		
		xController.setTargetPoint(targetLocation.getX());
		yController.setTargetPoint(targetLocation.getY());
		hController.setTargetPoint(targetLocation.getHeading());
		double x = xController.calculateAdjustment(currentLocation.getX());
		double y = yController.calculateAdjustment(currentLocation.getY());
		double h = hController.calculateAdjustment(currentLocation.getHeading());
		mode.telemetry.addData("X:", x);
		mode.telemetry.addData("Y:", y);
		mode.telemetry.addData("H:", h);
		
		double distanceToEnd = currentLocation.distanceToLocation(path.getEnd());
		if (time - 1 >= path.getLength() / MAX_SPEED && false || distanceToEnd <= 0.5) {
			currentlyMoving = false;
			rawMove(0, 0, 0);
		}
		else {
			//moveTrueNorth(x + base.getX(), y + base.getY(), h + base.getHeading());
			moveTrueNorth(x, y, h);
		}
	}
	
	public void moveToLocation(double x, double y, double h) {
		moveToLocation(new Location(x, y, h));
	}
	
	public void moveToLocation(Location targetLocation) {
		xController.reset();
		yController.reset();
		hController.reset();
		xController.setTargetPoint(targetLocation.getX());
		yController.setTargetPoint(targetLocation.getY());
		hController.setTargetPoint(targetLocation.getHeading());
		long startTime = System.currentTimeMillis();
		long endTime = 1000 + startTime + (long)(1000 *
				Math.hypot(currentLocation.distanceToLocation(targetLocation) / MAX_SPEED,
						currentLocation.headingDifference(targetLocation) / MAX_ANGULAR));
		while (mode.opModeIsActive()) {
			updateLocation();
//			mode.telemetry.addData("Status", "Moving");
//			mode.telemetry.addData("Location", currentLocation);
//			mode.telemetry.addData("Target", targetLocation);
//			mode.telemetry.addData("Angle",
//					currentLocation.headingDifference(targetLocation));
//			mode.telemetry.addData("Time left",
//					(endTime - System.currentTimeMillis()) / 1000.0);
//			mode.telemetry.update();
			double x = xController.calculateAdjustment(currentLocation.getX());
			double y = -yController.calculateAdjustment(currentLocation.getY());
			double h = hController.calculateAdjustment(currentLocation.getHeading());
			if (currentLocation.distanceToLocation(targetLocation) < 1
					&& currentLocation.headingDifference(targetLocation) < 5
					|| endTime <= System.currentTimeMillis()) {
				rawMove(0, 0, 0);
				break;
			}
			moveTrueNorth(x, y, h);
		}
	}
	
	public void rotate(double angle) {
		hController.reset();
		hController.setTargetPoint(angle);
		long startTime = System.currentTimeMillis();
		long endTime = 1000 + startTime + (long)Math.abs(1000 *
						currentLocation.headingDifference(angle) / MAX_ANGULAR);
		while (mode.opModeIsActive()) {
			updateLocation();
//			mode.telemetry.addData("Angle",
//					currentLocation.headingDifference(angle));
//			mode.telemetry.addData("Time left",
//					(endTime - System.currentTimeMillis()) / 1000.0);
//			mode.telemetry.update();
			double h = hController.calculateAdjustment(currentLocation.getHeading());
			if (Math.abs(currentLocation.headingDifference(angle)) < 2
					|| endTime <= System.currentTimeMillis()) {
				rawMove(0, 0, 0);
				break;
			}
			moveTrueNorth(0, 0, h);
		}
	}
	
	public void update() {
		updateLocation();
		if (currentlyMoving) {
			altMovement();
		} else {
			rawMove(0, 0, 0);
		}
	}

	public void waitForMovement() {
		waitForMovement(()->{});
	}

	public void waitForMovement(Runnable updateFunctions) {
		previousTime = System.nanoTime();
		while (mode.opModeIsActive() && currentlyMoving) {
			update();
			updateFunctions.run();
		}
	}

	public boolean isMoving() {
		return currentlyMoving;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location location) { localizer.setCurrentLocation(location); }
}
