package TeleOp;

import static DataFiles.DriveBaseConstants.MOTOR_DIRECTIONS;
import static DataFiles.DriveBaseConstants.MOTOR_NAMES;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import DriveEngine.Localizer;
import DriveEngine.TeleOpMotorDriver;
import UtilityClasses.Controller;

@TeleOp(name="FeliciaTankBot", group="TeleOp")
public class FeliciaTankBot extends LinearOpMode {
	
	private TeleOpMotorDriver driveBase;
	private Controller controller1;
	private Localizer localizer;
	
	@Override
	public void runOpMode() {
		DcMotor leftFront = hardwareMap.get(DcMotor.class, "lf");
		DcMotor leftBack = hardwareMap.get(DcMotor.class, "lb");
		DcMotor rightFront = hardwareMap.get(DcMotor.class, "rf");
		DcMotor rightBack = hardwareMap.get(DcMotor.class, "rb");

		rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
		rightBack.setDirection(DcMotorSimple.Direction.REVERSE);

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();
		
		while (opModeIsActive()) {
			double leftPower = gamepad1.left_stick_y;
			double rightPower = gamepad1.right_stick_y;
			leftFront.setPower(leftPower);
			leftBack.setPower(leftPower);
			rightFront.setPower(rightPower);
			rightBack.setPower(rightPower);
			telemetry.update();
		}
	}
}
