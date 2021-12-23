package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MagneticLimitSwitch {
	public DigitalChannel limit;

	public MagneticLimitSwitch(HardwareMap hw, String switchName) {
		limit = hw.get(DigitalChannel.class, switchName);
	}

	public boolean getState() {
		return limit.getState();
	}
}
