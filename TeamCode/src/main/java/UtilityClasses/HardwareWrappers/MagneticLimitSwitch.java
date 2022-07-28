package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * This is a wrapper for magnetic limit switches to provide easy initialization.
 * It also implements LimitSwitch, so it can be used in MotorController.
 * Feel free to add methods to this as needed.
 *
 * @author Alex Prichard
 */
public class MagneticLimitSwitch implements LimitSwitch {
	public DigitalChannel limit;

	public MagneticLimitSwitch(HardwareMap hw, String switchName) {
		limit = hw.get(DigitalChannel.class, switchName);
	}

	@Override
	public boolean getState() { return limit.getState(); }
}
