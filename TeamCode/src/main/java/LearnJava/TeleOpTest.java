package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Subsystems.Carousel;
import Subsystems.Lift;
import UtilityClasses.Controller;

@TeleOp(name = "cheeseburger", group = "TeleOp")
public class TeleOpTest extends LinearOpMode {
	private Controller controller1;
	private Carousel roundabout;
	private Lift lift;
	private TankDrive drive;


	@Override
	public void runOpMode() throws InterruptedException {
		controller1 = new Controller(gamepad1);
		roundabout = new Carousel(hardwareMap, this);
		lift = new Lift(hardwareMap, this);
		drive = new TankDrive(hardwareMap, this);


		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		while (opModeIsActive()) {
			//drive
			drive.leftMotor.setPower(controller1.leftStick.y + controller1.rightStick.x);
			drive.rightMotor.setPower(controller1.leftStick.y - controller1.rightStick.x);

			if (controller1.aPressed) {
				roundabout.rotate();
			} else if (controller1.aReleased) {
				roundabout.stop();
			}

			if (controller1.leftTriggerHeld){
				lift.moveDown();
			} else if (controller1.rightTriggerHeld) {
				lift.moveUp();
			} else {
				lift.brake();
			}
			telemetry.addData("left trigger", controller1.leftTriggerHeld);
			telemetry.addData("right trigger", controller1.rightTriggerHeld);

			controller1.update();
			telemetry.update();
		}
	}
}
