package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import UtilityClasses.OutputCapture;

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
