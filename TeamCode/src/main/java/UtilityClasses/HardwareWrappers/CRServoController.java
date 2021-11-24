package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Objects;

public class CRServoController {
	private LinearOpMode mode;

	public CRServo servo;
	private String name;
	private boolean throwErrors;

	private void error(Exception e) {
		e.printStackTrace();
		mode.telemetry.addData("Could not access CRServo", name);
		if (throwErrors) {
			throw new Error("Could not access CRServo: " + name);
		}
	}

	public CRServoController(HardwareMap hw, String servoName, LinearOpMode m, boolean errors) {
		mode = m;
		name = servoName;
		throwErrors = errors;

		if (errors) {
			servo = hw.get(CRServo.class, name);
		}
		else {
			try {
				servo = hw.get(CRServo.class, name);
			} catch (IllegalArgumentException e) {
				error(e);
			}
		}
	}

	public void setPower(double position) {
		try {
			servo.setPower(position);
		}
		catch (NullPointerException e) {
			error(e);
		}
	}

	public double getPower() {
		try {
			return servo.getPower();
		}
		catch (NullPointerException e) {
			error(e);
			return 0;
		}
	}

	public void setDirection(DcMotorSimple.Direction direction) {
		try {
			servo.setDirection(direction);
		}
		catch (NullPointerException e) {
			error(e);
		}
	}

	public DcMotorSimple.Direction getDirection() {
		try {
			return servo.getDirection();
		}
		catch (NullPointerException e) {
			error(e);
			return DcMotorSimple.Direction.FORWARD;
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
		CRServoController that = (CRServoController) o;
		return mode.equals(that.mode) && servo.equals(that.servo) && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, servo, name);
	}
}
