package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MotorCarousel {
	public double AUTO_POWER = 0.2;
	public double TELEOP_POWER = 0.4;
	private static final long ON_TIME = 900;
	private static final long FULL_POWER = 1300;
	private static final long CYCLE_TIME = 2200;

	private LinearOpMode mode;

	private DcMotor spinner;
	private boolean spinning = false;

	private boolean endgameSpin = false;
	private boolean blueSide;
	private long switchTime;

	public MotorCarousel(HardwareMap hw, LinearOpMode m) {
		mode = m;
		spinner = hw.get(DcMotor.class, "duckSpinner");
		spinner.setDirection(DcMotorSimple.Direction.REVERSE);
		spinner.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		spinner.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}

	public void blueSpin() {
		spinning = !spinning;
		spinner.setPower(spinning ? AUTO_POWER : 0);
	}

	public void redSpin() {
		spinning = !spinning;
		spinner.setPower(spinning ? -AUTO_POWER : 0);
	}

	public void blueEndgame() {
		blueSide = true;
		endgameSpin = !endgameSpin;
		switchTime = System.currentTimeMillis();
		spinner.setPower(endgameSpin ? powerCurve(0) : 0);
	}

	public void redEndgame() {
		blueSide = false;
		endgameSpin = !endgameSpin;
		switchTime = System.currentTimeMillis();
		spinner.setPower(endgameSpin ? -powerCurve(0) : 0);
	}

	public void update(long millis) {
		if (endgameSpin) {
			long t = millis - switchTime;
			if (t > CYCLE_TIME) {
				switchTime = millis;
				t = 0;
			}
			double power = powerCurve(t);
			power *= blueSide ? 1 : -1;
			spinner.setPower(power);
		}
	}

	private double powerCurve(long t) {
		double f = (t > ON_TIME) ? 1 : TELEOP_POWER;
		double p = Math.max(TELEOP_POWER, 0/*TELEOP_POWER + (t - ON_TIME) * (1.0 - TELEOP_POWER) /
				(double)(FULL_POWER - ON_TIME)*/);//ramp up power by interpolating
		return (t <= FULL_POWER) ? f : 0;
	}
}
