package UtilityClasses;

import android.view.Display;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DriveMotor {
	private DcMotorEx motor;
	private double ticksPerRevolution;
	private double maxSpeed;
	private double frictionRatio;
	
	private double targetPower;
	
	public void initializeFromConfigFile(HardwareMap hw, String fileName) {
		JSONReader reader = new JSONReader(hw, fileName);
		ticksPerRevolution = reader.getDouble("ticks_per_revolution");
		double wheelRadius = reader.getDouble("wheel_radius");
		double maxRPM = reader.getDouble("max_rpm");
		maxSpeed = Math.PI * wheelRadius * maxRPM / 30.0;//convert to inches per second
	}
	
	public DriveMotor(HardwareMap hw, DcMotorEx m, String fileName,
	                  double frictionRatio) {
		motor = m;
		motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		this.frictionRatio = frictionRatio;
		initializeFromConfigFile(hw, fileName);
	}
	
	public DriveMotor(HardwareMap hw, String motorName, String fileName,
	                  double frictionRatio) {
		this(hw, hw.get(DcMotorEx.class, motorName), fileName, frictionRatio);
	}
	
	public void setDirection(DcMotorSimple.Direction direction) {
		motor.setDirection(direction);
	}
	
	public void setTargetPower(double targetPower) {
		this.targetPower = targetPower;
	}
	
	public void update(double currentSpeed) {
		currentSpeed = Math.abs(currentSpeed);
		currentSpeed = Math.min(currentSpeed, maxSpeed);
		double speedRatio = currentSpeed / maxSpeed;
		double newPower = Math.min(1.0, frictionRatio / Math.abs(1.0 - speedRatio));
		motor.setPower(Math.copySign(Math.min(newPower, Math.abs(targetPower)), targetPower));
	}
}
