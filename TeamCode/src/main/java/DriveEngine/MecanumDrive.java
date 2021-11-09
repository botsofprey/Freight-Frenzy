package DriveEngine;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.JSONReader;
import UtilityClasses.Location;

public class MecanumDrive {
	private static final double FRONT_LEFT_DRIVE_MOTOR = 0;
	private static final double FRONT_RIGHT_DRIVE_MOTOR = 1;
	private static final double BACK_RIGHT_DRIVE_MOTOR = 2;
	private static final double BACK_LEFT_DRIVE_MOTOR = 3;
	private static final String[] MOTOR_NAMES = {
			"frontLeftDriveMotor",
			"frontRightDriveMotor",
			"backRightDriveMotor",
			"backLeftDriveMotor"
	};
	
	
	private volatile DcMotorEx[] driveMotors = new DcMotorEx[4];
	
	private volatile Location currentLocation;
	
	private long[] previousPositions;
	private long previousTime;
	
	private double encoderCPR;
	private double wheelDiameter;
	private double trackWidth;
	private double trackLength;
	
	
	public MecanumDrive(HardwareMap hw, String fileName) {
		initFromConfig(hw, fileName);
		
		previousPositions = new long[]{0, 0, 0, 0};
		
		new Thread(() -> {
			updateLocation();
			
		}).start();
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
	
	private void updateLocation() {
		long[] positions = new long[4];
		for (int i = 0; i < 4; i++) {
			positions[i] = driveMotors[i].getCurrentPosition();
		}
		long currentTime = System.nanoTime();
		for (int i = 0; i < 4; i++) {
			positions[i] -= previousPositions[i];
			previousPositions[i] += positions[i];
		}
	}
}
