package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import DriveEngine.TeleOpMotorDriver;
import Subsystems.Carousel;
import Subsystems.Intake;
import Subsystems.Lift;
import UtilityClasses.Controller;

@TeleOp(name="TeleOpTest", group="TeleOp")
public class TeleOpTest extends LinearOpMode {
	private TeleOpMotorDriver drive;
	private Lift lift;
	private Intake intake;
	private Carousel carousel;
	private Controller controller1;

	private static final boolean throwErrors = true;


	@Override
	public void runOpMode() throws InterruptedException {
		try {
			drive = new TeleOpMotorDriver(hardwareMap, "RobotConfig.json",
					false, this, throwErrors);
			lift = new Lift(hardwareMap, this, throwErrors);
			intake = new Intake(hardwareMap, this, throwErrors);
			carousel = new Carousel(hardwareMap, this, throwErrors);
			controller1 = new Controller(gamepad1);

			telemetry.addData("Status", "Initialized");
			telemetry.update();
			waitForStart();

			lift.zeroSlider();

			while (opModeIsActive()) {
				controller1.update();

				drive.moveRobot(controller1.leftStick.x, controller1.leftStick.y,
						controller1.rightStick.x);

				if (controller1.upPressed) {
					lift.positionUp();
				}
				if (controller1.downPressed) {
					lift.positionDown();
				}

				if (controller1.rightTriggerPressed) {
					lift.up();
				}
				if (controller1.leftTriggerPressed) {
					lift.down();
				}
				if (controller1.rightTriggerReleased || controller1.leftTriggerReleased) {
					lift.brake();
				}

				if (controller1.aPressed) {
					intake.switchState(Intake.INTAKE_BUTTON);
				}

				if (controller1.bPressed) {
					intake.switchState(Intake.OUTTAKE_BUTTON);
				}

				if (controller1.yPressed) {
					lift.dropFreight();
				}

				if (controller1.xHeld) {
					carousel.rotate();
				} else {
					carousel.stop();
				}

				if (controller1.startPressed) {
					drive.toggleSlowMode();
				}

				lift.update();

				telemetry.update();
			}
		} finally {
			drive.moveRobot(0, 0, 0);
			lift.brake();
			carousel.stop();
			intake.brake();
		}
	}
}
