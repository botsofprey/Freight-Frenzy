package Autonomous;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.NewMecanumDrive;
import Subsystems.CameraPipelineBlue;
import Subsystems.Intake;
import Subsystems.Lift;
import Subsystems.MotorCarousel;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.Location;

@Autonomous(name="DuckWarehouseAutoBlue", group="Blue Autos", preselectTeleOp="Blue TeleOp")
public class DuckWarehouseAutoBlue extends LinearOpMode {
	private NewMecanumDrive drive;
	private MotorCarousel carousel;
	private Lift lift;
	private Intake intake;

	private static final Location carouselLocation = new Location(-21, -14, 0);
	private static final Location corner1 = new Location(-10, -47, 0);
	private static final Location shippingHub = new Location(10, -48, 90);
	private static final Location corner2 = new Location(-10, -47, 90);
	private static final Location corner3 = new Location(-12, -12, -90);
	private static final Location corner4 = new Location(27, -12, -90);

	@Override
	public void runOpMode() throws InterruptedException {
		CameraPipelineBlue cameraPipeline = new CameraPipelineBlue(this);//todo changes with color
		Camera camera = new Camera(hardwareMap, "Webcam 1", cameraPipeline, this);
		drive = new NewMecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), this);
		carousel = new MotorCarousel(hardwareMap, this);
		lift = new Lift(hardwareMap, this, true);
		intake = new Intake(hardwareMap, this, true);
//		int chargeSoundId = hardwareMap.appContext.getResources().getIdentifier("charge",
//				"raw", hardwareMap.appContext.getPackageName());
//		boolean chargeFound = false;
//		if (chargeSoundId != 0)
//			chargeFound = SoundPlayer.getInstance().preload(hardwareMap.appContext, chargeSoundId);

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

		drive.moveToLocation(carouselLocation);
		sleep(500);
		carousel.blueSpin();//todo changes with color
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
				lift.positionDown();
				break;
			default:
				lift.positionUp();
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
		drive.rotate(-90);
		sleep(200);
		drive.moveToLocation(corner3);
		sleep(200);
		drive.moveToLocation(corner4);
		lift.update(System.currentTimeMillis());
		sleep(200);
		drive.rotate(-100);
		sleep(200);
//		if (chargeFound)
//			SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, chargeSoundId);
		drive.rawMove(0, -1, 0);
		sleep(1500);
		drive.brake();

		while (opModeIsActive()) sleep(100);
	}
}