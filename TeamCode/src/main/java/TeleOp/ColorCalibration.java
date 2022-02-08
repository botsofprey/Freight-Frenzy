package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Arrays;

import Subsystems.Intake;

@TeleOp(name="Color Calibration", group="test")
public class ColorCalibration extends LinearOpMode {
	private Intake intake;
	
	@Override
	public void runOpMode() throws InterruptedException {
		intake = new Intake(hardwareMap, this, true);
		
		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();
		
		while (opModeIsActive()) {
			int[][] colors = intake.getColor();
			telemetry.addData("A", Arrays.toString(colors[0]));
			telemetry.addData("B", Arrays.toString(colors[1]));
			telemetry.update();
		}
	}
}
