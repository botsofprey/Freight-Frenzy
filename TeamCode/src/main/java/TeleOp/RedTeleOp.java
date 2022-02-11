package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import DriveEngine.MecanumDrive;
import Subsystems.Intake;
import Subsystems.Lift;
import Subsystems.MotorCarousel;
import UtilityClasses.Controller;
import UtilityClasses.Location;

@TeleOp(name="RedTeleOp", group="TeleOp")
public class RedTeleOp extends LinearOpMode {
	private Lift lift;
	private Intake intake;
	private MotorCarousel carousel;
	private Controller controller1;
	private Controller controller2;
	private MecanumDrive drive;

	private static final boolean throwErrors = true;


	@Override
	public void runOpMode() throws InterruptedException {
		lift = new Lift(hardwareMap, this, throwErrors);
		intake = new Intake(hardwareMap, this, throwErrors);
		carousel = new MotorCarousel(hardwareMap, this);
		drive = new MecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), false, this, throwErrors);
		controller1 = new Controller(gamepad1);
		controller2 = new Controller(gamepad2);

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		double[] cycleTimes = new double[32];
		int cycle = 0;
		long previousTime = System.currentTimeMillis();

		while (opModeIsActive()) {
			controller1.update();
			controller2.update();

			if (controller1.rightTriggerHeld) {
				double power = 1.0 / 3;
				//drive.moveRobot(0, power, Math.sqrt(0.5) * power);
				drive.moveRobot(0, power, 0);
			}
			else {
				drive.moveRobot(controller1.leftStick.x, -controller1.leftStick.y,
						-controller1.rightStick.x);
			}

			if (controller2.rightTriggerHeld && !controller2.leftTriggerHeld) {
				lift.upAnalog(controller2.rightTrigger);
			}
			else if (controller2.leftTriggerHeld && !controller2.rightTriggerHeld) {
				lift.downAnalog(controller2.leftTrigger);
			}
			else {
				lift.brake();
			}

			if (controller2.upPressed) {
				lift.positionUp();
			}
			if (controller2.rightPressed) {
				lift.positionMiddle();
			}
			if (controller2.downPressed) {
				lift.positionDown();
			}

			if (controller2.aPressed) {
				intake.switchState(Intake.INTAKE_BUTTON);
			}

			if (controller2.bPressed) {
				intake.switchState(Intake.OUTTAKE_BUTTON);
			}

			if (controller1.yPressed) {
				lift.autoDrop();
				intake.resetLEDs();
			}

			if (controller2.rightBumperPressed) {
				carousel.TELEOP_POWER += 0.01;
			}
			if (controller2.leftBumperPressed) {
				carousel.TELEOP_POWER -= 0.01;
			}
			if (controller2.xPressed) {
				carousel.redEndgame();
			}

			if (controller1.leftTriggerPressed) {
				drive.slowMode();
			}
			if (controller1.leftTriggerReleased) {
				drive.noSlowMode();
			}
			long time = System.currentTimeMillis();

			lift.update(time);
			intake.update(time);
			carousel.update(time);

			cycleTimes[cycle] = 1000.0 / (time - previousTime);
			previousTime = time;
			cycle++;
			cycle %= cycleTimes.length;
			double acc = 0;
			for (double elem : cycleTimes) {
				acc += elem;
			}
			acc /= cycleTimes.length;
			telemetry.addData("Cycles per second", (int)acc);


			telemetry.update();
		}
	}
}