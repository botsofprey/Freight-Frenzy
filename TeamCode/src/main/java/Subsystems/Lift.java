package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import UtilityClasses.HardwareWrappers.MagneticLimitSwitch;
import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.HardwareWrappers.ServoController;

public class Lift {
	private static final double TICKS_PER_INCH = 537.7 / (0.91 * Math.PI);


	private MagneticLimitSwitch limitSwitch;

	private ServoController bucketWall;

	private MotorController slide;
	private LinearOpMode mode;

	public Lift(HardwareMap hardwareMap, LinearOpMode opMode) {
		mode = opMode;

		slide = new MotorController(hardwareMap, "Slider", mode);
		slide.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
		slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		slide.setDirection(DcMotorSimple.Direction.REVERSE);

		bucketWall = new ServoController(hardwareMap, "bucket", mode);
		bucketWall.setDirection(Servo.Direction.REVERSE);

		limitSwitch = new MagneticLimitSwitch(hardwareMap, "liftLimit", mode);
	}

	private void zeroSlider(){
		if(limitSwitch.getState()){
			slide.setPower(0.25);

			while(mode.opModeIsActive() && limitSwitch.getState());
		}

		slide.setPower(-0.25);

		while(mode.opModeIsActive() && !limitSwitch.getState());

		slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
	}

	public void move(double height, double inchesPerSecond) {
		height -= 7 ;
		slide.setTargetPosition((int)(height * TICKS_PER_INCH));
		slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		slide.setVelocity(inchesPerSecond * TICKS_PER_INCH);

		while(mode.opModeIsActive() && slide.isBusy()) {
			update();
		}
	}

	public void moveUp(){
		slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		slide.setPower(1);
	}
	public void moveDown(){
		slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		slide.setPower(-1);
	}
	public void brake(){
		slide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		slide.setPower(0);
	}

	public double getCurrentHeight(){
		return 7 + slide.getCurrentPosition() / TICKS_PER_INCH;
	}

	public void dropFreight() {
		bucketWall.setPosition(1);
		mode.sleep(2500);
		bucketWall.setPosition(0);
	}

	void update(){
		if(limitSwitch.getState() && slide.getPower() < 0){
			slide.setPower(0);
		}
	}
}
