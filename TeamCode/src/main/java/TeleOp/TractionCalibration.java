package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import DriveEngine.TeleOpMotorDriver;
import UtilityClasses.Controller;

@TeleOp(name="Traction", group="TeleOp")
public class TractionCalibration extends LinearOpMode {
	@Override
	public void runOpMode() throws InterruptedException {
		TeleOpMotorDriver drive = new TeleOpMotorDriver(hardwareMap, "RobotConfig.json",
				false, this, true);
		Controller controller = new Controller(gamepad1);

		telemetry.addData("Status", "Initialized");
		telemetry.update();

		double power = 0;

		while (opModeIsActive()) {
			controller.update();
			telemetry.addData("power", power);

			if (controller.aPressed) {
				power += 0.05;
				drive.moveRobot(0, power, 0);
			}

			telemetry.update();
		}
	}
}
