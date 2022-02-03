package Subsystems;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsTouchSensor;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

import UtilityClasses.HardwareWrappers.MagneticLimitSwitch;
import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.HardwareWrappers.ServoController;
import UtilityClasses.PIDController;

public class Lift {
	private static final double TICKS_PER_INCH = 145.1 / (0.945 * Math.PI) * (7.6 / 5.8);

	private static final int[] POSITIONS = {
			0,
			350,
			700
	};
	private static final RevBlinkinLedDriver.BlinkinPattern
			downColor =  RevBlinkinLedDriver.BlinkinPattern.GREEN,
			midColor = RevBlinkinLedDriver.BlinkinPattern.YELLOW,
			upColor = RevBlinkinLedDriver.BlinkinPattern.RED;


	private ModernRoboticsTouchSensor limitSwitch;

	private ServoController bucketWall;

	private MotorController slide;
	private LinearOpMode mode;

	private boolean usingEncoders;
	private boolean braking;
	private boolean switchPressed = true;

	private ColorSensor bucketColor;
	private int blue, red, green;
	private static final int[] block = new int[] {204, 126, 8},
			ball = new int[] {255, 255, 255}, duck = new int[] {224, 183, 31};
	private static  final int range = 25;

	private RevBlinkinLedDriver liftLed;

	private long dropTime;
	private boolean freightDropped;

	public Lift(HardwareMap hardwareMap, LinearOpMode opMode, boolean errors) {
		mode = opMode;
		usingEncoders = true;
		braking = false;

		slide = new MotorController(hardwareMap, "liftMotor", mode, errors);
		slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		slide.setDirection(DcMotorSimple.Direction.REVERSE);
		slide.setPositionPIDFCoefficients(10);

		bucketWall = new ServoController(hardwareMap, "bucketServo", mode, errors);
		bucketWall.setPosition(1);

		limitSwitch = hardwareMap.get(ModernRoboticsTouchSensor.class, "liftLimit");

		zeroSlider();

		liftLed = hardwareMap.get(RevBlinkinLedDriver.class, "liftLED");
		liftLed.setPattern(downColor);

		bucketColor = hardwareMap.get(ColorSensor.class, "bucketColor");

		dropTime = System.currentTimeMillis();
		freightDropped = false;
	}

	public void zeroSlider(){
		mode.telemetry.addData("Status", "Zeroing slider");
		mode.telemetry.update();
		braking = false;
		slide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

		if(limitSwitch.isPressed()){//lift is lower
			slide.setPower(1);

			while (mode.opModeIsActive() && limitSwitch.isPressed());

			slide.setPower(0);
			mode.sleep(100);
		}

		slide.setPower(-0.1);//lift is higher

		while (mode.opModeIsActive() && !limitSwitch.isPressed());

		slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		mode.telemetry.addData("Status", "Initializing");
		mode.telemetry.update();
	}

	public void rawMove(int height) {
		braking = true;
		usingEncoders = true;
		slide.setTargetPosition(height);
		slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		if (height < 100) {
			slide.setVelocity(750);
		} else {
			slide.setVelocity(10000);
		}
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

	public void upAnalog(double power) {
		braking = false;
		modeCheck();
		slide.setPower(power);
	}

	public void up() {
		getTick();
		braking = false;
		modeCheck();
		slide.setPower(0.5);
	}

	public void downAnalog(double power) {
		if (!limitSwitch.isPressed()) {
			braking = false;
			modeCheck();
			slide.setPower(-power / 5.0);
		}
	}

	public void down() {
		if (!limitSwitch.isPressed()) {
			braking = false;
			modeCheck();
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

	public void autoDrop() {
		bucketWall.setPosition(0);
		dropTime = System.currentTimeMillis();
		freightDropped = true;
	}

	private boolean detectColor() {
		red = bucketColor.red();
		green = bucketColor.green();
		blue = bucketColor.blue();

		int[] color = new int[] {red,green,blue};

		boolean blockBo = colorChecker(block, color);
		boolean ballBo = colorChecker(ball, color);
		boolean duckBo = colorChecker(duck, color);

		return ballBo || blockBo || duckBo;
	}

	private boolean colorChecker(int[] colorA, int[] colorB) {
		return Math.abs(colorA[0] - colorB[0]) <= range && Math.abs(colorA[1] - colorB[1]) <= range
				&& Math.abs(colorA[2] - colorB[2]) <= range;
	}

	public void update() {
		boolean pressed = limitSwitch.isPressed();
		if (pressed && !switchPressed) {
			slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		}
		switchPressed = pressed;

		if (pressed && slide.getPower() < 0) {
			brake();
		}

		if (pressed) {
			liftLed.setPattern(downColor);
		} else if (slide.getCurrentPosition() > POSITIONS[2] - 50) {
			liftLed.setPattern(upColor);
		} else {
			liftLed.setPattern(midColor);
		}

		if (freightDropped && System.currentTimeMillis() >= 2000 + dropTime) {
			freightDropped = false;
			bucketWall.setPosition(1);
		}

		mode.telemetry.addData("Switch", pressed);
	}
}
