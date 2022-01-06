package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class RevTouchSensor implements LimitSwitch {
	public TouchSensor limit;

	public RevTouchSensor(HardwareMap hw, String switchName) {
		limit = hw.get(TouchSensor.class, switchName);
	}

	@Override
	public boolean getState() {
		return !limit.isPressed();
	}
}
