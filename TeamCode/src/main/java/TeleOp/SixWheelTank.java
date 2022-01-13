package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Func;

import Subsystems.BucketArm;
import UtilityClasses.Controller;
import UtilityClasses.HardwareWrappers.CRServoController;
import UtilityClasses.HardwareWrappers.RevTouchSensor;

@TeleOp(name="6 Wheel Tank", group="Tank")
public class SixWheelTank extends LinearOpMode {
	private String[] names = new String[] {
			"frontLeft",
			"backLeft",
			"backRight",
			"frontRight"
	};
	private DcMotorSimple.Direction[] directions = new DcMotorSimple.Direction[] {
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.FORWARD
	};


	private DcMotor[] motors = new DcMotor[4];

	private Controller controller;

	private BucketArm bucketArm;

	private CRServoController servoLeft, servoRight;

	@Override
	public void runOpMode() throws InterruptedException {
		controller = new Controller(gamepad1);
		bucketArm = new BucketArm(hardwareMap);

		servoLeft = new CRServoController(hardwareMap, "leftWheel");
		servoRight = new CRServoController(hardwareMap, "rightWheel");

		for (int i = 0; i < 4; i++) {
			motors[i] = hardwareMap.get(DcMotor.class, names[i]);
			motors[i].setDirection(directions[i]);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		}

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		while (opModeIsActive()) {
			controller.update();
//			if(gamepad1.left_trigger != 0){
//				motors[0].setPower(1);
//				motors[1].setPower(1);
//			}else{
//				motors[0].setPower(0);
//				motors[1].setPower(0);
//			}
//
//			if(gamepad1.right_trigger != 0){
//
//				motors[2].setPower(1);
//				motors[3].setPower(1);
//			}else{
//				motors[2].setPower(0);
//				motors[3].setPower(0);
//			}
			motors[0].setPower(controller.leftStick.y);
			motors[1].setPower(controller.leftStick.y);
			motors[2].setPower(controller.rightStick.y);
			motors[3].setPower(controller.rightStick.y);

			if(controller.rightTriggerReleased || controller.leftTriggerReleased){
				bucketArm.noDrop();
			}else if(controller.rightTriggerPressed || controller.leftTriggerReleased){
				bucketArm.motorMode();
			}

			if(controller.rightTrigger != 0){
				bucketArm.setPower(-controller.rightTrigger);
			} else if(controller.leftTrigger != 0){
				bucketArm.setPower(controller.leftTrigger);
			}

			if(controller.xPressed){
				bucketArm.dropFreight();
			}

			if(controller.yHeld){
				servoLeft.setPower(-1);
				servoRight.setPower(1);
			}else{

				servoLeft.setPower(0);
				servoRight.setPower(0);
			}

			//			double forward = -gamepad1.left_stick_y;
//			double turn = gamepad1.right_stick_x;
//			double left = forward + turn;
//			double right = forward - turn;
//			if (gamepad1.left_trigger > 0.1) {
//				left /= 3;
//				right /= 3;
//			}
//			double[] powers = new double[] {
//					left,
//					left,
//					right,
//					right
//			};
//			normalize(powers);
//			for (int i = 0; i < 4; i++) {
//				motors[i].setPower(powers[i]);
//			}

			bucketArm.update();
			telemetry.update();
		}
	}

	private void normalize(double[] powers) {
		double max = 1;
		for (int i = 0; i < powers.length; i++) {
			max = Math.max(max, Math.abs(powers[i]));
		}
		for (int i = 0; i < powers.length; i++) {
			powers[i] /= max;
		}
	}
}
