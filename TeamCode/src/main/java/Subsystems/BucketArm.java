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
	private DistanceSensor bucketSensor;
	private double distanceInBucket;
	private RevBlinkinLedDriver led;

	public BucketArm(HardwareMap hardwareMap){
		RevTouchSensor start;
		motorControllerLeft = new MotorController(hardwareMap, "leftArm");
		motorControllerLeft.setSwitches(hardwareMap, "start", "end");
		motorControllerRight = new MotorController(hardwareMap, "rightArm");
		motorControllerRight.setSwitches(hardwareMap, "start", "end");

		motorControllerRight.setDirection(DcMotorSimple.Direction.REVERSE);
		motorControllerLeft.setDirection(DcMotorSimple.Direction.REVERSE);

		led = hardwareMap.get(RevBlinkinLedDriver.class, "Led Indicate");

		bucketDoor = hardwareMap.get(Servo.class, "bucketDoor");
		bucketSensor = hardwareMap.get(DistanceSensor.class, "bucketSensor");

		bucketDoor.setPosition(1);
		distanceInBucket = bucketSensor.getDistance(DistanceUnit.CM);
		led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
	}

	public void setPower(double power){
		motorControllerLeft.setPower(power);
		motorControllerRight.setPower(power);
	}

	public void dropFreight(){
		bucketDoor.setPosition(1 - bucketDoor.getPosition());
	}

	public void update(){
		motorControllerLeft.update();
		motorControllerRight.update();

		if(Math.abs(bucketSensor.getDistance(DistanceUnit.CM) - distanceInBucket) < 5){
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
		}else{
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
		}
	}
}
