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

import UtilityClasses.BatterySaving;
import UtilityClasses.HardwareWrappers.CRServoController;
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

	public static final double INTAKE = 0.5, OUTTAKE = -0.5;
	public static final int TOP = 3113, MIDDLE = 1760, BOTTOM = 1019, MAX = 5000;
	public static final double TICKS_PER_INCH = 537.6 / (1.5 * Math.PI);

	public boolean startPosSet = false;

	private BatterySaving batterySaving;

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

		batterySaving = new BatterySaving(hardwareMap, led);

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
			power = power * .85;
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
		return curColor[0] > 40 && curColor[1] > 70;
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

	public boolean autoIntaking = false, bucketFull = false;

	public void update() {
		batterySaving.update();

		if(!batterySaving.currentStatus()) {
			if (bucketFull) {
				led.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
			} else {
				led.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);
			}
		}

		if (getBucketPower() >= 0) {
			if (bucketDistance() <= 7) {
				if (freightFound()) {
					setBucketPower(INTAKE);
					autoIntaking = true;
				}
			}
			if (autoIntaking && !freightFound()) {
				setBucketPower(0);
				autoIntaking = false;
				bucketFull = true;
			}
		}else{
			bucketFull = false;
		}
	}

}