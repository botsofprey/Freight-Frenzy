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
	private Trajectory currentTrajectory;
	private boolean currentlyMoving;
	private long startTime;
	private double maxSpeed = 30;
	private SplineCurve path;
	
	private PIDCoefficients coefficients = new PIDCoefficients(0.1, 0.025, 0.05);
	private PIDCoefficients headingCoefficients =
			new PIDCoefficients(0.01, 0.005, 0.005);
	private PIDController xController = new PIDController(coefficients);
	private PIDController yController = new PIDController(coefficients);
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
		previousTime = System.nanoTime();
	}
	
	private void updateLocation() {
		localizer.updateLocation();
		currentLocation = localizer.getCurrentLocation();
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
		x /= 43.0;
		y /= 17.0;
		h /= 14.0;
		rawMove(x, y, h);
	}
	
	public void moveTrueNorth(double x, double y, double h) {
		Matrix vec = new Matrix(new double[][]{ { x }, { y } });
		double heading = -Math.toRadians(currentLocation.getHeading());
		double sin = Math.sin(heading);
		double cos = Math.cos(heading);
		Matrix rotation = new Matrix(new double[][]{
				{ cos, -sin },
				{ sin,  cos }
		});
		double[] result = rotation.mul(vec).transpose().getData()[0];
		move(result[0], result[1], h);
	}
	
	private void calculateMovement() {
		long currentTime = System.nanoTime();
		double time = (currentTime - previousTime) / 1_000_000_000.0;
		previousTime = currentTime;
		TrajectoryPoint point = currentTrajectory.getMotion(time);
		
		xController.setTargetPoint(point.x);
		yController.setTargetPoint(point.y);
		hController.setTargetPoint(point.h);
		double x = xController.calculateAdjustment(currentLocation.getX());
		double y = yController.calculateAdjustment(currentLocation.getY());
		double h = hController.calculateAdjustment(currentLocation.getHeading());
		
		moveTrueNorth(point.vx + x, point.vy + y, point.vh + h);
		if (time > currentTrajectory.getDuration()) {
			currentlyMoving = false;
		}
	}

	private void altMovement() {
		long currentTime = System.nanoTime();
		double time = (currentTime - previousTime) / 1_000_000_000.0;
		double inches = time * maxSpeed;
		Location point = path.getPoint(inches, 0.1);

		xController.setTargetPoint(point.getX());
		yController.setTargetPoint(point.getY());
		hController.setTargetPoint(point.getHeading());
		double x = xController.calculateAdjustment(currentLocation.getX());
		double y = yController.calculateAdjustment(currentLocation.getY());
		double h = hController.calculateAdjustment(currentLocation.getHeading());

		double distanceToEnd = currentLocation.distanceToLocation(path.getPoint(1));
		System.out.println("Distance to end point: " + distanceToEnd);
		System.out.println("Theoretical distance to end point: " + (path.getLength() - inches));

		if ((inches - maxSpeed * 0.5 >= path.getLength()) ||
				distanceToEnd <= 0.5) {
			currentlyMoving = false;
			rawMove(0, 0, 0);
		}
		else {
			moveTrueNorth(x, y, h);
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
}
