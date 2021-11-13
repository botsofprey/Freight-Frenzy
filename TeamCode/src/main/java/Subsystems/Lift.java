package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Lift {
	private static final double TICKS_PER_INCH = 537.7 / (0.91 * Math.PI);


	private DigitalChannel limitSwitch;

	private CRServo bucketWall;

	private DcMotorEx slide;
	private LinearOpMode mode;

	public Lift(HardwareMap hardwareMap, LinearOpMode opMode) {
		slide = hardwareMap.get(DcMotorEx.class, "Slider");
		slide.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
		slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		slide.setDirection(DcMotorSimple.Direction.REVERSE);

		bucketWall = hardwareMap.get(CRServo.class, "bucket");
		bucketWall.setDirection(DcMotorSimple.Direction.REVERSE);

		limitSwitch = hardwareMap.get(DigitalChannel.class, "limit");

		mode = opMode;
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
		bucketWall.setPower(1);
		mode.sleep(2500);
		bucketWall.setPower(0);
	}

	public void spinServo() {
		bucketWall.setPower(1);
	}

	public void stopServo() {
		bucketWall.setPower(0);
	}

	void update(){
		if(limitSwitch.getState() && slide.getPower() < 0){
			slide.setPower(0);
		}
	}
}
