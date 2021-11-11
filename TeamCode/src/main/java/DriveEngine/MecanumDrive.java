package DriveEngine;

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
	
	
	public MecanumDrive(HardwareMap hw, String fileName) {
		initFromConfig(hw, fileName);
		
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
	}
	
	private void updateLocation() {//  calculate new position from odometry data
		long[] positions = new long[4];
		for (int i = 0; i < 4; i++) {
			positions[i] = driveMotors[i].getCurrentPosition();
		}
		long currentTime = System.nanoTime();
		
		long deltaTime = currentTime - previousTime;
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

	}
	
	public void update() {
		updateLocation();
	}
	
	
	public Location getCurrentLocation() {
		return currentLocation;
	}
}
