package Subsystems;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
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

	private ColorRangeSensor bucketSensor;
	private static final int[] boxColor = {255, 190, 0}, ballColor = {241, 239, 223}, duckColor = {239, 206, 0};

	private RevBlinkinLedDriver led;

	//private double doorStartPos = 0, doorClosePos = 1;
	private double liftStartPos;

	public static final double INTAKE = 0.5, OUTTAKE = -0.5;
	public static final int TOP = 2640, MIDDLE = 1390, BOTTOM = 727, MAX = 3120;
	public static final double TICKS_PER_INCH = 537.6 / 1.5;

	public boolean startPosSet = false;

	public BucketArm(HardwareMap hardwareMap) {

		liftMotor = hardwareMap.get(DcMotor.class, "lift");

		magLiftSensor = hardwareMap.get(TouchSensor.class, "liftSensor");
		leftBucketMotor = hardwareMap.get(DcMotor.class, "leftBucketMotor");
		rightBucketMotor = hardwareMap.get(DcMotor.class, "rightBucketMotor");
		leftBucketMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		rightBucketMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

		bucketSensor = hardwareMap.get(ColorRangeSensor.class, "bucketSensor");

		led = hardwareMap.get(RevBlinkinLedDriver.class, "Led Indicate");

		liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
	}

	public boolean liftIsBusy() {
		return liftMotor.isBusy();
	}

	public void setLiftPower(double power) {
		liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		liftMotor.setPower(power);
	}

	public void setBucketPower(double power) {
		if (compareNumbers(getLiftPos(), TOP, 500))
			power = power * .75;
		rightBucketMotor.setPower(power);
		leftBucketMotor.setPower(power);
	}

	public double getBucketPower() {
		return rightBucketMotor.getPower();
	}

	public double getLiftPos() {
		return liftMotor.getCurrentPosition();
	}

	public void noDrop() {
		leftBucketMotor.setTargetPosition(leftBucketMotor.getCurrentPosition());
		rightBucketMotor.setTargetPosition(rightBucketMotor.getCurrentPosition());
		rightBucketMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		leftBucketMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
	}

	public void motorMode() {
		leftBucketMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		rightBucketMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}

	public double getPower() {
		return leftBucketMotor.getPower();
	}

	public int[] getColor() {
		int[] color = new int[3];

		color[0] = bucketSensor.red();
		color[1] = bucketSensor.green();
		color[2] = bucketSensor.blue();

		return color;
	}

	public boolean freightFound() {
		int[] curColor = getColor();
		return curColor[0] > 35 && curColor[1] > 65;
	}

	private boolean compareNumbers(double a, double b, double range) {
		return Math.abs(a - b) < range;
	}

	public boolean limitSwitch() {
		return magLiftSensor.isPressed();
	}

	public double getLiftPower() {
		return liftMotor.getPower();
	}

	public void liftMoveTowards(double inches, double power) {
		liftMotor.setTargetPosition((int) (inches * TICKS_PER_INCH));
		liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		liftMotor.setPower(power);
	}

	public void resetLiftEncoder() {
		liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
	}

	public boolean bucketFreight() {
		return freightFound();
	}

	public double bucketDistance() {
		return bucketSensor.getDistance(DistanceUnit.CM);
	}

	boolean autoIntaking = false, bucketFull = false;

	public void update() {
		if (bucketFull) {
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
		} else {
			led.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);
		}

		if (getBucketPower() >= 0) {
			if (bucketDistance() > 7) {
				if (freightFound()) {
					setBucketPower(INTAKE);
					autoIntaking = true;
				}
			} else if (autoIntaking && bucketDistance() < 5.5 || autoIntaking && !freightFound()) {
				setBucketPower(0);
				autoIntaking = false;
				bucketFull = true;
			}
		}else{
			bucketFull = false;
		}
	}

}