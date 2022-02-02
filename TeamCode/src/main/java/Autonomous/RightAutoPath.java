package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import DriveEngine.NewMecanumDrive;
import DriveEngine.SplineCurve;
import Subsystems.CameraPipeline;
import Subsystems.Carousel;
import Subsystems.Intake;
import Subsystems.Lift;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.Location;

@Autonomous(name="RightAutoBlue", group="Competition Autos")
public class RightAutoPath extends LinearOpMode {
	private NewMecanumDrive drive;
	private Carousel carousel;
	private Lift lift;
	private Intake intake;

	private Location carouselLocation = new Location(-18, -4, 0);
	private Location corner1 = new Location(-18, -40, 0);
	private Location shippingHub = new Location(7, -40, 90);
	private Location corner2 = new Location(-18, -40, 90);
	private Location corner3 = new Location(-18, -6, 90);
	private Location ramPause = new Location(44, -12, -90);
	private Location warehouseEntrance = new Location(38, 8, -90);
	private Location warehouse = new Location(50, 8, -90);
	private Location corner4 = new Location(20, -23, -90);
	private Location duckDrive = new Location(36, -27, -100);
	private Location dropPoint2 = new Location(34, -30, -90);

	@Override
	public void runOpMode() throws InterruptedException {
		CameraPipeline cameraPipeline = new CameraPipeline(this);
		Camera camera = new Camera(hardwareMap, "Webcam 1", cameraPipeline, this);
		drive = new NewMecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), this);
		carousel = new Carousel(hardwareMap, this, true);
		lift = new Lift(hardwareMap, this, true);
		intake = new Intake(hardwareMap, this, true);

		while (!isStarted() && !isStopRequested()) {
			drive.update();
			telemetry.addData("QR Code", cameraPipeline.getShippingElementLocation());
			telemetry.update();
		}
		int pos = cameraPipeline.getShippingElementLocation();
		telemetry.addData("Pos", pos);
		telemetry.update();

		drive.moveToLocation(carouselLocation);
		sleep(100);
		carousel.autoRotate();
		sleep(4000);
		carousel.stop();
		sleep(100);
		drive.moveToLocation(corner1);
		sleep(200);
		drive.rotate(90);
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
		sleep(200);
		lift.dropFreight();
		sleep(1000);
		lift.dropFreight();
		sleep(200);
		drive.moveToLocation(corner2);
		lift.positionDown();
		sleep(200);
		drive.moveToLocation(corner3);
		sleep(200);
		drive.rotate(-90);
		sleep(200);
		intake.intake();
		sleep(200);
		drive.moveToLocation(corner4);
		sleep(200);
		drive.rotate(-100);
		sleep(200);
		drive.moveToLocation(duckDrive);
		sleep(200);
		drive.rotate(-90);
		sleep(1000);
		intake.brake();
		lift.positionUp();
		while (opModeIsActive() && lift.isMoving()) sleep(100);
		drive.moveToLocation(dropPoint2);
		sleep(200);
		lift.dropFreight();
		sleep(1000);
		lift.dropFreight();
		sleep(200);
		drive.moveToLocation(warehouseEntrance);
		lift.positionDown();
		sleep(200);
		drive.moveToLocation(warehouse);

		while (opModeIsActive()) sleep(200);
	}
}
