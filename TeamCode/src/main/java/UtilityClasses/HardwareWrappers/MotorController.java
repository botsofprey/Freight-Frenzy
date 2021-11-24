package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Objects;

public class MotorController {
	private LinearOpMode mode;

	public DcMotorEx motor;
	private String name;

	private boolean throwErrors;

	private void error(Exception e) {
		e.printStackTrace();
		mode.telemetry.addData("Could not access motor", name);
		if (throwErrors) {
			throw new Error("Could not access motor: " + name);
		}
	}

	public MotorController(HardwareMap hw, String motorName, LinearOpMode m, boolean errors) {
		mode = m;
		name = motorName;
		throwErrors = errors;

		if (errors) {
			motor = hw.get(DcMotorEx.class, name);
		}
		else {
			try {
				motor = hw.get(DcMotorEx.class, name);
			} catch (IllegalArgumentException e) {
				error(e);
			}
		}
	}

	public void setMode(DcMotor.RunMode runMode) {
		try {
			motor.setMode(runMode);
		}
		catch (NullPointerException e) {
			error(e);
		}
	}

	public void setPower(double power) {
		try {
			motor.setPower(power);
		}
		catch (NullPointerException e) {
			error(e);
		}
	}

	public void setDirection(DcMotorSimple.Direction direction) {
		try {
			motor.setDirection(direction);
		}
		catch (NullPointerException e) {
			error(e);
		}
	}

	public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
		try {
			motor.setZeroPowerBehavior(zeroPowerBehavior);
		}
		catch (NullPointerException e) {
			error(e);
		}
	}

	public int getCurrentPosition() {
		try {
			return motor.getCurrentPosition();
		}
		catch (NullPointerException e) {
			error(e);
			return 0;
		}
	}

	public void setVelocity(double velocity) {
		try {
			motor.setVelocity(velocity);
		}
		catch (NullPointerException e) {
			error(e);
		}
	}

	public void setTargetPosition(int targetPosition) {
		try {
			motor.setTargetPosition(targetPosition);
		}
		catch (NullPointerException e) {
			error(e);
		}
	}

	public double getPower() {
		try {
			return motor.getPower();
		}
		catch (NullPointerException e) {
			error(e);
			return 0;
		}
	}

	public boolean isBusy() {
		try {
			return motor.isBusy();
		}
		catch (NullPointerException e) {
			error(e);
			return false;
		}
	}


	@Override
	public String toString() {
		return "MotorController{" +
				"mode=" + mode +
				", motor=" + motor +
				", name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MotorController that = (MotorController) o;
		return mode.equals(that.mode) && motor.equals(that.motor) && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, motor, name);
	}
}
