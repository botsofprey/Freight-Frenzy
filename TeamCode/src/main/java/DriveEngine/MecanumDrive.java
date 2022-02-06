package DriveEngine;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

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
	private volatile Location currentVelocity;
	private volatile double prePreviousAngle;
	private volatile double previousAngle;
	
	private double[] motorSpeeds;//  rad/s
	private double[] motorRPMs;
	private long[] previousPositions;//  ticks
	private long previousTime;//  nanos
	private long prePreviousTime;
	private long prePrePreviousTime;
	
	private double encoderCPR;
	private double wheelDiameter;
	private double trackWidth;
	private double trackLength;
	private double maxSpeed;

	private Path path;
	private boolean isMoving;

	private LinearOpMode mode;

	public BNO055IMU imu;

	private PIDCoefficients coefficients = new PIDCoefficients(0.1, 0.025, 0.05);
	private PIDCoefficients headingCoefficients =
			new PIDCoefficients(0.1, 0.05, 0.05);
	private PIDController xController = new PIDController(coefficients);
	private PIDController yController = new PIDController(coefficients);
	private PIDController hController = new PIDController(headingCoefficients);
	private double pointCoefficient;
	
	private long moveStart;

	private boolean slowMode;
	private boolean fastMode;
	private boolean trueNorth;
	
	
	public MecanumDrive(HardwareMap hw, String fileName, Location startLocation, boolean north,
	                    LinearOpMode m, boolean errors) {
		mode = m;
		
		initFromConfig(hw, fileName);

		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

		parameters.mode                = BNO055IMU.SensorMode.IMU;
		parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
		parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
		parameters.loggingEnabled      = false;
		parameters.accelerationIntegrationAlgorithm
										= new JustLoggingAccelerationIntegrator();

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
		fastMode = false;
		trueNorth = north;

		previousAngle = 0;
		prePreviousAngle = 0;
		currentLocation = startLocation;
		previousPositions = new long[] { 0, 0, 0, 0 };
		motorSpeeds = new double[] { 0, 0, 0, 0 };
		motorRPMs = new double[] { 0, 0, 0, 0 };
		previousTime = System.nanoTime();
	}
	
	private void initFromConfig(HardwareMap hw, String fileName) {
		JSONReader reader = new JSONReader(hw, fileName);
		for (int i = 0; i < 4; i++) {
			String motorName = reader.getString(MOTOR_NAMES[i] + "Name");
			driveMotors[i] = new MotorController(hw, motorName);
			driveMotors[i].setDirection(
					reader.getString(MOTOR_NAMES[i] + "Direction").equals("forward") ?
							DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE
			);
			driveMotors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
			driveMotors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
			driveMotors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
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
		prePrePreviousTime = prePreviousTime;
		prePreviousTime = previousTime;
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
		double currentRotation = imu.getAngularOrientation(AxesReference.INTRINSIC,
				AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
		double rotation = Math.toRadians(currentLocation.getHeading() - currentRotation);
		double xMovement =
				(motorDistances[0] + motorDistances[1] +//1.6 is a manually tuned constant
						motorDistances[2] + motorDistances[3]) * 0.25 / 0.8;
		double yMovement =
				(motorDistances[1] - motorDistances[0] +//2.13 is a manually tuned constant
						motorDistances[3] - motorDistances[2]) * 0.25 / 1.07;
		double currentHeading = -Math.toRadians(currentLocation.getHeading() + 90);
		
		Matrix vector = new Matrix(new double[][] {
				{xMovement,
				yMovement,
				rotation}
		});
		Matrix PoseExponential = new Matrix(3, 3);
		if (rotation != 0) {
			PoseExponential = new Matrix(new double[][]{
					{	Math.sin(rotation) / rotation,			(Math.cos(rotation) - 1) / rotation,	0 },
					{	(1 - Math.cos(rotation)) / rotation,	Math.sin(rotation) / rotation,			0 },
					{	0,										0,										1 }
			});
		}
		Matrix rotationMatrix = new Matrix(new double[][]{
				{ Math.cos(currentHeading), -Math.sin(currentHeading),  0 },
				{ Math.sin(currentHeading), Math.cos(currentHeading),   0 },
				{ 0,                        0,                          1 }
		});
		vector.mul(PoseExponential.transpose());
		vector.mul(rotationMatrix.transpose());
		double[] movementVectors = vector.getData()[0];
		Location deltaLocation = new Location(movementVectors[0], movementVectors[1],
				currentRotation - currentLocation.getHeading());
		prePreviousAngle = previousAngle;
		previousAngle = currentLocation.getHeading();
		currentLocation.add(deltaLocation);
		currentVelocity = deltaLocation.scale(1.0 / timeDiff);
		currentVelocity.setHeading(currentVelocity.getHeading() * 0.1);
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
		if (currentLocation.distanceToLocation(path.getEnd()) < path.getError() &&
				currentLocation.headingDifference(path.getEnd()) < path.getAngleError()) {
			isMoving = false;
			for (int i = 0; i < 4; i++) {
				driveMotors[i].setPower(0);
			}
			xController.reset();
			yController.reset();
			hController.reset();
			return;
		}
		else if (currentLocation.distanceToLocation(path.getEnd()) < path.getError()) {
			xController.reset();
			yController.reset();
		}
		else if (currentLocation.headingDifference(path.getEnd()) < path.getAngleError()) {
			hController.reset();
		}

//		double location = Math.min((previousTime - moveStart) * pointCoefficient, 1);
		Location target = path.getTargetLocation(currentLocation, 3);//look three inches down the path
//		Location target = path.interpolateLocation(location);
		mode.telemetry.addData("target", target.toString());
		mode.telemetry.addData("position", currentLocation.toString());
		xController.setTargetPoint(target.getX());
		yController.setTargetPoint(target.getY());
		hController.setTargetPoint(target.getHeading());
		double xMovement = xController.calculateAdjustment(currentLocation.getX());
		double yMovement = yController.calculateAdjustment(currentLocation.getY());
		double hMovement = hController.calculateAdjustment(currentLocation.getHeading());
		moveRobot(xMovement, yMovement, hMovement);
	}
	
	public void update() {
		updateLocation();
		mode.telemetry.addData("Location", currentLocation);
		mode.telemetry.update();
		if (isMoving) {
			correctTrajectory();
		}
	}


	public void moveToLocation(Location location) {
		path = new Path(currentLocation, location);
		isMoving = true;
		moveStart = System.nanoTime();
		pointCoefficient = Math.min(12.0 / path.getPathLength(), 90.0 / path.getPathAngleChange())
				/ 1_000_000_000.0;
		while (mode.opModeIsActive() && isMoving()) {
			if (System.nanoTime() - previousTime > 10_000_000) {
				update();
			}
		}
	}
	
	public Location getCurrentLocation() {
		return currentLocation;
	}

	public Location getCurrentVelocity() { return currentVelocity; }

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

	public void coast() {
		for (int i = 0; i < 4; i++) {
			driveMotors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
		}
	}

	public void brake() {
		for (int i = 0; i < 4; i++) {
			driveMotors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		}
	}
	
	public void moveRobot(double x, double y, double a) {
		if (slowMode) {
			x /= 3;
			y /= 3;
			a /= 3;
		}
		if (fastMode) {
			x *= 3;
			y *= 3;
			a *= 3;
		}
		if (trueNorth) {
			Vec2d movementVector = new Vec2d(x, y);
			movementVector.convertToAngleMagnitude();
			movementVector.angle -= currentLocation.getHeading();
			movementVector.convertToXY();
			x = movementVector.x;
			y = movementVector.y;
		}
		
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
	public void fastMode() { fastMode = true; }
	public void noFastMode() { fastMode = false; }
}
