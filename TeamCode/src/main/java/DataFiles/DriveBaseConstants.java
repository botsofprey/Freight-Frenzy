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
	
	public static final double TRACK_WIDTH = 14.0;
	public static final double WHEELBASE = 14.0;
	public static final double WHEEL_RADIUS = 2.0;
	
	public static final double ENCODER_LATERAL_DISTANCE = 9.875;
	public static final double ENCODER_FORWARD_OFFSET = 5.0;
	
	public static final int LEFT_ENCODER_DIRECTION = -1;
	public static final int RIGHT_ENCODER_DIRECTION = 1;
	public static final int CENTER_ENCODER_DIRECTION = -1;
	public static final String LEFT_ENCODER_NAME = "backRightDriveMotor";
	public static final String RIGHT_ENCODER_NAME = "frontRightDriveMotor";
	public static final String CENTER_ENCODER_NAME = "frontLeftDriveMotor";
}
