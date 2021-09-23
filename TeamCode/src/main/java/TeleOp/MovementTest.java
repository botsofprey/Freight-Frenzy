package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import DriveEngine.TeleOpMotorDriver;
import UtilityClasses.Controller;

@TeleOp(name="MovementTest", group="TeleOp")
//@Disabled
public class MovementTest extends LinearOpMode {
	
	private TeleOpMotorDriver driveBase;
	private Controller controller1;
	
	@Override
	public void runOpMode() throws InterruptedException {
		try {
			driveBase = new TeleOpMotorDriver(hardwareMap, false);
			controller1 = new Controller(gamepad1);
			
			telemetry.addData("Status", "Initialized");
			telemetry.update();
			waitForStart();
			
			while (opModeIsActive()) {
				controller1.update();
				driveBase.moveRobot(
						controller1.leftStick.x, controller1.leftStick.y, controller1.rightStick.x, this
				);
				telemetry.update();
			}
		} finally {
			driveBase.close();
		}
	}
}
