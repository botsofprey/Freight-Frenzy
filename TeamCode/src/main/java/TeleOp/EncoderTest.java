package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import DriveEngine.NewLocalizer;
import UtilityClasses.Controller;
import UtilityClasses.NewLocation;

@TeleOp(name="Encoder Test", group="test")
public class EncoderTest extends LinearOpMode {
	private static final DcMotorSimple.Direction[] DIRECTIONS = {
			DcMotorSimple.Direction.FORWARD,
			DcMotorSimple.Direction.FORWARD,
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.REVERSE
	};
	@Override
	public void runOpMode() throws InterruptedException {
		String[] motorNames = {
				"flMotor",
				"blMotor",
				"brMotor",
				"frMotor"
		};
		DcMotorEx[] motors = new DcMotorEx[4];
		for (int i = 0; i < 4; i++) {
			motors[i] = hardwareMap.get(DcMotorEx.class, motorNames[i]);
			motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
			motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
			motors[i].setDirection(DIRECTIONS[i]);
		}
		
		Controller controller = new Controller(gamepad1);
		
		NewLocalizer localizer = new NewLocalizer(hardwareMap, "RobotConfig.json");
		localizer.update(System.nanoTime());

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();
		localizer.setLocation(NewLocation.ORIGIN);

		while (opModeIsActive()) {
			controller.update();
			localizer.update(System.nanoTime());
			
			double x = +controller.leftStick.y, y = -controller.leftStick.x,
					a = -controller.rightStick.x;
			
			double[] powers = {
					x - y - a,
					x + y - a,
					x - y + a,
					x + y + a
			};

			double scale = 1;
			if (controller.leftTriggerHeld) scale /= 5.0;
			
			for (int i = 0; i < 4; i++)
				motors[i].setPower(powers[i] * scale);
			
			telemetry.addData("Location", localizer.getCurrentLocation());
			telemetry.update();
		}
	}
}
