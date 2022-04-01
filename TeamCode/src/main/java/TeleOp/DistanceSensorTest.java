package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Subsystems.Intake;

@TeleOp(name="Distance Test", group="test")
@Disabled
public class DistanceSensorTest extends LinearOpMode {
	@Override
	public void runOpMode() throws InterruptedException {
		Intake intake = new Intake(hardwareMap, this, true);

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		while (opModeIsActive()) {
			int numMeasurements = 10;
			double avg = 0;
			for (int i = 0; i < numMeasurements; i++) {
				avg += Math.min(intake.getDistance() / numMeasurements, 24.0 / numMeasurements);
			}
			telemetry.addData("Distance", avg);
			telemetry.update();
		}
	}
}
