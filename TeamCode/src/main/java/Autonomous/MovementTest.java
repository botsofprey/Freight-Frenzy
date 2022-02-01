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
	
	@Override
	public void runOpMode() throws InterruptedException {//test
		drive = new NewMecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), this);
		
		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();
		
		drive.moveToLocation(-18, 0, 0);
		drive.moveToLocation(-18, -48, 0);
		while (opModeIsActive()) {
		
		}
	}
}
