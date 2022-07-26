package Autonomous.Delilah;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.NewMecanumDrive;
import Subsystems.CameraPipelineBlue;
import Subsystems.CameraPipelineRed;
import Subsystems.Intake;
import Subsystems.Lift;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.Location;

@Autonomous(name="CycleAutoRed", group="Red Autos", preselectTeleOp="Red TeleOp")
public class CycleAutoRed extends LinearOpMode {
	private NewMecanumDrive drive;
	private Lift lift;
	private Intake intake;

	private Location shippingHub = new Location(21, -14, 0);
	private Location warehouseEntrance = new Location(3, 12, 90);
	private Location warehouse = new Location(-24, 12, 90);
	private Location wareHouseExit = new Location(5, 13, 90);
	private Location shippingHubCycle = new Location(23, -17, 90);

	private void grabBlock() {
		intake.intakeNoDelay();
		drive.rawMove(0.15, -1.0 / 3.5, 0);
		while (opModeIsActive() && intake.moving()) {
			intake.update(System.currentTimeMillis());
			if (!intake.moving()) break;
			drive.updateLocation();
		}
		drive.rawMove(0.15, 1, 0);
		sleep(200);
		drive.brake();
		sleep(200);
		int numMeasurements = 10;
		double avg = 0;
		for (int i = 0; i < numMeasurements; i++) {
			avg += Math.min(intake.getDistance() / numMeasurements, 24.0 / numMeasurements);
		}
		drive.setCurrentLocation(new Location(avg - 49, 8, 90));//todo changes with color
	}

	@Override
	public void runOpMode() throws InterruptedException {
		CameraPipelineRed cameraPipeline = new CameraPipelineRed(this);//todo changes with color
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
		camera.stop();
		switch (pos) {
			case 1:
				lift.positionMiddle();
				break;
			case 2:
				lift.positionDown();
				break;
			default:
				lift.positionUp();
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
			drive.rotate(90);
			lift.positionDown();
			drive.moveToLocation(warehouseEntrance);
			if (!opModeIsActive() || i == numCycles) break;
			drive.moveToLocation(warehouse);
			lift.update(System.currentTimeMillis());
			grabBlock();
			intake.intake();
			drive.moveToLocation(wareHouseExit);
			lift.positionUp();
			intake.brake();
			drive.moveToLocation(shippingHubCycle);
			drive.rotate(0);
		}
		lift.update(System.currentTimeMillis());
		drive.rawMove(0.25, -1, 0);
		sleep(1000);
		drive.brake();

		while (opModeIsActive()) sleep(100);
	}
}
