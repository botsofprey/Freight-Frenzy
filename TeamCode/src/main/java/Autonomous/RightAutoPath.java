package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.NewMecanumDrive;
import DriveEngine.SplineCurve;
import Subsystems.CameraPipeline;
import Subsystems.Carousel;
import Subsystems.Lift;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.Location;

@Autonomous(name="RightAutoBlue", group="Competition Autos")
public class RightAutoPath extends LinearOpMode {
	private NewMecanumDrive drive;
	private Carousel carousel;
	private Lift lift;

	private Location carouselLocation = new Location(-18, -4, 0);
	private Location corner1 = new Location(-18, -40, 0);
	private Location shippingHub = new Location(7, -38, 90);
	private Location corner2 = new Location(-18, -36, 90);
	private Location corner3 = new Location(-18, -12, 90);
	private Location ramPause = new Location(54, -12, -90);

	@Override
	public void runOpMode() throws InterruptedException {
		CameraPipeline cameraPipeline = new CameraPipeline(this);
		Camera camera = new Camera(hardwareMap, "Webcam 1", cameraPipeline, this);
		drive = new NewMecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), this);
		carousel = new Carousel(hardwareMap, this, true);
		lift = new Lift(hardwareMap, this, true);

		while (!isStarted() && !isStopRequested()) {
			drive.update();
			telemetry.addData("QR Code", cameraPipeline.getShippingElementLocation());
			telemetry.update();
		}
		int pos = cameraPipeline.getShippingElementLocation();
		telemetry.addData("Pos", pos);
		telemetry.update();

		drive.moveToLocation(carouselLocation);
		sleep(500);
		carousel.autoRotate();
		sleep(6000);
		carousel.stop();
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
		lift.dropFreight();
		sleep(3000);
		lift.dropFreight();
		sleep(200);
		drive.moveToLocation(corner2);
		lift.positionDown();
		sleep(200);
		drive.moveToLocation(corner3);
		sleep(200);
		drive.rotate(-90);
		sleep(200);
		drive.moveToLocation(ramPause);
		sleep(500);
		drive.ram();
		sleep(2000);
		drive.brake();
		while (opModeIsActive());
	}
}
