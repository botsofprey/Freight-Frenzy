package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.MecanumDrive;
import UtilityClasses.Location;

public class MovementCalibration extends LinearOpMode {
	@Override
	public void runOpMode() throws InterruptedException {
		MecanumDrive drive = new MecanumDrive(hardwareMap, "robot.json",
				new Location(0, 0, 0), false, this, true);
		drive.update();
		telemetry.addData("Velocity", drive::getCurrentVelocity);
		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		while(opModeIsActive()) {
			drive.moveRobot(gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x);
			drive.update();
			telemetry.update();
		}
	}
}
