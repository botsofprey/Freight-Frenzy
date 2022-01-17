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
	public static final int TOP = 6100, MIDDLE = 3700, BOTTOM = 1300, MAX = 7200;
	public static final double TICKS_PER_INCH = 537.7 / 1.5;

	public boolean startPosSet = false;

	public BucketArm(HardwareMap hardwareMap){
//		RevTouchSensor start = new RevTouchSensor(hardwareMap, "start");
//		RevTouchSensor end = new RevTouchSensor(hardwareMap, "end");
		//motorControllerLeft = new MotorController(hardwareMap, "leftArm");
		//motorControllerLeft.setSwitches(start, end);
		//motorControllerLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//		motorControllerRight = new MotorController(hardwareMap, "rightArm");
//		motorControllerRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//		motorControllerRight.setSwitches(start, end);
//
//		motorControllerRight.setDirection(DcMotorSimple.Direction.REVERSE);
//		motorControllerLeft.setDirection(DcMotorSimple.Direction.REVERSE);
//		motorControllerLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

		liftMotor = hardwareMap.get(DcMotor.class, "lift");
//		liftMotor.setTargetPosition(liftMotor.getCurrentPosition() + 2000);
//		liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//		liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

		magLiftSensor = hardwareMap.get(TouchSensor.class, "liftSensor");
		leftBucketMotor = hardwareMap.get(DcMotor.class, "leftBucketMotor");
		rightBucketMotor = hardwareMap.get(DcMotor.class, "rightBucketMotor");
		leftBucketMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		rightBucketMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

		led = hardwareMap.get(RevBlinkinLedDriver.class, "Led Indicate");

		//bucketDoor = hardwareMap.get(Servo.class, "bucketDoor");
		//bucketSensor = hardwareMap.get(DistanceSensor.class, "bucketSensor");

		//bucketDoor.setPosition(doorStartPos);
		//distanceInBucket = bucketSensor.getDistance(DistanceUnit.CM);

		liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
	}

	public boolean liftIsBusy(){
		return liftMotor.isBusy();
	}

	public void setLiftPower(double power){
//		motorControllerLeft.setPower(power);
//		motorControllerRight.setPower(power);

		liftMotor.setPower(power);
	}

	public void setBucketPower(double power){
//		motorControllerLeft.setPower(power);
//		motorControllerRight.setPower(power);

		rightBucketMotor.setPower(power);
		leftBucketMotor.setPower(power);
	}

	public void dropFreight(){
//		if(bucketDoor.getPosition() == doorStartPos){
//		bucketDoor.setPosition(doorClosePos);
//		} else{
//			bucketDoor.setPosition(doorStartPos);
//		}

	}

	public double getLiftPos(){
		return liftMotor.getCurrentPosition();
	}

	public void noDrop(){
//		motorControllerLeft.setTargetPosition(motorControllerLeft.getCurrentPosition());
//		motorControllerRight.setTargetPosition(motorControllerRight.getCurrentPosition());
//		motorControllerRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//		motorControllerRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

		leftBucketMotor.setTargetPosition(leftBucketMotor.getCurrentPosition());
		rightBucketMotor.setTargetPosition(rightBucketMotor.getCurrentPosition());
		rightBucketMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		leftBucketMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
	}
	public void motorMode(){
//		motorControllerLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//		motorControllerRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//
		leftBucketMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		rightBucketMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}

	/*public double currentDistance(){
		//return bucketSensor.getDistance(DistanceUnit.CM);
	}

	public double initDistance(){
		//return distanceInBucket;
	}*/

	public double getPower(){
		//return motorControllerLeft.getPower();
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

	public void liftMoveTowards(int inches, double power){
		liftMotor.setTargetPosition(inches * (int)TICKS_PER_INCH);
		liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		liftMotor.setPower(power);
	}

	public void resetLiftEncoder(){
		liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
	}

	public void update(){
//		motorControllerLeft.update();
//		motorControllerRight.update();

		if(magLiftSensor.isPressed()){
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE);
		}else{
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
		}

//		if(!startPosSet && magLiftSensor.isPressed()){
//			startPosSet = true;
//			liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//		}
//		if(compareNumbers(liftMotor.getTargetPosition(), liftMotor.getCurrentPosition(), 0.1)
//				&& !startPosSet){
//			liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//			liftMotor.setPower(-.15);
//		}
	}
}
