package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.HardwareWrappers.CRServoController;

public class  Carousel {
	private CRServoController lCarousel;
	private CRServoController rCarousel;
	private LinearOpMode mode;
	private boolean power;
	private boolean endgameSpin;
	private long switchTime;

	private static final long ON_TIME = 1750;
	private static final long OFF_TIME = 500;

	public Carousel(HardwareMap hardwareMap, LinearOpMode m, boolean errors) {
		mode = m;
		lCarousel = new CRServoController(hardwareMap,
				"leftDuckSpinner");
		rCarousel = new CRServoController(hardwareMap,
				"rightDuckSpinner");
		power = false;
		endgameSpin = false;
	}

	public void autoRotate() {
		lCarousel.setPower(-1.0 / 4);
		rCarousel.setPower(1.0 / 4);
		power = true;
	}

	public void rotate() {
		lCarousel.setPower(-1);
		rCarousel.setPower(1);
		power = true;
	}

	public void stop() {
		lCarousel.setPower(0);
		rCarousel.setPower(0);
		power = false;
	}

	public void toggleEndgameRotate() {
		endgameSpin = !endgameSpin;
		power = endgameSpin;
		if (power) rotate();
		else stop();
		switchTime = System.currentTimeMillis() + ON_TIME;
	}

	public void update(long millis) {
		if (endgameSpin && switchTime <= millis) {
			power = !power;
			double servoPower = power ? 1 : 0;
			lCarousel.setPower(-servoPower);
			rCarousel.setPower(servoPower);
			switchTime = millis + (power ? ON_TIME : OFF_TIME);
		}
	}
}
