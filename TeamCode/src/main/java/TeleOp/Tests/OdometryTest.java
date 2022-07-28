package TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import UtilityClasses.Controller;

@TeleOp(name="Odometry TeleOp", group="TeleOp")
@Disabled
public class OdometryTest extends LinearOpMode {
	private static final String[] MOTOR_NAMES = {
			"flMotor",
			"blMotor",
			"brMotor",
			"frMotor"
	};
	private static final DcMotorSimple.Direction[] DIRECTIONS = {
			DcMotorSimple.Direction.FORWARD,
			DcMotorSimple.Direction.FORWARD,
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.REVERSE
	};
	private DcMotor[] motors = new DcMotor[4];

	@Override
	public void runOpMode() throws InterruptedException {
		for (int i = 0; i < 4; i++) {
			motors[i] = hardwareMap.get(DcMotor.class, MOTOR_NAMES[i]);
			motors[i].setDirection(DIRECTIONS[i]);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		}

		Controller controller = new Controller(gamepad1);

		double x = 0, y = 0, h = 0;

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		while (opModeIsActive()) {
			controller.update();

			double xp = +controller.leftStick.y, yp = -controller.leftStick.x,
					ap = -controller.rightStick.x;
			double[] powers = {
					xp - yp - ap,
					xp + yp - ap,
					xp - yp + ap,
					xp + yp + ap
			};
			double max = 1;
			for (double power : powers) {
				max = Math.max(max, power);
			}
			for (int i = 0; i < 4; i++) {
				motors[i].setPower(powers[i] / max);
			}

			double front = motors[3].getCurrentPosition();
			double left = motors[1].getCurrentPosition();
			double right = motors[2].getCurrentPosition();
			double inchesPerTick = Math.PI * 1.5 / 2000.0;
			telemetry.addData("raw front", front);
			front *= inchesPerTick;
			left *= inchesPerTick;
			right *= inchesPerTick;

			telemetry.addData("front", front);
			telemetry.addData("left", left);
			telemetry.addData("right", right);
			telemetry.update();
		}
	}
}
