package UtilityClasses;

import com.qualcomm.robotcore.hardware.DcMotor;

public class MotorEncoder {
	private DcMotor encoder;
	
	private int position;
	private long timer;
	private int deltaTicks;
	private int deltaTime;
	
	private double ticksPerRevolution;
	private double ticksPerDegree;
	private double wheelDiameter;
	private double ticksPerInch;
	
	private void initFromConfigFile(String fileName) {
		JSONReader fileReader = null;
		try {
			fileReader = new JSONReader(fileName);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		ticksPerRevolution = fileReader.getDouble("ticks_per_revolution");
		wheelDiameter = fileReader.getDouble("wheel_diameter");
	}
	
	public MotorEncoder(DcMotor motor, String fileName) {
		encoder = motor;
		initFromConfigFile(fileName);
		encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		encoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		
		ticksPerInch = ticksPerRevolution / (wheelDiameter * Math.PI);
		ticksPerDegree = ticksPerRevolution / 360.0;
		
		position = 0;
		deltaTime = 0;
		deltaTicks = 0;
		timer = System.currentTimeMillis();
	}
	
	public void update() {
		int newTicks = encoder.getCurrentPosition();
		long newTime = System.currentTimeMillis();
		deltaTicks = newTicks - position;
		deltaTime = (int)(newTime - timer);
		position = newTicks;
		timer = newTime;
	}
	
	public double getWheelDiameter() {
		return wheelDiameter;
	}
	public double getTicksPerRevolution() {
		return ticksPerRevolution;
	}
	public double getTicksPerDegree() {
		return ticksPerDegree;
	}
	public int getCurrentTick() {
		return position;
	}
	public double getInchesFromStart() {
		return position / ticksPerInch;
	}
	public double getCurrentDegree() {
		return position / ticksPerDegree;
	}
	public double getCurrentTicksPerSecond() {
		return deltaTicks / (deltaTime * 1000.0);
	}
	public double getCurrentInchesPerSecond() {
		return getCurrentTicksPerSecond() / ticksPerInch;
	}
	public double getCurrentRPS() {
		return getCurrentTicksPerSecond() / ticksPerRevolution;
	}
	public double getCurrentRPM() {
		return getCurrentRPS() * 60;
	}
}
