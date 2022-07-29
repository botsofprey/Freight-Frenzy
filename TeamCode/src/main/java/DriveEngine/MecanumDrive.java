package DriveEngine;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import DriveEngine.Deprecated.OldLocalizerClass;
import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.JSONReader;
import UtilityClasses.Deprecated.OldLocationClass;
import UtilityClasses.Deprecated.Matrix;
import UtilityClasses.Location;
import UtilityClasses.PIDController;

/**
 * This is a class for moving the robot to set positions on the field.
 *
 * @author Alex Prichard
 */
public class MecanumDrive {
	private MotorController[] motors = new MotorController[4];
	private static final String[] MOTOR_NAMES = {
			"frontLeftDriveMotor",
			"backLeftDriveMotor",
			"backRightDriveMotor",
			"frontRightDriveMotor"
	};
	
	private LinearOpMode mode;
	
	private double Kt;
	private double R;
	private double Kv;
	
	private OldLocalizerClass localizer;
	private OldLocationClass currentLocation;
	private boolean currentlyMoving;
	private static final double buffer = 0.1;
	private static final double maxTorque = 1;
	public static final double MAX_SPEED = 24;
	public static final double MAX_ANGULAR = 90;
	
	// these have been tuned, you can re tune them if you want and know what you're doing
	private PIDCoefficients xCoefficients = new PIDCoefficients(0.08, 0.01, 0.02);
	private PIDCoefficients yCoefficients = new PIDCoefficients(0.08, 0.01, 0.02);
	private PIDCoefficients headingCoefficients = new PIDCoefficients(0.02, 0.006, 0.005);
	private PIDController xController = new PIDController(xCoefficients);
	private PIDController yController = new PIDController(yCoefficients);
	private PIDController hController = new PIDController(headingCoefficients);

	// for config file, see RobotConfig.json
	// track width and track length change based on width and length of the robot
	public MecanumDrive(HardwareMap hw, String configFileName,
	                    Location startLocation, LinearOpMode m) {
		this(hw, configFileName, startLocation.getAsOldLocation(), m);
	}
	@Deprecated
	public MecanumDrive(HardwareMap hw, String configFileName,
	                    OldLocationClass startLocation, LinearOpMode m) {
		mode = m;
		
		initFromConfig(hw, configFileName);

		localizer = new OldLocalizerClass(hw, configFileName, startLocation, mode);
		
		currentlyMoving = false;
		currentLocation = startLocation;
	}
	
	private void initFromConfig(HardwareMap hw, String fileName) {
		JSONReader reader = new JSONReader(hw, fileName);
		for (int i = 0; i < 4; i++) {
			String motorName = reader.getString(MOTOR_NAMES[i] + "Name");
			motors[i] = new MotorController(hw, motorName);
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
	
	public void updateLocation() {
		localizer.updateLocation();
		currentLocation = localizer.getCurrentLocation();
	}
	
	public void ram() {
		oldRawMove(0, 1, 0);
	}

	public void brake() {
		oldRawMove(0, 0, 0);
	}

	public void rawMove(double x, double y, double h) {
		rawMove(x, y, h, 1);
	}
	public void rawMove(double x, double y, double h, double speed) {
		oldRawMove(-y, +x, h, speed);
		
	}
	
	@Deprecated
	public void oldRawMove(double x, double y, double h) {
		oldRawMove(x, y, h, 1);
	}
	@Deprecated
	public void oldRawMove(double x, double y, double h, double speed) {
		double[] powers = new double[]{
				x + y - h,
				-x + y - h,
				x + y + h,
				-x + y + h
		};
		double max = speed;
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
		oldRawMove(x, y, h);
	}

	public void moveTrueNorth(double x, double y, double h) {
		moveTrueNorth(x, y, h, 1.0);
	}
	
	public void moveTrueNorth(double x, double y, double h, double speed) {
		Matrix vec = new Matrix(new double[][]{ { x }, { y } });
		double heading = -Math.toRadians(currentLocation.getHeading());
		double sin = Math.sin(heading);
		double cos = Math.cos(heading);
		Matrix rotation = new Matrix(new double[][]{
				{ cos, sin },
				{ -sin,  cos }
		});
		double[] result = rotation.mul(vec).transpose().getData()[0];
		oldRawMove(result[0], result[1], h, speed);
	}
	
	public void moveToLocation(double x, double y, double h) {
		moveToLocation(new Location(x, y, h));
	}

	public void moveToLocation(Location targetLocation) {
		moveToLocation(targetLocation.getAsOldLocation());
	}
	@Deprecated
	public void moveToLocation(OldLocationClass targetLocation) {
		moveToLocation(targetLocation, 1);
	}
	
	public void moveToLocation(Location targetLocation, double speed) {
		moveToLocation(targetLocation.getAsOldLocation(), speed);
	}
	@Deprecated
	public void moveToLocation(OldLocationClass targetLocation, double speed) {
		xController.reset();
		yController.reset();
		hController.reset();
		xController.setSP(targetLocation.getX());
		yController.setSP(targetLocation.getY());
		hController.setSP(targetLocation.getHeading());
		long startTime = System.currentTimeMillis();
		long endTime = 1000 + startTime + (long)(1000 *
				Math.hypot(currentLocation.distanceToLocation(targetLocation) / MAX_SPEED,
						currentLocation.headingDifference(targetLocation) / MAX_ANGULAR));
		while (mode.opModeIsActive()) {
			updateLocation();
			double x = xController.calculateResponse(currentLocation.getX());
			double y = -yController.calculateResponse(currentLocation.getY());
			double h = hController.calculateResponse(currentLocation.getHeading());
			if (currentLocation.distanceToLocation(targetLocation) < 1
					&& currentLocation.headingDifference(targetLocation) < 5
					|| endTime <= System.currentTimeMillis()) {
				oldRawMove(0, 0, 0);
				break;
			}
			moveTrueNorth(x, y, h, speed);
		}
	}
	
	/**
	 * This rotates the robot to the given heading
	 *
	 * @param angle
	 * @author Alex Prichard
	 */
	public void rotate(double angle) {
		hController.reset();
		hController.setSP(angle);
		long startTime = System.currentTimeMillis();
		long endTime = 1000 + startTime + (long)Math.abs(1000 *
						currentLocation.headingDifference(angle) / MAX_ANGULAR);
		while (mode.opModeIsActive()) {
			updateLocation();
			double h = hController.calculateResponse(currentLocation.getHeading());
			if (Math.abs(currentLocation.headingDifference(angle)) < 2
					|| endTime <= System.currentTimeMillis()) {
				oldRawMove(0, 0, 0);
				break;
			}
			moveTrueNorth(0, 0, h);
		}
	}
	
	public void update() {
		updateLocation();
		oldRawMove(0, 0, 0);
	}
	
	public Location getCurrentLocation() { return new Location(currentLocation); }

	public void setCurrentLocation(OldLocationClass location) {
		localizer.setCurrentLocation(location);
	}
	public void setCurrentLocation(Location location) {
		localizer.setCurrentLocation(location.getAsOldLocation());
	}
}
