package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Objects;

/**
 * This is a wrapper class for a continuous rotation servo.
 * It makes initialization a bit more clean.
 * Feel free to add more methods to this class if you need them.
 * You may want to replicate the automatic limit switch handling from MotorController
 * as we have used a continuous rotation servo with limit switches before.
 *
 * @author Alex Prichard
 */
public class CRServoController {
	public CRServo servo;
	private String name;

	public CRServoController(HardwareMap hw, String servoName) {
		name = servoName;

		servo = hw.get(CRServo.class, name);
	}

	public void setPower(double position) {
		servo.setPower(position);
	}

	public double getPower() {
		return servo.getPower();
	}

	public void setDirection(DcMotorSimple.Direction direction) {
		servo.setDirection(direction);
	}

	public DcMotorSimple.Direction getDirection() {
		return servo.getDirection();
	}


	@Override
	public String toString() {
		return "ServoController{" +
				", servo=" + servo +
				", name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CRServoController that = (CRServoController) o;
		return servo.equals(that.servo) && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(servo, name);
	}
}
