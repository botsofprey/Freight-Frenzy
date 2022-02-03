package Subsystems;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.ColorSensor;
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

	private ColorSensor bucketSensor, outsideSensor;
	private static final int[] boxColor = {255, 190, 0}, ballColor = {241, 239, 223}, duckColor = {239, 206, 0};

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

		bucketSensor = hardwareMap.get(ColorSensor.class, "bucketSensor");
		outsideSensor = hardwareMap.get(ColorSensor.class, "outsideSensor");

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
		if(compareNumbers(getLiftPos(), TOP, 500))
			power = power * .5;
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

	private int[] getColor(ColorSensor colorSensor){
		int[] color = new int[3];

		color[0] = colorSensor.red() / 255;
		color[0] = colorSensor.green() /255;
		color[0] = colorSensor.blue() / 255;

		return color;
	}
	public boolean freightFound(ColorSensor colorSensor){
		int[] curColor = getColor(colorSensor);
		return compareNumbers(ballColor[0], curColor[0], 25) &&
				compareNumbers(ballColor[1], curColor[1], 25) &&
				compareNumbers(ballColor[2], curColor[2], 25) ||
	compareNumbers(boxColor[0], curColor[0], 25) &&
				compareNumbers(boxColor[1], curColor[1], 25) &&
				compareNumbers(boxColor[2], curColor[2], 25) ||
	compareNumbers(duckColor[0], curColor[0], 25) &&
				compareNumbers(duckColor[1], curColor[1], 25) &&
				compareNumbers(duckColor[2], curColor[2], 25);
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
	}

	public boolean bucketFreight(){
		return freightFound(bucketSensor);
	}

	public boolean outsideFreight(){
		return freightFound(outsideSensor);
	}

	long millis;
	boolean autoIntaking = false;

	public void update(){
		if(freightFound(bucketSensor)){
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
		}else{
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);

			if(freightFound(outsideSensor)){
				setBucketPower(INTAKE);
				millis = System.currentTimeMillis();
				autoIntaking = true;
			}
		}
		if(autoIntaking){
			if(System.currentTimeMillis() - millis >= 1500){
				setBucketPower(0);
				autoIntaking = false;
			}
		}
	}
}

