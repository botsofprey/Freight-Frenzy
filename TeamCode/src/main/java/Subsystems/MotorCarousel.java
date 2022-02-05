package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MotorCarousel {
	private static final double TELEOP_POWER = 1;
	private static final long ON_TIME = 1000;
	private static final long CYCLE_TIME = 1500;

	private LinearOpMode mode;

	private DcMotor spinner;
	private boolean spinning = false;

	private boolean endgameSpin = false;
	private boolean blueSide;
	private long switchTime;

	public MotorCarousel(HardwareMap hw, LinearOpMode m) {
		mode = m;
		spinner = hw.get(DcMotor.class, "duckSpinner");
	}

	public void blueSpin() {
		spinning = !spinning;
		spinner.setPower(spinning ? TELEOP_POWER : 0);
	}

	public void redSpin() {
		spinning = !spinning;
		spinner.setPower(spinning ? -TELEOP_POWER : 0);
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
		return (t > ON_TIME) ? 0 : TELEOP_POWER;
	}
}
