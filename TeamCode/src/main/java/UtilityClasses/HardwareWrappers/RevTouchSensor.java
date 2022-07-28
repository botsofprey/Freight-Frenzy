package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * This class allows for easy initialization of a touch sensor.
 * It implements LimitSwitch, so it can bee used automatically by MotorController.
 * Feel free to add functionality to this class as needed.
 *
 * @author Alex Prichard
 */
public class RevTouchSensor implements LimitSwitch {
	public TouchSensor limit;

	public RevTouchSensor(HardwareMap hw, String switchName) {
		limit = hw.get(TouchSensor.class, switchName);
	}

	@Override
	public boolean getState() { return !limit.isPressed(); }
}
