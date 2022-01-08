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

	private MotorController motorControllerLeft, motorControllerRight;

	private Servo bucketDoor;
	//private DistanceSensor bucketSensor;
	//private double distanceInBucket;
	private RevBlinkinLedDriver led;

	private double doorStartPos = 0, doorClosePos = 1;

	public BucketArm(HardwareMap hardwareMap){
		RevTouchSensor start = new RevTouchSensor(hardwareMap, "start");
		RevTouchSensor end = new RevTouchSensor(hardwareMap, "end");
		motorControllerLeft = new MotorController(hardwareMap, "leftArm");
		motorControllerLeft.setSwitches(start, end);
		motorControllerLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		motorControllerRight = new MotorController(hardwareMap, "rightArm");
		motorControllerRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		motorControllerRight.setSwitches(start, end);

		motorControllerRight.setDirection(DcMotorSimple.Direction.REVERSE);
		motorControllerLeft.setDirection(DcMotorSimple.Direction.REVERSE);
		motorControllerLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

		led = hardwareMap.get(RevBlinkinLedDriver.class, "Led Indicate");

		bucketDoor = hardwareMap.get(Servo.class, "bucketDoor");
		//bucketSensor = hardwareMap.get(DistanceSensor.class, "bucketSensor");

		bucketDoor.setPosition(doorStartPos);
		//distanceInBucket = bucketSensor.getDistance(DistanceUnit.CM);
		led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
	}

	public void setPower(double power){
		motorControllerLeft.setPower(power);
		motorControllerRight.setPower(power);
	}

	public void dropFreight(){
		if(bucketDoor.getPosition() == doorStartPos){
		bucketDoor.setPosition(doorClosePos);
		} else{
			bucketDoor.setPosition(doorStartPos);
		}
	}

	public double getDoorPosition(){
		return bucketDoor.getPosition();
	}

	public void noDrop(){
		motorControllerLeft.setTargetPosition(motorControllerLeft.getCurrentPosition());
		motorControllerRight.setTargetPosition(motorControllerRight.getCurrentPosition());
		motorControllerRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		motorControllerRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
	}
	public void motorMode(){
		motorControllerLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorControllerRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}

	/*public double currentDistance(){
		//return bucketSensor.getDistance(DistanceUnit.CM);
	}

	public double initDistance(){
		//return distanceInBucket;
	}*/

	public double getPower(){
		return motorControllerLeft.getPower();
	}

	private boolean compareNumbers(double a, double b, double range){
		return Math.abs(a - b) < range;
	}

	public void update(){
		motorControllerLeft.update();
		motorControllerRight.update();

		if(compareNumbers(bucketDoor.getPosition(), doorClosePos, .25)){
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
		}else{
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);
		}
	}
}
