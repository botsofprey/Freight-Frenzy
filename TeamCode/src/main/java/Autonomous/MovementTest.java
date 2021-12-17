package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;

import DriveEngine.MecanumDrive;
import Subsystems.CameraPipeline;
import UtilityClasses.HardwareWrappers.Camera;
import UtilityClasses.Location;

@Autonomous(name="mecanum drive test", group="Autonomous")
public class MovementTest extends LinearOpMode {
	private MecanumDrive drive;
	
	@Override
	public void runOpMode() throws InterruptedException {
		drive = new MecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), true, this, true);
		
		//CameraPipeline pipeline = new CameraPipeline(this);
		//Camera camera = new Camera(hardwareMap, "Webcam 1", pipeline, this);

		//while (opModeIsActive() && pipeline.getShippingElementLocation() == 0);
		telemetry.addData("Status", "Initialized");
		telemetry.update();

		waitForStart();
		telemetry.addData("Status", "Running");
		telemetry.update();
		
		while (opModeIsActive()) {
			drive.update();
			drive.moveToLocation(new Location(0, 48, 90));
			sleep(100);
			drive.moveToLocation(new Location(48, 48, -180));
			sleep(100);
			drive.moveToLocation(new Location(48, 0, -90));
			sleep(100);
			drive.moveToLocation(new Location(0, 0, 0));
			sleep(100);
		}
	}
}
