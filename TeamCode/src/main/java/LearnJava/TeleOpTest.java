package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import UtilityClasses.Controller;

@TeleOp(name = "cheeseburger", group = "TeleOp")
public class TeleOpTest extends LinearOpMode {
	DcMotor leftMotor;
	DcMotor rightMotor;
	DcMotor spinMotor;

	Controller controller1;

	@Override
	public void runOpMode() throws InterruptedException {
		leftMotor = hardwareMap.get(DcMotor.class, "left_motor");
		rightMotor = hardwareMap.get(DcMotor.class, "right_motor");
		spinMotor = hardwareMap.get(DcMotor.class, "Carousel");

		leftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
		rightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
		spinMotor.setDirection(DcMotorSimple.Direction.FORWARD);

		controller1 = new Controller(gamepad1);

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		while (opModeIsActive()) {
			controller1.update();

			double power = controller1.leftStick.y;
			double turn = controller1.rightStick.x;
			leftMotor.setPower(power + turn);
			rightMotor.setPower(power - turn);

			if (controller1.aHeld) {
				spinMotor.setPower(1);
			}
			else {
				spinMotor.setPower(0);
			}

			telemetry.update();
		}
	}
}
