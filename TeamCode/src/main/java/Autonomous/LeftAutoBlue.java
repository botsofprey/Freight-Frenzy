package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.NewMecanumDrive;
import Subsystems.CameraPipeline;
import Subsystems.Carousel;
import Subsystems.Intake;
import Subsystems.Lift;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.Location;

@Autonomous(name="LeftAutoBlue", group="Competition Autos")
public class LeftAutoBlue extends LinearOpMode {
	private NewMecanumDrive drive;
	private Carousel carousel;
	private Lift lift;
	private Intake intake;

	private Location shippingHub = new Location(-15, -20, 0);
	private Location warehouseEntrance = new Location(0, 4, -90);
	private Location warehouse = new Location(24, 4, -90);
	private Location shippingHubCycle = new Location(-15, -20, -90);

	private void grabBlock() {
		intake.intake();
		drive.rawMove(0, 1.0 / 3, 0);
		while (opModeIsActive() && intake.moving()) {
			long time = System.currentTimeMillis();
			intake.update(time);
			drive.updateLocation();
		}
		drive.brake();
		intake.intake();
	}

	@Override
	public void runOpMode() throws InterruptedException {
		CameraPipeline cameraPipeline = new CameraPipeline(this);
		Camera camera = new Camera(hardwareMap, "Webcam 1", cameraPipeline, this);
		drive = new NewMecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), this);
		carousel = new Carousel(hardwareMap, this, true);
		lift = new Lift(hardwareMap, this, true);
		intake = new Intake(hardwareMap, this);

		String[] positions = { "Right", "Center", "Left" };

		while (!isStarted() && !isStopRequested()) {
			drive.update();
			telemetry.addData("QR Code", positions[cameraPipeline.getShippingElementLocation()]);
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
