package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import DriveEngine.MecanumDrive;
import DriveEngine.TeleOpMotorDriver;
import Subsystems.Carousel;
import Subsystems.Intake;
import Subsystems.Lift;
import UtilityClasses.Controller;
import UtilityClasses.Location;

@TeleOp(name="TeleOpTest", group="TeleOp")
public class TeleOpTest extends LinearOpMode {
	private Lift lift;
	private Intake intake;
	private Carousel carousel;
	private Controller controller1;
	private Controller controller2;
	private MecanumDrive drive;

	private static final boolean throwErrors = true;


	@Override
	public void runOpMode() throws InterruptedException {
		try {
			lift = new Lift(hardwareMap, this, throwErrors);
			intake = new Intake(hardwareMap, this, throwErrors);
			carousel = new Carousel(hardwareMap, this, throwErrors);
			drive = new MecanumDrive(hardwareMap, "RobotConfig.json",
					new Location(0, 0, 0), this, throwErrors);
			controller1 = new Controller(gamepad1);
			controller2 = new Controller(gamepad1);

			telemetry.addData("Status", "Initialized");
			telemetry.update();
			waitForStart();

			//lift.zeroSlider();
			drive.update();

			while (opModeIsActive()) {
				controller1.update();
				controller2.update();

				drive.moveRobot(controller1.leftStick.x, controller1.leftStick.y,
						-controller1.rightStick.x);

				if (controller2.rightTriggerHeld && !controller2.leftTriggerHeld) {
					lift.up();
				}
				else if (controller2.leftTriggerHeld && !controller2.rightTriggerHeld) {
					lift.down();
				}
				else {
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
				drive.update();
				telemetry.addData("Location", drive.getCurrentLocation());

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
