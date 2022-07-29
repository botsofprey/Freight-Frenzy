package DriveEngine.Deprecated;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.JSONReader;

@Deprecated
public class OldMecanumDriveClass {
	private static final String[] MOTOR_NAMES = {
			"frontLeftDriveMotor",
			"backLeftDriveMotor",
			"backRightDriveMotor",
			"frontRightDriveMotor"
	};
	
	private static final double SQRT_ONE_HALF = Math.sqrt(0.5);
	
	private volatile MotorController[] driveMotors = new MotorController[4];
	
	private boolean slowMode;
	
	public OldMecanumDriveClass(HardwareMap hw, String fileName) {
		initFromConfig(hw, fileName);

		List<LynxModule> allHubs = hw.getAll(LynxModule.class);
		for (LynxModule hub : allHubs) {
			hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
		}

		slowMode = false;
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

	private void adjustWheelPowers(double[] wheelPowers) {
		double maxPower = 1.0;
		for (int i = 0; i < 4; i++) {
			maxPower = Math.max(maxPower, Math.abs(wheelPowers[i]));
		}
		for (int i = 0; i < 4; i++) {
			wheelPowers[i] /= maxPower;
		}
	}
	
	public void moveRobot(double x, double y, double a) {
		if (slowMode) {
			x /= 3;
			y /= 3;
			a /= 3;
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
}
