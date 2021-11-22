package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Objects;

public class ServoController {
	private LinearOpMode mode;

	public Servo servo;
	private String name;

	private void error(Exception e) {
		e.printStackTrace();
		mode.telemetry.addData("Could not access servo", name);
	}

	public ServoController(HardwareMap hw, String servoName, LinearOpMode m) {
		mode = m;
		name = servoName;

		servo = null;
		try {
			servo = hw.get(Servo.class, name);
		}
		catch (IllegalArgumentException e) {
			error(e);
		}
	}

	public void setPosition(double position) {
		try {
			servo.setPosition(position);
		}
		catch (NullPointerException e) {
			error(e);
		}
	}

	public double getPosition() {
		try {
			return servo.getPosition();
		}
		catch (NullPointerException e) {
			error(e);
			return 0;
		}
	}

	public void setDirection(Servo.Direction direction) {
		try {
			servo.setDirection(direction);
		}
		catch (NullPointerException e) {
			error(e);
		}
	}

	public Servo.Direction getDirection() {
		try {
			return servo.getDirection();
		}
		catch (NullPointerException e) {
			error(e);
			return Servo.Direction.FORWARD;
		}
	}


	@Override
	public String toString() {
		return "ServoController{" +
				"mode=" + mode +
				", servo=" + servo +
				", name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServoController that = (ServoController) o;
		return mode.equals(that.mode) && servo.equals(that.servo) && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, servo, name);
	}
}
