package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import DriveEngine.MecanumDrive;
import UtilityClasses.Location;

@Autonomous(name="mecanum drive test", group="Autonomous")
public class MovementTest extends LinearOpMode {
	private MecanumDrive drive;

	@Override
	public void runOpMode() throws InterruptedException {
		drive = new MecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), this);

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		drive.moveToLocation(new Location(0, 24, 0));
	}
}
