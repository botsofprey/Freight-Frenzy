package DriveEngine;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import UtilityClasses.Vec2d;

public class TeleOpMotorDriver {
	private static final int FRONT_LEFT_DRIVE = 0;
	private static final int FRONT_RIGHT_DRIVE = 1;
	private static final int BACK_RIGHT_DRIVE = 2;
	private static final int BACK_LEFT_DRIVE = 3;
	private static final double SQRT_ONE_HALF = Math.sqrt(2) / 2.0;
	private static final String[] MOTOR_NAMES = {};
	private static final DcMotorSimple.Direction[] MOTOR_DIRECTIONS = {};
	
	private boolean trueNorth;
	private BNO055IMU imu;
	private DcMotor[] motors = new DcMotor[4];
	
	public TeleOpMotorDriver(HardwareMap hw, boolean trueNorthEnabled) {
		for (int i = 0; i < 4; i++) {
			motors[i] = hw.get(DcMotor.class, MOTOR_NAMES[i]);
			motors[i].setDirection(MOTOR_DIRECTIONS[i]);
		}
		
		trueNorth = trueNorthEnabled;
		
		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
		parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
		parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
		parameters.loggingEnabled = true;
		parameters.loggingTag = "IMU";
		parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
		
		imu = hw.get(BNO055IMU.class, "");
		imu.initialize(parameters);
	}

	public void toggleTrueNorth() {
		trueNorth = !trueNorth;
	}
	
	public void moveRobot(double x, double y, double a) {
		a *= -0.5;
		if (trueNorth) {
			double heading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
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
				leftVector + a,
				rightVector + a,
				leftVector - a
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
