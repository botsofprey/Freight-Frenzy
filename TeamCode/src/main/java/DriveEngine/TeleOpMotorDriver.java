package DriveEngine;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.JSONReader;
import UtilityClasses.Vec2d;

public class TeleOpMotorDriver {
	private static final int FRONT_LEFT_DRIVE = 0;
	private static final int FRONT_RIGHT_DRIVE = 1;
	private static final int BACK_RIGHT_DRIVE = 2;
	private static final int BACK_LEFT_DRIVE = 3;
	private static final double SQRT_ONE_HALF = Math.sqrt(2) / 2.0;
	private static final String[] MOTOR_NAMES = {
			"frontLeftDriveMotor",
			"backLeftDriveMotor",
			"backRightDriveMotor",
			"frontRightDriveMotor"
	};
	
	private boolean trueNorth;
	private BNO055IMU imu;
	private MotorController[] motors = new MotorController[4];
	private boolean slowMode;
	
	public TeleOpMotorDriver(HardwareMap hw, String fileName, boolean trueNorthEnabled,
							 LinearOpMode mode, boolean errors) {
		slowMode = false;
		JSONReader reader = new JSONReader(hw, fileName);
		for (int i = 0; i < 4; i++) {
			String motorName = reader.getString(MOTOR_NAMES[i] + "Name");
			motors[i] = new MotorController(hw, motorName, mode, errors);
			motors[i].setDirection(
					reader.getString(MOTOR_NAMES[i] + "Direction").equals("forward") ?
							DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE
			);
			motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
			motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		}
		
		trueNorth = trueNorthEnabled;
		
		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
		parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
		parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
		parameters.loggingEnabled = true;
		parameters.loggingTag = "IMU";
		parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
		
		imu = hw.get(BNO055IMU.class, "imu");
		imu.initialize(parameters);
	}

	public void toggleTrueNorth() {
		trueNorth = !trueNorth;
	}

	public void toggleSlowMode() { slowMode = !slowMode; }
	public void slowMode() { slowMode = true; }
	public void noSlowMode() { slowMode = false; }
	
	public void moveRobot(double x, double y, double a) {
		if (slowMode) {
			x /= 3;
			y /= 3;
			a /= 3;
		}
		a *= -1;
		if (trueNorth) {
			double heading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX,
					AngleUnit.DEGREES).firstAngle;
			Vec2d movementVector = new Vec2d(x, y);
			movementVector.convertToAngleMagnitude();
			movementVector.angle -= heading;
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
		double[] normalizedPowers = normalizeValues(powers);
		for (int i = 0; i < normalizedPowers.length; i++) {
			motors[i].setPower(normalizedPowers[i]);
		}
	}
	
	private double[] normalizeValues(double[] powers) {
		double scaleFactor = 1;
		for (double power : powers) {
			scaleFactor = Math.max(Math.abs(power), scaleFactor);
		}
		for (int i = 0; i < powers.length; i++) {
			powers[i] /= scaleFactor;
		}
		return powers;
	}
	
	public void close() {
		imu.close();
	}
}
