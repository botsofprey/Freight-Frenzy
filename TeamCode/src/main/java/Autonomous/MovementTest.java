package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.DriveConstraints;
import DriveEngine.NewMecanumDrive;
import DriveEngine.SplineCurve;
import DriveEngine.Trajectory;
import DriveEngine.TrajectoryBuilder;
import Subsystems.Carousel;
import Subsystems.Lift;
import UtilityClasses.Location;

@Autonomous(name="mecanum drive test", group="Autonomous")
public class MovementTest extends LinearOpMode {
	private NewMecanumDrive drive;
	private Carousel carousel;
	private Lift lift;

	private int blockLocation;

	private static final boolean throwErrors = true;
	
	@Override
	public void runOpMode() throws InterruptedException {//test
		drive = new NewMecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), this);
		DriveConstraints constraints = new DriveConstraints();
		constraints.maxAngularAcceleration = 720;
		constraints.maxAngularVelocity = 280;
		constraints.maxAcceleration = 200;
		constraints.maxVelocity = 30;
		SplineCurve splineCurve = new SplineCurve(new Location(0, 0, 0),
				new Location(48, 48, 0),
				new Location(0, 100, 0),
				new Location(0, 100, 0));
		SplineCurve a = new SplineCurve(new Location(-12, -12, 0),
				new Location(-12, 12, 0));
		SplineCurve b = new SplineCurve(new Location(-12, 12, 0),
				new Location(12, 12, 0));
		SplineCurve c = new SplineCurve(new Location(12, 12, 0),
				new Location(12, -12, 0));
		SplineCurve d = new SplineCurve(new Location(12, -12, 0),
				new Location(-12, -12, 0));
		
		TrajectoryBuilder trajBuild =
				new TrajectoryBuilder(new Location(0, 0, 0), constraints)
				.splineToLocation(new Location(24, 24, 0));
		Trajectory traj = trajBuild.build();
		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		telemetry.addData("Status", "Moving");
		telemetry.update();
		//drive.followTrajectory(traj);
		drive.followPath(splineCurve);
		drive.waitForMovement();
//		while (opModeIsActive() && drive.isMoving()) {
//			telemetry.addData("Location", drive.getCurrentLocation());
//			telemetry.addData("Time", System.nanoTime());
//			telemetry.update();
//			drive.update();
//		}
//		telemetry.addData("Status", "Stopped");
//		telemetry.update();
//		while (opModeIsActive()) {
//			drive.followPath(a);
//			drive.waitForMovement();
//			drive.followPath(b);
//			drive.waitForMovement();
//			drive.followPath(c);
//			drive.waitForMovement();
//			drive.followPath(d);
//			drive.waitForMovement();
//		}
	}
}
