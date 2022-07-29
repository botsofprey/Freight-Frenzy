package OpModes.Autonomous.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import UtilityClasses.OutputCapture;

/**
 * <p style="color:#FF0000";>DOES NOT CURRENTLY WORK</p>
 *
 * This is a concept for an autonomous that replays what a driver did in a tele-op.
 * A recording class is played that saves all motor powers at every frame
 * and stores them in a file on the control hub's drive.
 * This op-mode would then read all motor powers from that file
 * and apply them at the recorded times to replay what the driver did.
 *
 * @author Alex Prichard
 */
@Disabled
@Autonomous(name="Replay", group="Autonomous")
public class Replay extends LinearOpMode {
	@Override
	public void runOpMode() throws InterruptedException {
		OutputCapture capture = new OutputCapture(hardwareMap);
		capture.retrieve("Capture.bin");
		
		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();
		
		capture.replayTimeSteps(this);
	}
}
