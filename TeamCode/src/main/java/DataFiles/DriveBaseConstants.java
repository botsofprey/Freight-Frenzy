package DataFiles;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class DriveBaseConstants {
	public static final String[] MOTOR_NAMES = {
			"frontLeftDriveMotor",
			"frontRightDriveMotor",
			"backRightDriveMotor",
			"backLeftDriveMotor"
	};
	public static final DcMotorSimple.Direction[] MOTOR_DIRECTIONS = {
			DcMotorSimple.Direction.FORWARD,
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.FORWARD
	};
	
	public static final String IMU_NAME = "imu";
	
	public static final double TRACK_WIDTH = 14;
	public static final double WHEELBASE = 14;
	public static final double WHEEL_RADIUS = 2;
}
