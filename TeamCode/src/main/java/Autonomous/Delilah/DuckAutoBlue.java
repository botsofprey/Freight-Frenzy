package Autonomous.Delilah;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.MecanumDrive;
import Subsystems.CameraPipelineBlue;
import Subsystems.Intake;
import Subsystems.Lift;
import Subsystems.MotorCarousel;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.OldLocationClass;

@Autonomous(name="DuckAutoBlue", group="Blue Autos", preselectTeleOp="Blue TeleOp")
public class DuckAutoBlue extends LinearOpMode {
	private MecanumDrive drive;
	private MotorCarousel carousel;
	private Lift lift;
	private Intake intake;

	private static final OldLocationClass carouselLocation = new OldLocationClass(-21, -14, 0);
	private static final OldLocationClass corner1 = new OldLocationClass(-10, -47, 0);
	private static final OldLocationClass shippingHub = new OldLocationClass(10, -48, 90);
	private static final OldLocationClass corner2 = new OldLocationClass(-10, -47, 90);
	private static final OldLocationClass depot = new OldLocationClass(-20, -28, 90);

	@Override
	public void runOpMode() throws InterruptedException {
		CameraPipelineBlue cameraPipeline = new CameraPipelineBlue(this);//todo changes with color
		Camera camera = new Camera(hardwareMap, "Webcam 1", cameraPipeline, this);
		drive = new MecanumDrive(hardwareMap, "RobotConfig.json",
				new OldLocationClass(0, 0, 0), this);
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
		sleep(200);
		drive.moveToLocation(depot);
		lift.update(System.currentTimeMillis());

		while (opModeIsActive()) sleep(100);
	}
}
