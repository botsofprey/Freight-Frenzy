package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.MecanumDrive;
import Subsystems.CameraPipeline;
import Subsystems.Carousel;
import Subsystems.Lift;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.Location;

@Autonomous(name="Red Auto", group="Autonomous")
public class RedAuto extends LinearOpMode {
	private MecanumDrive drive;
	private Carousel carousel;
	private Lift lift;

	private int blockLocation;

	private static final boolean throwErrors = true;
	
	@Override
	public void runOpMode() throws InterruptedException {
		drive = new MecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), true, this, throwErrors);
		carousel = new Carousel(hardwareMap, this, throwErrors);
		lift = new Lift(hardwareMap, this, throwErrors);
		
		CameraPipeline pipeline = new CameraPipeline(this);
		Camera camera = new Camera(hardwareMap, "Webcam 1", pipeline, this);

		while (!isStarted()) {
			blockLocation = pipeline.getShippingElementLocation();
			telemetry.addData("Location", blockLocation);
			telemetry.update();
		}

		telemetry.addData("Status", "Running");
		telemetry.update();

		drive.moveToLocation(new Location(-35.5, -7.5, 0));

		while(opModeIsActive());
		carousel.rotate();
		sleep(5000);
		carousel.stop();

		drive.moveToLocation(new Location(21, -9, 0));
		sleep(200);
		drive.moveToLocation(new Location(21, -23, 0));
		lift.up();
		sleep(100);
		lift.brake();
		if (blockLocation == 1) {
			lift.positionDown();
		}
		else if (blockLocation == 2) {
			lift.positionMiddle();
		}
		else if (blockLocation == 3) {
			lift.positionUp();
		}
		long time = System.nanoTime();
		while (opModeIsActive() && lift.isMoving() && System.nanoTime() < time + 2_000_000_000L)
			lift.update();
		sleep(2000);
		lift.dropFreight();
		sleep(1000);
		lift.dropFreight();
		sleep(1000);
		lift.positionDown();

		drive.moveToLocation(new Location(49, -20, -90));
		sleep(1000);
		drive.fastMode();
		drive.coast();
		drive.moveRobot(-1, 0, 0);
		sleep(1200);
		drive.moveRobot(0, 0, 0);
		drive.noFastMode();
		sleep(1000);
		drive.brake();
	} 
}
