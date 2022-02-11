package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.NewMecanumDrive;
import Subsystems.CameraPipeline;
import Subsystems.Intake;
import Subsystems.Lift;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.Location;

@Autonomous(name="CycleAutoRed", group="Red Autos")
public class CycleAutoRed extends LinearOpMode {
	private NewMecanumDrive drive;
	private Lift lift;
	private Intake intake;

	private Location shippingHub = new Location(-18, -22, 0);
	private Location warehouseEntrance = new Location(-3, 10, -90);
	private Location warehouse = new Location(24, 10, -90);
	private Location wareHouseExit = new Location(-3, 6, -90);
	private Location shippingHubCycle = new Location(-10, -20, -90);

	private void grabBlock() {
		intake.intakeNoDelay();
		drive.rawMove(-0.1, -1.0 / 3, 0);
		while (opModeIsActive() && intake.moving()) {
			intake.update(System.currentTimeMillis());
			if (!intake.moving()) break;
			drive.updateLocation();
		}
		drive.brake();
		sleep(200);
		int numMeasurements = 10;
		double avg = 0;
		for (int i = 0; i < numMeasurements; i++) {
			avg += Math.min(intake.getDistance() / numMeasurements, 24.0 / numMeasurements);
		}
		drive.setCurrentLocation(new Location(46 - avg, 0, -90));
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
			lift.autoDrop();
			long drop = System.currentTimeMillis();
			while (opModeIsActive()) {
				long time = System.currentTimeMillis();
				if (time > drop + 1000) {
					break;
				}
				lift.update(time);
				sleep(50);
			}
			drive.rotate(-90);
			lift.positionDown();
			drive.moveToLocation(warehouseEntrance);
			drive.moveToLocation(warehouse);
			lift.update(System.currentTimeMillis());
			if (!opModeIsActive() || i == numCycles) break;
			grabBlock();
			intake.intake();
			drive.moveToLocation(wareHouseExit);
			lift.positionUp();
			intake.brake();
			drive.moveToLocation(shippingHubCycle);
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
