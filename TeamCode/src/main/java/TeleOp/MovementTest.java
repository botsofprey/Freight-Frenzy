package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import DriveEngine.Localizer;
import DriveEngine.TeleOpMotorDriver;
import UtilityClasses.Controller;

@TeleOp(name="MovementTest", group="TeleOp")
public class MovementTest extends LinearOpMode {
	
	private TeleOpMotorDriver driveBase;
	private Controller controller1;
	private Localizer localizer;
	
	@Override
	public void runOpMode() {
		driveBase = new TeleOpMotorDriver(hardwareMap, false);
		controller1 = new Controller(gamepad1);
		
		localizer = new Localizer(hardwareMap, "DeadWheelEncoder.json");
		
		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();
		
		while (opModeIsActive()) {
			controller1.update();
			if (controller1.aReleased) {
				driveBase.toggleTrueNorth();
			}
			driveBase.moveRobot(
					controller1.leftStick.x, controller1.leftStick.y, controller1.rightStick.x
			);
			localizer.update(this);
			telemetry.update();
		}
	}
}