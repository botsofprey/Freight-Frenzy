package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.NewMecanumDrive;
import Subsystems.CameraPipeline;
import Subsystems.Carousel;
import Subsystems.Intake;
import Subsystems.Lift;
import Subsystems.MotorCarousel;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.Location;

@Autonomous(name="DuckAutoBlue", group="Blue Autos")
public class DuckAutoBlue extends LinearOpMode {
	private NewMecanumDrive drive;
	private MotorCarousel carousel;
	private Lift lift;
	private Intake intake;

	private Location carouselLocation = new Location(-21, -7, 0);
	private Location corner1 = new Location(-20, -36, 0);
	private Location shippingHub = new Location(1, -45, 90);
	private Location corner2 = new Location(-20, -36, 90);
	private Location corner3 = new Location(-18, -12, 90);
	private Location ramPause = new Location(32, -20, -90);
	private Location warehouseEntrance = new Location(35, 11, -90);
	private Location warehouse = new Location(60, 12, -90);

	@Override
	public void runOpMode() throws InterruptedException {
		CameraPipeline cameraPipeline = new CameraPipeline(this);
		Camera camera = new Camera(hardwareMap, "Webcam 1", cameraPipeline, this);
		drive = new NewMecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), this);
		carousel = new MotorCarousel(hardwareMap, this);
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
		telemetry.addData("Pos", pos);
		telemetry.update();

		drive.moveToLocation(carouselLocation);
		sleep(500);
		carousel.blueSpin();
		sleep(4000);
		carousel.blueSpin();
		sleep(200);
		drive.moveToLocation(corner1);
		sleep(200);
		drive.rotate(90);
		sleep(200);
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
		sleep(200);
		drive.moveToLocation(shippingHub);
		sleep(200);
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
		drive.moveToLocation(corner2);
		lift.positionDown();
		sleep(200);
		drive.moveToLocation(corner3);
		sleep(200);
		drive.rotate(-90);
		sleep(200);
		drive.moveToLocation(ramPause);
		sleep(200);
		drive.rawMove(0, -1, 0);
		sleep(1500);
		drive.brake();
		lift.update(System.currentTimeMillis());
//		drive.moveToLocation(warehouseEntrance);
//		sleep(200);
//		drive.moveToLocation(warehouse);

		telemetry.addData("Status", "Stopping");
		telemetry.update();
		camera.stop();
		telemetry.addData("Status", "Stopped");
		telemetry.update();
		while (opModeIsActive()) sleep(100);
	}
}
