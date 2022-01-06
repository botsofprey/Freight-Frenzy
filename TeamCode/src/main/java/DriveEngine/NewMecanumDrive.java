package DriveEngine;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import java.util.List;

import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.JSONReader;
import UtilityClasses.Location;
import UtilityClasses.PIDController;

/**
 * @author Alex Prichard
 * @version 1.0
 * @since 5/1/2022
 * This class is used to follow a Trajectory generated by the TrajectoryBuilder class.
 */
public class NewMecanumDrive {
	private MotorController[] motors = new MotorController[4];
	private static final String[] MOTOR_NAMES = {
			"frontLeftDriveMotor",
			"backLeftDriveMotor",
			"backRightDriveMotor",
			"frontRightDriveMotor"
	};
	
	private MotorController[] driveMotors = new MotorController[4];
	private long previousTime;
	
	private LinearOpMode mode;
	
	private boolean slowMode;
	private boolean fastMode;
	
	private double Kt;
	private double R;
	private double Kv;
	
	private Localizer localizer;
	private Location currentLocation;
	private Trajectory currentTrajectory;
	private boolean currentlyMoving;
	private long startTime;
	
	private PIDCoefficients coefficients = new PIDCoefficients(0.1, 0.025, 0.05);
	private PIDCoefficients headingCoefficients =
			new PIDCoefficients(0.01, 0.005, 0.005);
	private PIDController xController = new PIDController(coefficients);
	private PIDController yController = new PIDController(coefficients);
	private PIDController hController = new PIDController(headingCoefficients);

	public NewMecanumDrive(HardwareMap hw, String fileName,
	                       Location startLocation, LinearOpMode m) {
		mode = m;
		
		initFromConfig(hw, fileName);
		
		slowMode = false;
		fastMode = false;
		
		currentlyMoving = false;
		currentLocation = startLocation;
		previousTime = System.nanoTime();
	}
	
	private void initFromConfig(HardwareMap hw, String fileName) {
		JSONReader reader = new JSONReader(hw, fileName);
		for (int i = 0; i < 4; i++) {
			String motorName = reader.getString(MOTOR_NAMES[i] + "Name");
			driveMotors[i] = new MotorController(hw, motorName, mode, true);
			driveMotors[i].setDirection(
					reader.getString(MOTOR_NAMES[i] + "Direction").equals("forward") ?
							DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE
			);
			driveMotors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
			driveMotors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
			driveMotors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		}
		
		JSONReader motorReader = new JSONReader(hw, reader.getString("driveMotorFile"));
		double stallCurrent = motorReader.getDouble("stall_current");
		Kt = motorReader.getDouble("stall_torque") / stallCurrent;
		R = 12 / stallCurrent;
		Kv = motorReader.getDouble("max_rpm") /
				(12 - motorReader.getDouble("no_load_current") * R);
	}
	
	public void followTrajectory(Trajectory trajectory) {
		currentTrajectory = trajectory;
		currentlyMoving = true;
	}
	
	private void updateLocation() {
		localizer.updateLocation();
		currentLocation = localizer.getCurrentLocation();
	}
	
	public void move(double x, double y, double h) {
	
	}
	
	public void moveTrueNorth(double x, double y, double h) {
	
	}
	
	private void calculateMovement() {
		double time = (System.nanoTime() - previousTime) / 1_000_000_000.0;
		TrajectoryPoint point = currentTrajectory.getMotion(time);
		
		xController.setTargetPoint(point.x);
		yController.setTargetPoint(point.y);
		hController.setTargetPoint(point.h);
		double x = xController.calculateAdjustment(currentLocation.getX());
		double y = yController.calculateAdjustment(currentLocation.getY());
		double h = hController.calculateAdjustment(currentLocation.getHeading());
		
		moveTrueNorth(point.vx + x, point.vy + y, point.vh + h);
	}
	
	public void update() {
		updateLocation();
		if (currentlyMoving) {
			calculateMovement();
		}
	}
}
