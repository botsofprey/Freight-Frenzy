package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Objects;

/**
 * This class is a wrapper for a motor.
 * It provides easy initialization and automatic limit switch handling.
 * Built in limit switch handling is optional.
 * If you do not have any limit switches being handled by this class,
 * you do not need to call the update function.
 * If you do give this class limit switches to handle, you must call the update function
 * every frame or otherwise call the update function a few times per second.
 * If the class detects an activated limit switch and it is moving in the direction
 * of the limit switch, it will automatically set the motor's power to zero to protect the robot.
 * Make sure the limit switch is set to the correct start/end variable.
 * If it is set to the wrong one, the class will not work and the motor will not function properly.
 * The automatic limit switch functionality of this class has not been
 * extensively tested, and may need some debugging before it works.
 * Feel free to add methods to this class as needed.
 *
 * @author Alex Prichard
 */
public class MotorController {
	private DcMotorEx motor;
	private LimitSwitch start = null;
	private LimitSwitch end = null;

	public MotorController(HardwareMap hw, String motorName) {
		motor = hw.get(DcMotorEx.class, motorName);
	}

	public void setStartSwitch(HardwareMap hw, String switchName) {
		start = new MagneticLimitSwitch(hw, switchName);
	}

	public void setEndSwitch(HardwareMap hw, String switchName) {
		end = new MagneticLimitSwitch(hw, switchName);
	}

	public void setSwitches(HardwareMap hw, String startName, String endName) {
		start = new MagneticLimitSwitch(hw, startName);
		end = new MagneticLimitSwitch(hw, endName);
	}

	public void setStartSwitch(LimitSwitch s) {
		start = s;
	}

	public void setEndSwitch(LimitSwitch e) {
		end = e;
	}

	public void setSwitches(LimitSwitch s, LimitSwitch e) {
		start = s;
		end = e;
	}

	public void setMode(DcMotor.RunMode runMode) {
		motor.setMode(runMode);
	}

	public void setPower(double power) {
		if (start != null && !start.getState() && power < 0) {
			return;
		}
		if (end != null && !end.getState() && power > 0) {
			return;
		}
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

	public void setPositionPIDFCoefficients(double p) {
		motor.setPositionPIDFCoefficients(p);
	}

	public double getPower() {
		return motor.getPower();
	}

	public boolean isBusy() {
		return motor.isBusy();
	}

	public void update() {
		if (start != null && !start.getState() && getPower() < 0) {
			motor.setPower(0);
		}
		if (end != null && !end.getState() && getPower() > 0) {
			motor.setPower(0);
		}
	}

	protected DcMotorEx getMotor() {
		return motor;
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
