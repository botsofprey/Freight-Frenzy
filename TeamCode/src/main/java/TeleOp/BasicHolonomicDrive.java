package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class BasicHolonomicDrive extends LinearOpMode {
	@Override
	public void runOpMode() throws InterruptedException {
		DcMotor flMotor = hardwareMap.get(DcMotor.class, "flMotor");
		DcMotor blMotor = hardwareMap.get(DcMotor.class, "blMotor");
		DcMotor brMotor = hardwareMap.get(DcMotor.class, "brMotor");
		DcMotor frMotor = hardwareMap.get(DcMotor.class, "frMotor");
		flMotor.setDirection(DcMotorSimple.Direction.REVERSE);
		blMotor.setDirection(DcMotorSimple.Direction.REVERSE);
		
		telemetry.addData("Status", "Initialized");
		telemetry.update();
		
		while (opModeIsActive()) {
			double x = gamepad1.left_stick_x;
			double y = gamepad1.left_stick_y;
			double a = gamepad1.right_stick_x;
			
			flMotor.setPower(x - y + a);
			blMotor.setPower(-x - y + a);
			brMotor.setPower(x - y - a);
			frMotor.setPower(-x - y - a);
		}
	}
}
