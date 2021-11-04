package LearnJava;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SlideLift {
	private static final double TICKS_PER_INCH = 537.7 / (0.91 * Math.PI);


	private DcMotorEx slide;

	public SlideLift(HardwareMap hardwareMap) {
		slide = hardwareMap.get(DcMotorEx.class, "Slider");
		slide.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

	}

	public void move(double height, double inchesPerSecond) {
		height -= 7 ;

		slide.setTargetPosition((int)(height * TICKS_PER_INCH));
		slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		slide.setVelocity(inchesPerSecond * TICKS_PER_INCH);
	}

	public double getCurrentHeight(){
		return 7 + slide.getCurrentPosition() / TICKS_PER_INCH;
	}
}
