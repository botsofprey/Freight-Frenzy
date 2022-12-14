package Subsystems;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.HardwareWrappers.MotorController;

public class Intake {
	public static final int INTAKE_BUTTON = 0;
	public static final int OUTTAKE_BUTTON = 1;

	private static final double MOTOR_POWER = 0.75;
	private static final int BRAKE = 0;
	private static final int INTAKE = 1;
	private static final int OUTTAKE = 2;
	private static final int[][] STATE_TABLE = {
			{ INTAKE, OUTTAKE },
			{ BRAKE, OUTTAKE },
			{ INTAKE, BRAKE }
	};
	private int state;


	private MotorController intakeMotor;

	private RevBlinkinLedDriver intakeLEDs;

	private LinearOpMode mode;

	private ColorSensor colorSensorA;
	private ColorSensor colorSensorB;
	private static final int RED_THRESHOLD = 72;
	private int blue, red, green;
	private static final int[] block = new int[] {204, 126, 8},
			ball = new int[] {255, 255, 255}, duck = new int[] {224, 183, 31};
	private static  final int range = 25;

	private long mil = 0;
	private long freezeTime = 0;
	private boolean driverControl = true;

	public Intake(HardwareMap hw, LinearOpMode m, boolean errors) {
		mode = m;

		intakeMotor = new MotorController(hw, "intakeMotor", mode, errors);
		intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
		state = BRAKE;

		colorSensorA = hw.get(ColorSensor.class, "intakeSensorA");
		colorSensorB = hw.get(ColorSensor.class, "intakeSensorB");

		intakeLEDs = hw.get(RevBlinkinLedDriver.class, "intakeLED");
		intakeLEDs.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
	}

	private boolean detectColor() {
		return Math.max(colorSensorA.red(), colorSensorB.red()) > RED_THRESHOLD;
	}

	public void intake() {
		intakeMotor.setPower(MOTOR_POWER);
		state = INTAKE;

		mil = System.currentTimeMillis();
	}

	public void outtake() {
		intakeMotor.setPower(-MOTOR_POWER);
		state = OUTTAKE;
	}

	public void brake() {
		intakeMotor.setPower(0);
		state = BRAKE;
	}

	public void switchState(int button) {
		state = STATE_TABLE[state][button];
		intakeMotor.setPower(getPowerFromState());
		mil = System.currentTimeMillis();
	}

	private double getPowerFromState() {
		return state == OUTTAKE ? -MOTOR_POWER : (state * MOTOR_POWER);
	}

	public void resetLEDs() {
		intakeLEDs.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
	}

	public void update() {
		boolean color = detectColor();
		mode.telemetry.addData("Intake sensor", color);
		if (state == INTAKE && detectColor() && System.currentTimeMillis() - mil >= 1000) {
			intakeLEDs.setPattern(RevBlinkinLedDriver.BlinkinPattern.VIOLET);
			brake();
			freezeTime = System.currentTimeMillis();
		}

		if (freezeTime != 0 && freezeTime + 500 <= System.currentTimeMillis()) {
			intake();
			freezeTime = 0;
			resetLEDs();
		}
	}
}
