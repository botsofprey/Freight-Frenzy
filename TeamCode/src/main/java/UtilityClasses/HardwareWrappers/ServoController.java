package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Objects;

/**
 * This class is a wrapper for a regular(not continuous) servo.
 * It allows for easier and cleaner initialization.
 * Feel free to add any additional needed functionality.
 *
 * @author Alex Prichard
 */
public class ServoController {
	public Servo servo;

	public ServoController(HardwareMap hw, String servoName) {
		servo = hw.get(Servo.class, servoName);
	}

	public void setPosition(double position) {
		servo.setPosition(position);
	}

	public double getPosition() {
		return servo.getPosition();
	}

	public void setDirection(Servo.Direction direction) {
		servo.setDirection(direction);
	}

	public Servo.Direction getDirection() {
		return servo.getDirection();
	}


	@Override
	public String toString() {
		return "ServoController{" +
				"servo=" + servo +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServoController that = (ServoController) o;
		return servo.equals(that.servo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(servo);
	}
}
