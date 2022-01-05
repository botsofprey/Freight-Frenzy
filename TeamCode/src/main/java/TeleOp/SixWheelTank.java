package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="6 Wheel Tank", group="Tank")
public class SixWheelTank extends LinearOpMode {
	private String[] names = new String[] {
			"frontLeft",
			"backLeft",
			"backRight",
			"frontRight"
	};
	private DcMotorSimple.Direction[] directions = new DcMotorSimple.Direction[] {
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.FORWARD,
			DcMotorSimple.Direction.FORWARD
	};

	private DcMotor[] motors = new DcMotor[4];

	@Override
	public void runOpMode() throws InterruptedException {
		for (int i = 0; i < 4; i++) {
			motors[i] = hardwareMap.get(DcMotor.class, names[i]);
			motors[i].setDirection(directions[i]);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		}

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		while (opModeIsActive()) {
			double forward = -gamepad1.left_stick_y;
			double turn = gamepad1.right_stick_x;
			double left = forward + turn;
			double right = forward - turn;
			if (gamepad1.left_trigger > 0.1) {
				left /= 3;
				right /= 3;
			}
			double[] powers = new double[] {
					left,
					left,
					right,
					right
			};
			normalize(powers);
			for (int i = 0; i < 4; i++) {
				motors[i].setPower(powers[i]);
			}
		}
	}

	private void normalize(double[] powers) {
		double max = 1;
		for (int i = 0; i < powers.length; i++) {
			max = Math.max(max, Math.abs(powers[i]));
		}
		for (int i = 0; i < powers.length; i++) {
			powers[i] /= max;
		}
	}
}
