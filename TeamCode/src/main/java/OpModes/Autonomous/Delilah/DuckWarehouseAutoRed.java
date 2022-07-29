package OpModes.Autonomous.Delilah;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.MecanumDrive;
import Subsystems.Delilah.CameraPipelineRed;
import Subsystems.Delilah.Intake;
import Subsystems.Delilah.Lift;
import Subsystems.Delilah.MotorCarousel;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.Deprecated.OldLocationClass;

@Autonomous(name="DuckWarehouseAutoRed", group="Red Autos", preselectTeleOp="Red OpModes.TeleOp")
public class DuckWarehouseAutoRed extends LinearOpMode {
	private MecanumDrive drive;
	private MotorCarousel carousel;
	private Lift lift;
	private Intake intake;

	private static final OldLocationClass carouselLocation = new OldLocationClass(18, 3, 0);
	private static final OldLocationClass corner1 = new OldLocationClass(10, -31, 0);
	private static final OldLocationClass shippingHub = new OldLocationClass(-5, -31, -90);
	private static final OldLocationClass corner2 = new OldLocationClass(10, -31, -90);
	private static final OldLocationClass corner3 = new OldLocationClass(10, -6, 90);
	private static final OldLocationClass corner4 = new OldLocationClass(-36, -6, 90);

	@Override
	public void runOpMode() throws InterruptedException {
		CameraPipelineRed cameraPipeline = new CameraPipelineRed(this);//todo changes with color
		Camera camera = new Camera(hardwareMap, "Webcam 1", cameraPipeline, this);
		drive = new MecanumDrive(hardwareMap, "RobotConfig.json",
				new OldLocationClass(0, 0, 0), this);
		carousel = new MotorCarousel(hardwareMap, this);
		lift = new Lift(hardwareMap, this);
		intake = new Intake(hardwareMap, this);

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
		carousel.redSpin();//todo changes with color
		sleep(4000);
		carousel.redSpin();
		sleep(200);
		drive.moveToLocation(corner1);
		sleep(200);
		drive.rotate(-90);//todo changes with color
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
		drive.rotate(90);
		sleep(200);
		drive.moveToLocation(corner3);
		sleep(200);
		drive.moveToLocation(corner4);
		lift.update(System.currentTimeMillis());
		sleep(200);
		drive.rotate(100);
		sleep(200);
		drive.oldRawMove(0, -1, 0);
		sleep(1500);
		drive.brake();

		while (opModeIsActive()) sleep(100);
	}
}
