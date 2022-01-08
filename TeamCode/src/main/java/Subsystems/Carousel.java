package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.HardwareWrappers.CRServoController;

public class Carousel {
	private CRServoController lCarousel;
	private CRServoController rCarousel;
	private LinearOpMode mode;

	public Carousel(HardwareMap hardwareMap, LinearOpMode m, boolean errors){
		mode = m;
		lCarousel = new CRServoController(hardwareMap,
				"leftDuckSpinner");
		rCarousel = new CRServoController(hardwareMap,
				"rightDuckSpinner");
	}

	public void rotate() {
		lCarousel.setPower(-0.4);
		rCarousel.setPower(0.4);
	}

	public void stop() {
		lCarousel.setPower(0);
		rCarousel.setPower(0);
	}
}
