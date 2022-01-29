package Subsystems;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.HardwareWrappers.RevTouchSensor;

public class BucketArm {

	//private MotorController motorControllerLeft, motorControllerRight;
	private DcMotor liftMotor, leftBucketMotor, rightBucketMotor;
	private TouchSensor magLiftSensor;

	//private Servo bucketDoor;
	//private DistanceSensor bucketSensor;
	//private double distanceInBucket;
	private RevBlinkinLedDriver led;

	//private double doorStartPos = 0, doorClosePos = 1;
	private double liftStartPos;

	public static final double INTAKE = 0.5, OUTTAKE = -0.5;
	public static final int TOP = 2640, MIDDLE = 1390, BOTTOM = 727, MAX = 3120;
	public static final double TICKS_PER_INCH = 537.6 / 1.5;

	public boolean startPosSet = false;

	public BucketArm(HardwareMap hardwareMap){

		liftMotor = hardwareMap.get(DcMotor.class, "lift");

		magLiftSensor = hardwareMap.get(TouchSensor.class, "liftSensor");
		leftBucketMotor = hardwareMap.get(DcMotor.class, "leftBucketMotor");
		rightBucketMotor = hardwareMap.get(DcMotor.class, "rightBucketMotor");
		leftBucketMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		rightBucketMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

		led = hardwareMap.get(RevBlinkinLedDriver.class, "Led Indicate");
		led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE);

		liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
	}

	public boolean liftIsBusy(){
		return liftMotor.isBusy();
	}

	public void setLiftPower(double power){
		liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		liftMotor.setPower(power);
	}

	public void setBucketPower(double power){
		rightBucketMotor.setPower(power);
		leftBucketMotor.setPower(power);
	}

	public double getLiftPos(){
		return liftMotor.getCurrentPosition();
	}

	public void noDrop(){
		leftBucketMotor.setTargetPosition(leftBucketMotor.getCurrentPosition());
		rightBucketMotor.setTargetPosition(rightBucketMotor.getCurrentPosition());
		rightBucketMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		leftBucketMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
	}
	public void motorMode(){
		leftBucketMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		rightBucketMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}

	public double getPower(){
		return leftBucketMotor.getPower();
	}

	private boolean compareNumbers(double a, double b, double range){
		return Math.abs(a - b) < range;
	}

	public boolean limitSwitch(){
		return magLiftSensor.isPressed();
	}
	public double getLiftPower(){
		return liftMotor.getPower();
	}

	public void liftMoveTowards(double inches, double power){
		liftMotor.setTargetPosition((int)(inches * TICKS_PER_INCH));
		liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		liftMotor.setPower(power);
	}

	public void resetLiftEncoder(){
		liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}

	public void update(){
		if(magLiftSensor.isPressed()){
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE);
		}else{
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
		}
	}
}

