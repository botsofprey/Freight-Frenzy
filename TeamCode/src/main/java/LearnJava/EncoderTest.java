package LearnJava;

import static DataFiles.DriveBaseConstants.MOTOR_DIRECTIONS;
import static DataFiles.DriveBaseConstants.MOTOR_NAMES;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="Encoder Test", group="LearnJava")
public class EncoderTest extends LinearOpMode {

	private DcMotorEx[] motors = new DcMotorEx[4];

	public static final int FRONT_LEFT_DRIVE_MOTOR = 0;
	public static final int FRONT_RIGHT_DRIVE_MOTOR = 1;
	public static final int BACK_RIGHT_DRIVE_MOTOR = 2;
	public static final int BACK_LEFT_DRIVE_MOTOR = 3;
	public DcMotorEx armExtension;

	public void runOpMode() throws InterruptedException {
		DcMotorSimple.Direction[] directions = {
				DcMotorSimple.Direction.REVERSE,
				DcMotorSimple.Direction.FORWARD,
				DcMotorSimple.Direction.FORWARD,
				DcMotorSimple.Direction.REVERSE
		};

		for (int i = 0; i < motors.length; i++) {
			motors[i] = hardwareMap.get(DcMotorEx.class, MOTOR_NAMES[i]);
			motors[i].setDirection(directions[i]);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		}

		armExtension = hardwareMap.get(DcMotorEx.class, "armExtension");

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		while (opModeIsActive()) {
			for (int i = 0; i < motors.length; i++) {
				telemetry.addData(MOTOR_NAMES[i], motors[i].getCurrentPosition());
			}

			motors[BACK_LEFT_DRIVE_MOTOR].setPower(gamepad1.a ? 1 : 0);
			motors[BACK_RIGHT_DRIVE_MOTOR].setPower(gamepad1.b ? 1 : 0);
			motors[FRONT_LEFT_DRIVE_MOTOR].setPower(gamepad1.x ? 1 : 0);
			motors[FRONT_RIGHT_DRIVE_MOTOR].setPower(gamepad1.y ? 1 : 0);

			telemetry.update();
		}
	}

}
