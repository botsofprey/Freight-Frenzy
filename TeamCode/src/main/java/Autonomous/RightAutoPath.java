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

	private SplineCurve toCarousel = new SplineCurve(new Location(-61, -41, 90),
			new Location(-58, -56, 90));
	private SplineCurve toShippingHub = new SplineCurve(toCarousel.getEnd(),
			new Location(-24, -29, 180),
			new Location(0, 300, 0), SplineCurve.SECOND_POINT);
	private SplineCurve toDepot = new SplineCurve(toShippingHub.getEnd(),
			new Location(-36, -60, 180));

	@Override
	public void runOpMode() throws InterruptedException {
		CameraPipeline cameraPipeline = new CameraPipeline(this);
		Camera camera = new Camera(hardwareMap, "Webcam 1", cameraPipeline, this);
		drive = new NewMecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(-61, -41, 90), this);
		carousel = new Carousel(hardwareMap, this, true);
		lift = new Lift(hardwareMap, this, true);

		while (!isStarted() && !isStopRequested()) {
			drive.update();
			telemetry.addData("Location", drive.getCurrentLocation());
			telemetry.addData("QR Code", cameraPipeline.getShippingElementLocation());
			telemetry.update();
		}

		telemetry.addData("Status", "Moving to carousel");
		telemetry.update();
		drive.followPath(toCarousel);
		drive.setSpeed(6);
		drive.waitForMovement(()->{
			telemetry.addData("Location", drive.getCurrentLocation());
			telemetry.addData("Target", drive.targetLocation);
			telemetry.update();
		});

		while (opModeIsActive());
/*
		telemetry.addData("Status", "Spinning carousel");
		telemetry.update();
		carousel.rotate();
		sleep(1500);
		carousel.stop();

		telemetry.addData("Status", "Moving to shipping hub");
		telemetry.update();
		int level = cameraPipeline.getShippingElementLocation();
		switch (level) {
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
		drive.followPath(toShippingHub);
		drive.waitForMovement();

		telemetry.addData("Status", "Dropping freight");
		telemetry.update();
		lift.dropFreight();
		sleep(1000);
		lift.dropFreight();

		telemetry.addData("Status", "Moving to depot");
		telemetry.update();
		drive.followPath(toDepot);
		drive.waitForMovement();

		telemetry.addData("Status", "Parked");
		telemetry.update();
		while(opModeIsActive());*/
	}
}
