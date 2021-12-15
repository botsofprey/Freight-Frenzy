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
	private Controller controller2;

	private static final boolean throwErrors = true;


	@Override
	public void runOpMode() throws InterruptedException {
		try {
			drive = new TeleOpMotorDriver(hardwareMap, "RobotConfig.json",
					true, this, throwErrors);
			lift = new Lift(hardwareMap, this, throwErrors);
			intake = new Intake(hardwareMap, this, throwErrors);
			carousel = new Carousel(hardwareMap, this, throwErrors);
			controller1 = new Controller(gamepad1);
			controller2 = new Controller(gamepad2);

			telemetry.addData("Status", "Initialized");
			telemetry.update();
			waitForStart();

			//lift.zeroSlider();

			while (opModeIsActive()) {
				controller1.update();
				controller2.update();

				drive.moveRobot(controller1.leftStick.x, controller1.leftStick.y,
						controller1.rightStick.x);

				if (controller2.rightTriggerPressed) {
					lift.up();
				}
				if (controller2.leftTriggerPressed) {
					lift.down();
				}
				if (controller2.rightTriggerReleased && !controller2.leftTriggerPressed ||
						controller2.leftTriggerReleased && !controller2.rightTriggerPressed) {
					lift.brake();
				}

				if (controller2.aPressed) {
					intake.switchState(Intake.INTAKE_BUTTON);
				}

				if (controller2.bPressed) {
					intake.switchState(Intake.OUTTAKE_BUTTON);
				}

				if (controller1.yPressed) {
					lift.dropFreight();
				}

				if (controller2.xHeld) {
					carousel.rotate();
				} else {
					carousel.stop();
				}

				if (controller1.leftTriggerPressed) {
					drive.slowMode();
				}
				if (controller1.leftTriggerReleased) {
					drive.noSlowMode();
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
