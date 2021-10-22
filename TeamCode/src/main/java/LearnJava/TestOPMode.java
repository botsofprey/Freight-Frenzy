package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.Controller;

@TeleOp(name = "Test Op Mode")
public class TestOPMode extends LinearOpMode {

	public void runOpMode(){
		RobotArm robotArm = new RobotArm(this.hardwareMap);
		Controller controller = new Controller(gamepad1);

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		while (opModeIsActive()) {
			if (controller.aPressed) {
				robotArm.setPosition(90);
			} else if (controller.bPressed) {
				robotArm.setPosition(180);
			}

			robotArm.update();
			controller.update();
		}
	}
}
