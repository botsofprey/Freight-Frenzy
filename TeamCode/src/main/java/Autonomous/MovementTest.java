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
				new Location(0, 0, 0), this, true);
		
		CameraPipeline pipeline = new CameraPipeline(this);
		Camera camera = new Camera(hardwareMap, "Webcam 1", pipeline, this);

		while (opModeIsActive() && pipeline.getShippingElementLocation() == 0);
		telemetry.update();

		waitForStart();
		
		//drive.calibrate();
		
		//drive.moveToLocation(new Location(0, 24, 0));
		while (opModeIsActive()) {
			//drive.update();
			//telemetry.addData("Location", drive.getCurrentLocation());
			//telemetry.update();
		}
	}
}
