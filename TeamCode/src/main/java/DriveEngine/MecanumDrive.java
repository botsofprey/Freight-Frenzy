package DriveEngine;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.io.IOException;
import java.util.List;

import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.JSONReader;
import UtilityClasses.Location;
import UtilityClasses.Matrix;
import UtilityClasses.PIDController;
import UtilityClasses.Vec2d;

public class MecanumDrive {
	private static final double FRONT_LEFT_DRIVE_MOTOR = 0;
	private static final double BACK_LEFT_DRIVE_MOTOR = 1;
	private static final double BACK_RIGHT_DRIVE_MOTOR = 2;
	private static final double FRONT_RIGHT_DRIVE_MOTOR = 3;
	private static final String[] MOTOR_NAMES = {
			"frontLeftDriveMotor",
			"backLeftDriveMotor",
			"backRightDriveMotor",
			"frontRightDriveMotor"
	};
	
	private static final double SQRT_ONE_HALF = Math.sqrt(0.5);
	
	
	private volatile MotorController[] driveMotors = new MotorController[4];

	private double Kt;
	private double R;
	private double Kv;
	private double maxTorque;
	private double buffer = 0.8;
	
	private volatile Location currentLocation;
	
	private double[] motorSpeeds;//  rad/s
	private double[] motorRPMs;
	private long[] previousPositions;//  ticks
	private long previousTime;//  nanos
	
	private double encoderCPR;
	private double wheelDiameter;
	private double trackWidth;
	private double trackLength;
	private double maxSpeed;

	private Path path;
	private boolean isMoving;

	private LinearOpMode mode;

	private BNO055IMU imu;

	private PIDCoefficients coefficients = new PIDCoefficients(0.1, 0.1, 0.2);
	private PIDCoefficients headingCoefficients = new PIDCoefficients(0.01, 0.01, 0.02);
	private PIDController xController = new PIDController(coefficients);
	private PIDController yController = new PIDController(coefficients);
	private PIDController hController = new PIDController(headingCoefficients);
	private double lookAheadDistance = 6;
	private double lookAheadLocation;

	private boolean slowMode;
	
	
	public MecanumDrive(HardwareMap hw, String fileName, Location startLocation, LinearOpMode m,
						boolean errors) {
		mode = m;
		
		initFromConfig(hw, fileName, errors);

		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

		parameters.mode                = BNO055IMU.SensorMode.IMU;
		parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
		parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		parameters.loggingEnabled      = false;

		// Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
		// on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
		// and named "imu".
		imu = hw.get(BNO055IMU.class, "imu");

		imu.initialize(parameters);

		List<LynxModule> allHubs = hw.getAll(LynxModule.class);
		for (LynxModule hub : allHubs) {
			hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
		}

		slowMode = false;

		currentLocation = startLocation;
		previousPositions = new long[] { 0, 0, 0, 0 };
		motorSpeeds = new double[] { 0, 0, 0, 0 };
		motorRPMs = new double[] { 0, 0, 0, 0 };
		previousTime = System.nanoTime();
	}
	
	private void initFromConfig(HardwareMap hw, String fileName, boolean errors) {
		JSONReader reader = new JSONReader(hw, fileName);
		for (int i = 0; i < 4; i++) {
			String motorName = reader.getString(MOTOR_NAMES[i] + "Name");
			driveMotors[i] = new MotorController(hw, motorName, mode, errors);
			driveMotors[i].setDirection(
					reader.getString(MOTOR_NAMES[i] + "Direction").equals("forward") ?
							DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE
			);
			driveMotors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
			driveMotors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
			driveMotors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
		}
		trackWidth = reader.getDouble("trackWidth") / 2.0;
		trackLength = reader.getDouble("trackLength") / 2.0;
		maxTorque = reader.getDouble("maxDriveTorque");
		
		JSONReader motorReader = new JSONReader(hw, reader.getString("driveMotorFile"));
		encoderCPR = motorReader.getDouble("ticks_per_revolution");
		wheelDiameter = motorReader.getDouble("wheel_diameter");
		maxSpeed =
				Math.sqrt(2) * Math.PI * wheelDiameter * motorReader.getDouble("max_rpm") / 30;
		double stallCurrent = motorReader.getDouble("stall_current");
		Kt = motorReader.getDouble("stall_torque") / stallCurrent;
		R = 12 / stallCurrent;
		Kv = motorReader.getDouble("max_rpm") /
				(12 - motorReader.getDouble("no_load_current") * R);
	}
	
	private void updateLocation() {//  calculate new position from odometry data
		long[] positions = new long[4];
		for (int i = 0; i < 4; i++) {
			positions[i] = driveMotors[i].getCurrentPosition();
		}
		long currentTime = System.nanoTime();

		long deltaTime = currentTime - previousTime;
		previousTime = currentTime;
		double timeDiff = deltaTime / 1_000_000_000.0;//convert nanoseconds to seconds
		double[] rotationAngles = new double[4];
		double[] motorDistances = new double[4];
		for (int i = 0; i < 4; i++) {
			positions[i] -= previousPositions[i];
			previousPositions[i] += positions[i];
			rotationAngles[i] = positions[i] / (encoderCPR * 2 * Math.PI);
			motorDistances[i] = Math.PI * wheelDiameter * positions[i] / encoderCPR;
			motorSpeeds[i] = rotationAngles[i] / timeDiff;
			motorRPMs[i] = rotationAngles[i] * 2 * Math.PI;
		}
		double rotation = Math.toRadians(imu.getAngularOrientation(AxesReference.INTRINSIC,
				AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle - currentLocation.getHeading());

		double[] movementVectors = {
				motorDistances[0] + motorDistances[1] + motorDistances[2] + motorDistances[3],
				motorDistances[0] - motorDistances[1] + motorDistances[2] - motorDistances[3],
				rotation
		};
		movementVectors[0] *= -0.25;
		movementVectors[1] *= -0.25;
		movementVectors[2] = Math.toDegrees(movementVectors[2]);

		Location deltaLocation =
				new Location(movementVectors[1], movementVectors[0], movementVectors[2]);
		/*
		Location deltaLocation;
		double a = Math.toRadians(currentLocation.getHeading());
		double cosA = Math.cos(a);
		double sinA = Math.sin(a);
		if (movementVectors[2] == 0) {
			deltaLocation = new Location(
					movementVectors[0] * cosA - movementVectors[1] * sinA,
					movementVectors[1] * cosA + movementVectors[0] * sinA,
					0
			);
		}
		else {
			double theta = movementVectors[2];
			double r = Math.hypot(movementVectors[0], movementVectors[1]) / theta;
			deltaLocation = new Location(
					r * (Math.cos(theta + a) - cosA),
					r * (Math.sin(theta + a) - sinA),
					Math.toDegrees(theta)
			);
		}*/
		currentLocation.add(deltaLocation);
	}

	private double[] getPowerRange(double currentRPM) {
		double lower = (currentRPM / Kv - buffer * maxTorque * R / Kt) / 12.0;
		double upper = (currentRPM / Kv + buffer * maxTorque * R / Kt) / 12.0;
		return new double[]{ lower, upper };
	}

	private void adjustWheelPowers(double[] wheelPowers) {
		double[][] wheelRanges = {
				getPowerRange(motorRPMs[0]),
				getPowerRange(motorRPMs[1]),
				getPowerRange(motorRPMs[2]),
				getPowerRange(motorRPMs[3])
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

	private void correctTrajectory() {
		if (currentLocation.distanceToLocation(path.getEnd()) < path.getError()) {
			isMoving = false;
			for (int i = 0; i < 4; i++) {
				driveMotors[i].setPower(0);
			}
			return;
		}
		
		double location = path.reverse_interpolate(currentLocation);
		location += lookAheadLocation;
		location = Math.min(location, 1);
		Location target = path.interpolateLocation(location);
		xController.setTargetPoint(target.getX());
		yController.setTargetPoint(target.getY());
		hController.setTargetPoint(target.getHeading());
		double xMovement = xController.calculateAdjustment(currentLocation.getX());
		double yMovement = xController.calculateAdjustment(currentLocation.getY());
		double hMovement = xController.calculateAdjustment(currentLocation.getHeading());
		moveRobot(xMovement, yMovement, hMovement);
	}
	
	public void update() {
		updateLocation();
		if (isMoving) {
			correctTrajectory();
		}
	}


	public void moveToLocation(Location location) {
		path = new Path(currentLocation, location);
		isMoving = true;
		lookAheadLocation = lookAheadDistance / path.getPathLength();
		while (mode.opModeIsActive() && isMoving) {
			if (System.nanoTime() - previousTime > 10_000_000) {
				update();
			}
		}
	}
	
	public Location getCurrentLocation() {
		return currentLocation;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public int[] getMotorLocations() {
		int[] locations = new int[4];
		for (int i = 0; i < 4; i++) {
			locations[i] = driveMotors[i].getCurrentPosition();
		}
		return locations;
	}

	public void calibrate() {
		long startTime = previousTime;
		double[] speeds = { 1, 1, -1, -1 };
		for (int i = 0; i < 4; i++) {
			driveMotors[i].setPower(speeds[i]);
		}
		while (mode.opModeIsActive() && startTime + 10_000_000 > System.nanoTime()) {
			update();
		}
		Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC,
				AxesOrder.XYZ, AngleUnit.DEGREES);
		double angleRatio = angles.firstAngle / currentLocation.getHeading();
		mode.telemetry.addData("Angle ratio", angleRatio);
		mode.telemetry.update();
		while (mode.opModeIsActive());
	}
	
	public void moveRobot(double x, double y, double a) {
		if (slowMode) {
			x /= 3;
			y /= 3;
			a /= 3;
		}
		Vec2d movementVector = new Vec2d(x, y);
		movementVector.convertToAngleMagnitude();
		movementVector.angle -= currentLocation.getHeading();
		movementVector.convertToXY();
		x = movementVector.x;
		y = movementVector.y;
		
		double rightVector = SQRT_ONE_HALF * (y + x);
		double leftVector = SQRT_ONE_HALF * (y - x);
		double[] powers = {
				rightVector - a,
				leftVector - a,
				rightVector + a,
				leftVector + a
		};
		adjustWheelPowers(powers);
		for (int i = 0; i < powers.length; i++) {
			driveMotors[i].setPower(powers[i]);
		}
	}

	public void slowMode() { slowMode = true; }
	public void noSlowMode() { slowMode = false; }
}
