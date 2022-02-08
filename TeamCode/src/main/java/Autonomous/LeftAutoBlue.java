package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.ArrayList;

import DriveEngine.NewMecanumDrive;
import Subsystems.CameraPipeline;
import Subsystems.Carousel;
import Subsystems.Intake;
import Subsystems.Lift;
import Subsystems.MotorCarousel;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.Location;

@Autonomous(name="LeftAutoBlue", group="Competition Autos")
public class LeftAutoBlue extends LinearOpMode {
	private NewMecanumDrive drive;
	private Lift lift;
	private Intake intake;

	private Location shippingHub = new Location(-18, -23, 0);
	private Location warehouseEntrance = new Location(0, 7.5, -90);
	private Location warehouse = new Location(24, 7.5, -90);
	private Location shippingHubCycle = new Location(-18, -21, -90);

	private void grabBlock() {
		intake.intakeNoDelay();
		drive.rawMove(0, -1.0 / 5, 0);
		long time;
		//long end = time + 10000;
		while (opModeIsActive() && intake.moving()) {
			time = System.currentTimeMillis();
			//if (time >= end) break;
			intake.update(time);
			if (!intake.moving()) break;
			drive.updateLocation();
		}
		drive.brake();
	}

	@Override
	public void runOpMode() throws InterruptedException {
		CameraPipeline cameraPipeline = new CameraPipeline(this);
		Camera camera = new Camera(hardwareMap, "Webcam 1", cameraPipeline, this);
		drive = new NewMecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), this);
		lift = new Lift(hardwareMap, this, true);
		intake = new Intake(hardwareMap, this, true);

		String[] positions = { "Right", "Center", "Left" };

		while (!isStarted() && !isStopRequested()) {
			drive.update();
			telemetry.addData("QR Code",
					positions[cameraPipeline.getShippingElementLocation()]);
			telemetry.addData("Checks", cameraPipeline.numChecks);
			telemetry.update();
		}
		int pos = cameraPipeline.getShippingElementLocation();
		switch (pos) {
			case 1:
				lift.positionMiddle();
				break;
			case 2:
				lift.positionUp();
				break;
			default:
				lift.positionDown();
				break;
		}
		while (opModeIsActive() && lift.isMoving()) sleep(100);
		drive.moveToLocation(shippingHub);
		int numCycles = 2;
		for (int i = 0; i < numCycles + 1; i++) {
			sleep(100);
			lift.dropFreight();
			sleep(1000);
			lift.dropFreight();
			sleep(100);
			drive.rotate(-90);
			sleep(100);
			lift.positionDown();
			sleep(100);
			drive.moveToLocation(warehouseEntrance);
			sleep(100);
			drive.moveToLocation(warehouse);
			if (!opModeIsActive() || i == numCycles) break;
			sleep(100);
			grabBlock();
			sleep(100);
			intake.intake();
			drive.moveToLocation(warehouseEntrance);
			sleep(100);
			lift.positionUp();
			intake.brake();
			drive.moveToLocation(shippingHubCycle);
			sleep(100);
			drive.rotate(0);
		}

		telemetry.addData("Status", "Stopping");
		telemetry.update();
		camera.stop();
		telemetry.addData("Status", "Stopped");
		telemetry.update();
		while (opModeIsActive()) sleep(100);
	}
}
