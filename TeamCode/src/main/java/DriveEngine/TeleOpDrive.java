package DriveEngine;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import java.util.List;

import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.JSONReader;
import UtilityClasses.Location;
import UtilityClasses.OldVec2d;

/**
 * This is a simplified movement class for tele-op.
 * It doesn't track location; it only responds to input.
 */
public class TeleOpDrive {
	private static final String[] MOTOR_NAMES = {
			"frontLeftDriveMotor",
			"backLeftDriveMotor",
			"backRightDriveMotor",
			"frontRightDriveMotor"
	};
	private static final double SQRT_ONE_HALF = Math.sqrt(0.5);
	
	private volatile MotorController[] driveMotors = new MotorController[4];
	
	public BNO055IMU imu;
	
	private double speed;
	private boolean trueNorth;
	
	public TeleOpDrive(HardwareMap hw, String configFileName, boolean trueNorthEnabled) {
		initFromConfig(hw, configFileName);
		
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
		
		// this makes the code run faster by reading all motor encoders at once instead of on demand
		// because of the way the control hub works, reading all non i2c ports at once takes the
		// same amount of time as reading one i2c port
		// there is no reason to put this in the drive class as opposed to any other subsystem, but
		// it is likely that every opmode will use a drive class and this puts it just out of sight
		List<LynxModule> allHubs = hw.getAll(LynxModule.class);
		for (LynxModule hub : allHubs) {
			hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
		}
		
		speed = 1;
		trueNorth = trueNorthEnabled;
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
	
	public void move(Location movement) {
		move(movement.getX(), movement.getY(), movement.getHeading());
	}
	
	public void move(double x, double y, double a) {
		x *= speed;
		y *= speed;
		a *= speed;
		
		if (trueNorth) { // rotate movement vector based on robot's orientation
			OldVec2d movementVector = new OldVec2d(x, y);
			movementVector.convertToAngleMagnitude();
			movementVector.angle -= imu.getAngularOrientation(AxesReference.INTRINSIC,
					AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
			movementVector.convertToXY();
			x = movementVector.x;
			y = movementVector.y;
		}
		
		double rightVector = SQRT_ONE_HALF * (x - y);
		double leftVector = SQRT_ONE_HALF * (x + y);
		double[] powers = {
				rightVector - a,
				leftVector - a,
				rightVector + a,
				leftVector + a
		};
		for (int i = 0; i < powers.length; i++) {
			driveMotors[i].setPower(powers[i]);
		}
	}
	
	public void slowMode() { speed = 1.0 / 3.0; }
	public void normalMode() { speed = 1.0; }
	public void fastMode() { speed = 3.0; }
}
