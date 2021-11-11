package DriveEngine;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.JSONReader;
import UtilityClasses.Location;
import UtilityClasses.Matrix;

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
	
	
	private volatile DcMotorEx[] driveMotors = new DcMotorEx[4];
	
	private volatile Location currentLocation;
	
	private double[] motorSpeeds;//  rad/s
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
	
	
	public MecanumDrive(HardwareMap hw, String fileName, LinearOpMode m) {
		initFromConfig(hw, fileName);

		mode = m;
		previousPositions = new long[] { 0, 0, 0, 0 };
		motorSpeeds = new double[] { 0, 0, 0, 0 };
		previousTime = System.nanoTime();
	}
	
	private void initFromConfig(HardwareMap hw, String fileName) {
		JSONReader reader = new JSONReader(hw, fileName);
		for (int i = 0; i < 4; i++) {
			driveMotors[i] = hw.get(DcMotorEx.class, reader.getString(MOTOR_NAMES[i] + "Name"));
			driveMotors[i].setDirection(
					reader.getString(MOTOR_NAMES[i] + "Direction").equals("forward") ?
							DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE
			);
			driveMotors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
			driveMotors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
		}
		JSONReader motorReader = new JSONReader(hw, reader.getString("driveMotorFile"));
		encoderCPR = motorReader.getDouble("ticks_per_revolution");
		wheelDiameter = motorReader.getDouble("wheel_diameter");
		maxSpeed =
				Math.sqrt(2) * Math.PI * wheelDiameter * motorReader.getDouble("max_rpm") / 30;
		trackWidth = motorReader.getDouble("trackWidth");
		trackLength = motorReader.getDouble("trackLength");
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
		for (int i = 0; i < 4; i++) {
			positions[i] -= previousPositions[i];
			previousPositions[i] += positions[i];
			rotationAngles[i] = positions[i] / (encoderCPR * 2 * Math.PI);
			motorSpeeds[i] = rotationAngles[i] / timeDiff;
		}
		
		double wheelPosition = (trackLength + trackWidth) * 0.5;
		Matrix kinematics = new Matrix(new double[][]{
				{ 1, 1, 1, 1 },
				{ -1, 1, -1, 1 },
				{ -1 / wheelPosition, -1 / wheelPosition, 1 / wheelPosition, 1 / wheelPosition }
		});
		Matrix wheelRotations = new Matrix(new double[][]{rotationAngles}).transpose();
		double[] movementVectors = kinematics.mul(wheelRotations)
				.scale(wheelDiameter / 2)
				.transpose()
				.getData()[0];
		
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
		}
		currentLocation.add(deltaLocation);
	}

	private void correctTrajectory() {
		if (currentLocation.distanceToLocation(path.getEnd()) < path.getError()) {
			isMoving = false;
			for (int i = 0; i < 4; i++) {
				driveMotors[i].setPower(0);
			}
			return;
		}
		Location targetLocation = path.getTargetLocation(currentLocation, 0.01 * maxSpeed);
		double r = targetLocation.getHeading();
		targetLocation.subXY(currentLocation);
		double sinA = Math.sin(Math.toRadians(currentLocation.getHeading()));
		double cosA = Math.cos(Math.toRadians(currentLocation.getHeading()));
		Matrix reverseRotation = new Matrix(new double[][]{
				{	cosA,	sinA	},
				{	-sinA,	cosA	}
		});
		Matrix rotation = new Matrix(new double[][]{
				{	cosA,	-sinA	},
				{	sinA,	cosA	}
		});
		double wheelPosition = (trackLength + trackWidth) * 0.5;
		Matrix inverseKinematics = new Matrix(new double[][]{
				{	1,	-1,	-wheelPosition	},
				{	1,	1,	-wheelPosition	},
				{	1,	-1,	wheelPosition	},
				{	1,	1,	wheelPosition	}
		});
		double[] movementVector = new Matrix(new double[][]{
				{targetLocation.getX()},
				{targetLocation.getY()}
		}).mul(reverseRotation).transpose().getData()[0];
		double theta = Math.acos(Math.max(
				1 - Math.hypot(movementVector[0], movementVector[1]) / (2.0 * r * r),
				-1
		));
		double xm = r * theta * Math.cos(theta / 2.0);
		double ym = -r * theta * Math.sin(theta / 2.0);
		double[] robotMovement = new Matrix(new double[][]{ {xm}, {ym} })
				.mul(rotation).transpose().getData()[0];
		double[] driveBasePowers = new Matrix(new double[][]{
				{ robotMovement[1] },
				{ -robotMovement[0] },
				{ theta }
		}).mul(inverseKinematics).scale(2.0 / wheelDiameter).transpose().getData()[0];
		for (int i = 0; i < 4; i++) {
			driveMotors[i].setPower(driveBasePowers[i]);
		}
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
}
