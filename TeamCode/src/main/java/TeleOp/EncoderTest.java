package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name="Encoder Test", group="test")
public class EncoderTest extends LinearOpMode {
	@Override
	public void runOpMode() throws InterruptedException {
		String[] motorNames = {
				"flMotor",
				"blMotor",
				"brMotor",
				"frMotor"
		};
		DcMotorEx[] motors = new DcMotorEx[4];
		for (int i = 0; i < 4; i++) {
			motors[i] = hardwareMap.get(DcMotorEx.class, motorNames[i]);
			motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		}

		waitForStart();

		while (opModeIsActive()) {
			for (int i = 0; i < 4; i++) {
				telemetry.addData(motorNames[i], motors[i].getCurrentPosition());
			}
			telemetry.update();
		}
	}
}
