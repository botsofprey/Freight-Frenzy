package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import DriveEngine.HDriveMotorDriver;

@TeleOp(name = "H-Drive Test", group = "TeleOp")
//@Disabled
public class HDriveTest extends LinearOpMode {
	HDriveMotorDriver driveBase;
	
	@Override
	public void runOpMode() {
		driveBase = new HDriveMotorDriver(hardwareMap, false);
		
		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();
		
		while (opModeIsActive()) {
			driveBase.moveRobot(gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x);
		}
	}
}
