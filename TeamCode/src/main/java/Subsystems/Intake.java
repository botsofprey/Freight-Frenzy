package Subsystems;

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

	private LinearOpMode mode;

	private ColorSensor colorSensor;
	private int blue, red, green;
	private static final int[] block = new int[] {204, 126, 8},
			ball = new int[] {255, 255, 255}, duck = new int[] {224, 183, 31};
	private static  final int range = 25;

	private long mil = 0;
	private boolean driverControl = true;

	public Intake(HardwareMap hw, LinearOpMode m, boolean errors) {
		mode = m;

		intakeMotor = new MotorController(hw, "intakeMotor", mode, errors);
		intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
		state = BRAKE;

		//colorSensor = hw.get(ColorSensor.class, "color Intake");
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

	private boolean detectColor() {
		red = colorSensor.red();
		green = colorSensor.green();
		blue = colorSensor.blue();

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
		//if (state == INTAKE && detectColor() && System.currentTimeMillis() - mil >= 1000) {
		//	brake();
		//}
	}

	private double[] RGBToHSV(int r, int g, int b) {
		if (r == g && r == b) {
			return new double[] { 0, 0, 1 };
		}
		double scale = Math.max(r, Math.max(g, b));
		double[] color = new double[] { r / scale, g / scale, b / scale };
		double h = 0;
		double max = 1.0;
		double min = Math.min(color[0], Math.min(color[1], color[2]));
		double diff = max - min;
		if (scale == r) {
			h = (60 * (color[1] - color[2]) / diff + 360) % 360;
		}
		if (scale == g) {
			h = (60 * (color[2] - color[0]) / diff + 120) % 360;
		}
		if (scale == b) {
			h = (60 * (color[0] - color[1]) / diff + 240) % 360;
		}
		return new double[] { h, diff, max };
	}
}
