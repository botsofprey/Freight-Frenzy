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
