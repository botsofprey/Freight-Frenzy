package Subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Carousel {
	private CRServo carousel;

	public Carousel(HardwareMap hardwareMap){
		carousel = hardwareMap.get(CRServo.class, "carousel_servo");

	}

	public void rotate() {
		carousel.setPower(0.4);
	}

	public void stop() {
		carousel.setPower(0);
	}
}
