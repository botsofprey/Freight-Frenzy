package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MagneticLimitSwitch implements LimitSwitch {
	public DigitalChannel limit;

	public MagneticLimitSwitch(HardwareMap hw, String switchName) {
		limit = hw.get(DigitalChannel.class, switchName);
	}

	@Override
	public boolean getState() {
		return limit.getState();
	}
}
