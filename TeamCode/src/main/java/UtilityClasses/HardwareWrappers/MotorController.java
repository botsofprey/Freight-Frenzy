package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Objects;

public class MotorController {
	public DcMotorEx motor;

	public MotorController(HardwareMap hw, String motorName, LinearOpMode m, boolean errors) {
		motor = hw.get(DcMotorEx.class, motorName);
	}

	public void setMode(DcMotor.RunMode runMode) {
		motor.setMode(runMode);
	}

	public void setPower(double power) {
		motor.setPower(power);
	}

	public void setDirection(DcMotorSimple.Direction direction) {
		motor.setDirection(direction);
	}

	public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
		motor.setZeroPowerBehavior(zeroPowerBehavior);
	}

	public int getCurrentPosition() {
		return motor.getCurrentPosition();
	}

	public void setVelocity(double velocity) {
		motor.setVelocity(velocity);
	}

	public void setTargetPosition(int targetPosition) {
		motor.setTargetPosition(targetPosition);
	}

	public double getPower() {
		return motor.getPower();
	}

	public boolean isBusy() {
		return motor.isBusy();
	}


	@Override
	public String toString() {
		return "MotorController{" +
				"motor=" + motor +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MotorController that = (MotorController) o;
		return motor.equals(that.motor);
	}

	@Override
	public int hashCode() {
		return Objects.hash(motor);
	}
}
