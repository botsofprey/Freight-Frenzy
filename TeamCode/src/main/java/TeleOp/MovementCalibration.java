package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import DriveEngine.MecanumDrive;
import UtilityClasses.Location;

@TeleOp(name="MovementCalibration")
public class MovementCalibration extends LinearOpMode {
	@Override
	public void runOpMode() throws InterruptedException {
		MecanumDrive drive = new MecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), false, this, true);
		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();
		drive.update();

		while(opModeIsActive()) {
			drive.moveRobot(gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x);
			telemetry.addData("Velocity", drive.getCurrentVelocity());
			drive.update();
			telemetry.update();
		}
	}
}
