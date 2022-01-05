package TeleOp;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

@TeleOp (name = "Led test", group = "teleop")
public class LedLights extends LinearOpMode {

	RevBlinkinLedDriver intakeLed;
	RevBlinkinLedDriver liftLed;

	 private static final RevBlinkinLedDriver.BlinkinPattern
			 BUCKET_EMPTY = RevBlinkinLedDriver.BlinkinPattern.BLACK,
	INTAKE_LOADED = RevBlinkinLedDriver.BlinkinPattern.YELLOW,
	BUCKET_LOADED = RevBlinkinLedDriver.BlinkinPattern.GREEN
	;

	@Override
	public void runOpMode() throws InterruptedException {
		intakeLed = hardwareMap.get(RevBlinkinLedDriver.class, "Led Indicate");
		intakeLed.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
		liftLed = hardwareMap.get(RevBlinkinLedDriver.class, "Led Indicate");
		liftLed.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
		waitForStart();
	}
}
