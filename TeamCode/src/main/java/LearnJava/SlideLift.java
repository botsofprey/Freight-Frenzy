package LearnJava;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SlideLift {

	private DcMotor slide;

	public SlideLift(HardwareMap hardwareMap){
		slide = hardwareMap.get(DcMotor.class, "Slider");

	}
}
