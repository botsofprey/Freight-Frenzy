package UtilityClasses.HardwareWrappers;

/**
 * This class is used to provide the same interface for both magnetic and physical limit switches.
 * It is currently implemented for both magnetic limit switches and rev touch sensors.
 * If you add another type of limit switch, make it implement this interface and
 * make sure it returns true when not triggered and false when triggered.
 * This is used by the motor controller class for
 * automatically using limit switches to avoid damage.
 *
 * @author Alex Prichard
 */
public interface LimitSwitch {
	boolean getState();
}
