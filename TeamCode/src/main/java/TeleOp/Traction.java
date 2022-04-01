package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import DriveEngine.NewMecanumDrive;
import UtilityClasses.Controller;
import UtilityClasses.Location;

@TeleOp(name="Traction")
@Disabled
public class Traction extends LinearOpMode {
	@Override
	public void runOpMode() throws InterruptedException {
		NewMecanumDrive drive = new NewMecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), this);
		Controller controller = new Controller(gamepad1);

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		double power = 0;
		while (opModeIsActive()) {
			controller.update();

			if (controller.aPressed) {
				power += 0.05;
			}

			drive.rawMove(0, power, 0);

			telemetry.addData("Power", power);
			telemetry.update();
		}
	}
}
