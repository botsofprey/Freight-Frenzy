package DriveEngine;

import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.Location;

public class NewMecanumDrive {
	private MotorController[] motors = new MotorController[4];
	private static final String[] MOTOR_NAMES = {
			"frontLeftDriveMotor",
			"backLeftDriveMotor",
			"backRightDriveMotor",
			"frontRightDriveMotor"
	};

	public NewMecanumDrive(HardwareMap hw, String fileName, Location startLocation) {

	}
}
