package Subsystems.Delilah;

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
import com.qualcomm.robotcore.util.Range;

import UtilityClasses.BatterySaving;
import UtilityClasses.HardwareWrappers.MagneticLimitSwitch;
import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.HardwareWrappers.ServoController;
import UtilityClasses.PIDController;

public class Lift {
	private static final double TICKS_PER_INCH = 145.1 / (0.945 * Math.PI) * (7.6 / 5.8);

	private static final int[] POSITIONS = {
			0,
			350,
			800
	};
	private static final RevBlinkinLedDriver.BlinkinPattern
			downColor =  RevBlinkinLedDriver.BlinkinPattern.GREEN,
			midColor = RevBlinkinLedDriver.BlinkinPattern.YELLOW,
			upColor = RevBlinkinLedDriver.BlinkinPattern.RED;


	private ModernRoboticsTouchSensor limitSwitch;

	private ServoController bucketWall;
	private ServoController cappingArm;

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

	private BatterySaving batterySaving;

	private long dropTime;
	private boolean freightDropped;
	private boolean resetServo;

	public Lift(HardwareMap hardwareMap, LinearOpMode opMode) {
		mode = opMode;
		usingEncoders = true;
		braking = false;

		slide = new MotorController(hardwareMap, "liftMotor");
		slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		slide.setDirection(DcMotorSimple.Direction.REVERSE);
		slide.setPositionPIDFCoefficients(10);

		bucketWall = new ServoController(hardwareMap, "bucketServo");
		bucketWall.setPosition(1);

		cappingArm = new ServoController(hardwareMap, "cappingArm");
		cappingArm.setPosition(1);

		limitSwitch = hardwareMap.get(ModernRoboticsTouchSensor.class, "liftLimit");

		zeroSlider();

		liftLed = hardwareMap.get(RevBlinkinLedDriver.class, "liftLED");
		liftLed.setPattern(downColor);

		batterySaving = new BatterySaving(hardwareMap, liftLed);

		bucketColor = hardwareMap.get(ColorSensor.class, "bucketColor");

		dropTime = System.currentTimeMillis();
		freightDropped = false;
		resetServo = false;
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

	@Deprecated
	public void dropFreight() {
		bucketWall.setPosition(1 - bucketWall.getPosition());
	}

	public void autoDrop() {
		dropTime = System.currentTimeMillis();
		freightDropped = true;
		resetServo = false;
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

	public void setCappingArm(double position) {
		cappingArm.setPosition(position);
	}

	public void moveCappingArm(double offset) {
		cappingArm.setPosition(
				Range.clip(cappingArm.getPosition() + offset, -1, 1));
	}

	public void update(long millis) {
		boolean pressed = limitSwitch.isPressed();
		if (pressed && !switchPressed) {
			slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		}
		switchPressed = pressed;

		if (pressed && slide.getPower() < 0) {
			brake();
		}

//		if(!batterySaving.currentStatus()) {
			if (pressed) {
				liftLed.setPattern(downColor);
			} else if (slide.getCurrentPosition() > POSITIONS[2] - 50) {
				liftLed.setPattern(upColor);
			} else {
				liftLed.setPattern(midColor);
			}
//		}

		long delay = 600;
		if (freightDropped) {
			if (millis >= delay + dropTime) {
				bucketWall.setPosition(0);
				freightDropped = false;
				resetServo = true;
				dropTime = millis;
			}
			else {
				double pos = 1 - (millis - dropTime) / (double)delay;
				bucketWall.setPosition(pos);
			}
		}

		if (resetServo && millis >= dropTime + 2000) {
			resetServo = false;
			bucketWall.setPosition(1);
		}
	}
}
