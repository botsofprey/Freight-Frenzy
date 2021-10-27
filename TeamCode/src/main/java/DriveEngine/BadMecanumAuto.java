package DriveEngine;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import static DataFiles.DriveBaseConstants.*;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import UtilityClasses.PIDController;

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
			motors[i] = hw.get(DcMotorEx.class, MOTOR_NAMES[i]);
			motors[i].setDirection(MOTOR_DIRECTIONS[i]);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		}

		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
		parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
		parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
		parameters.loggingEnabled = true;
		parameters.loggingTag = "IMU";
		parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

		imu = hw.get(BNO055IMU.class, IMU_NAME);
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

	public void turnToAngle(double angle) {
		this.angle = angle;
	}

	public void update() {
		double heading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
		double angleDiff = angle - heading;
		angleDiff *= 0.01;
		double[] powers = {
				-angleDiff,
				angleDiff,
				angleDiff,
				-angleDiff
		};
		double scale = 1;
		for (int i = 0; i < motors.length; i++) {
			powers[i] += motors[i].getPower();
			scale = Math.max(scale, Math.abs(powers[i]));
		}
		for (int i = 0; i < motors.length; i++) {
			powers[i] /= scale;
		}
		for (int i = 0; i < motors.length; i++) {
			motors[i].setPower(powers[i]);
		}
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