package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.HardwareWrappers.CRServoController;

public class Carousel {
	private CRServoController carousel;
	private CRServoController carousel1;
	private LinearOpMode mode;

	public Carousel(HardwareMap hardwareMap, LinearOpMode m, boolean errors){
		mode = m;
		carousel = new CRServoController(hardwareMap,
				"carousel_servo", mode, errors);
		carousel1 = new CRServoController(hardwareMap,
				"carousel1", mode, errors);
	}

	public void rotate() {
		carousel.setPower(-0.4);
		carousel1.setPower(0.4);
	}

	public void stop() {
		carousel.setPower(0);
		carousel1.setPower(0);
	}
}
