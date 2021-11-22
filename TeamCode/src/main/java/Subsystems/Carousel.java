package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.HardwareWrappers.CRServoController;

public class Carousel {
	private CRServoController carousel;
	private LinearOpMode mode;

	public Carousel(HardwareMap hardwareMap, LinearOpMode m){
		mode = m;
		try {
			carousel = new CRServoController(hardwareMap, "carousel_servo", mode);
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			mode.telemetry.addData("Could not reach servo", "carousel_servo");
		}
	}

	public void rotate() {
		try {
			carousel.setPower(0.4);
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			mode.telemetry.addData("Could not reach servo", "carousel_servo");
		}
	}

	public void stop() {
		try {
			carousel.setPower(0);
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			mode.telemetry.addData("Could not reach servo", "carousel_servo");
		}
	}
}
