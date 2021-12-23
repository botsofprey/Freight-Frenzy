package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.HardwareWrappers.MagneticLimitSwitch;
import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.HardwareWrappers.ServoController;

public class Lift {
	private static final double TICKS_PER_INCH = 145.1 / (0.945 * Math.PI) * (7.6 / 5.8);

	private static final int[] POSITIONS = {
			150,
			425,
			800
	};


	private MagneticLimitSwitch limitSwitch;

	private ServoController bucketWall;

	private MotorController slide;
	private LinearOpMode mode;

	private boolean usingEncoders;
	private boolean braking;

	public Lift(HardwareMap hardwareMap, LinearOpMode opMode, boolean errors) {
		mode = opMode;
		usingEncoders = true;
		braking = false;

		slide = new MotorController(hardwareMap, "Slider", mode, errors);
		slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		slide.setDirection(DcMotorSimple.Direction.REVERSE);

		bucketWall = new ServoController(hardwareMap, "bucket", mode, errors);
		bucketWall.setPosition(1);

		limitSwitch = new MagneticLimitSwitch(hardwareMap, "liftLimit");

		zeroSlider();
	}

	public void zeroSlider(){
		braking = false;
		slide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

		if(!limitSwitch.getState()){//lift is lower
			slide.setPower(1);

			while (mode.opModeIsActive() && !limitSwitch.getState());

			slide.setPower(0);
			mode.sleep(100);
		}

		slide.setPower(-0.1);//lift is higher

		while (mode.opModeIsActive() && limitSwitch.getState());

		slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
	}

	public void rawMove(int height) {
		braking = true;
		usingEncoders = true;
		slide.setTargetPosition(height);
		slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
	}

	public boolean isMoving() {
		return slide.isBusy();
	}

	private void modeCheck() {
		if (usingEncoders) {
			usingEncoders = false;
			slide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		}
	}

	public void up() {
		braking = false;
		modeCheck();
		slide.setPower(0.5);
	}

	public void down() {
		braking = false;
		modeCheck();
		if (limitSwitch.getState()) {
			slide.setPower(-0.1);
		}
	}

	public void brake() {
		if (!braking) {
			modeCheck();
			slide.setTargetPosition(slide.getCurrentPosition());
			slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
			usingEncoders = true;
			braking = true;
		}
	}

	public void positionUp() {
		rawMove(POSITIONS[2]);
	}

	public void positionMiddle() {
		rawMove(POSITIONS[1]);
	}

	public void positionDown() {
		rawMove(POSITIONS[0]);
	}

	public double getCurrentHeight() {
		return 5 + slide.getCurrentPosition() / TICKS_PER_INCH;
	}

	public int getTick() {
		return slide.getCurrentPosition();
	}

	public void dropFreight() {
		bucketWall.setPosition(1 - bucketWall.getPosition());
	}

	public void update() {
		if (!limitSwitch.getState() && slide.getPower() < 0) {
			modeCheck();
			slide.setPower(0);
			slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		}
//		if (getCurrentHeight() >= 23 && slide.getPower() > 0) {
//			modeCheck();
//			slide.setPower(0);
//		}
	}
}
