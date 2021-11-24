package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MagneticLimitSwitch {
	private LinearOpMode mode;

	public DigitalChannel limit;
	private String name;
	private boolean throwErrors;

	private void error(Exception e) {
		e.printStackTrace();
		mode.telemetry.addData("Could not access limit switch", name);
	}

	public MagneticLimitSwitch(HardwareMap hw, String switchName, LinearOpMode m, boolean errors) {
		mode = m;
		name = switchName;
		throwErrors = errors;

		if (errors) {
			limit = hw.get(DigitalChannel.class, name);
		} else {
			try {
				limit = hw.get(DigitalChannel.class, name);
			} catch (IllegalArgumentException e) {
				error(e);
			}
		}
	}

	public boolean getState() {
		try {
			return limit.getState();
		}
		catch (NullPointerException e) {
			error(e);
			return false;
		}
	}
}
