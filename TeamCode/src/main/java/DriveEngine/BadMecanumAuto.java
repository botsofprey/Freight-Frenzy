package DriveEngine;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Deprecated
public class BadMecanumAuto {
	private static final double TICKS_PER_INCH = 560.0 / ((4 * Math.PI) * (Math.sqrt(2)/2));
	private LinearOpMode mode;

	public static final int FRONT_LEFT_DRIVE_MOTOR = 0;
	public static final int FRONT_RIGHT_DRIVE_MOTOR = 1;
	public static final int BACK_RIGHT_DRIVE_MOTOR = 2;
	public static final int BACK_LEFT_DRIVE_MOTOR = 3;

	private DcMotorEx[] motors = new DcMotorEx[4];

	private BNO055IMU imu;
	private double angle;

	public BadMecanumAuto(HardwareMap hw, LinearOpMode mode) {
		this.mode = mode;

		for (int i = 0; i < motors.length; i++) {
			motors[i] = hw.get(DcMotorEx.class, "");
			motors[i].setDirection(DcMotorSimple.Direction.FORWARD);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		}

		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
		parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
		parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
		parameters.loggingEnabled = true;
		parameters.loggingTag = "IMU";
		parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

		imu = hw.get(BNO055IMU.class, "");
		imu.initialize(parameters);
		angle = 0;
	}

	public void moveForward(double power) {
		for (int i = 0; i < motors.length; i++) {
			motors[i].setPower(power);
		}
	}

	public void moveBackward(double power) {
		moveForward(-power);
	}

	public void moveRight(double power) {
		double[] powers = {
				power,
				-power,
				power,
				-power,
		};
		for (int i = 0; i < motors.length; i++) {
			motors[i].setPower(powers[i]);
		}
	}

	public void moveLeft(double power) {
		moveRight(-power);
	}

	public void brake() {
		for (int i = 0; i < motors.length; i++) {
			motors[i].setPower(0);
		}
	}

	public void turnLeft(double power) {
		double[] powers = {
				-power,
				power,
				power,
				-power,
		};
		for (int i = 0; i < motors.length; i++) {
			motors[i].setPower(powers[i]);
		}
	}

	public void turnRight(double power) {
		turnLeft(-power);
	}

	public void move(double inches, double inchesPerSecond) {

		for (int i = 0; i < motors.length; i++) {
			motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		}

		for (int i = 0; i < motors.length; i++) {
			motors[i].setTargetPosition((int)(inches * TICKS_PER_INCH));
		}

		for (int i = 0; i < motors.length; i++) {
			motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
		}

		for (int i = 0; i < motors.length; i++) {
			motors[i].setVelocity(inchesPerSecond * TICKS_PER_INCH);
		}

		for (int i = 0; i < motors.length; i++) {
			while (motors[i].isBusy() && mode.opModeIsActive()) {}
		}

		mode.sleep(250);
	}
}
