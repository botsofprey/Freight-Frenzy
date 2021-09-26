package UtilityClasses;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Encoder {
	private DcMotor encoder;
	
	private int direction;
	
	private int position;
	private long timer;
	private int deltaTicks;
	private int deltaTime;
	
	private double ticksPerRevolution;
	private double ticksPerDegree;
	private double wheelDiameter;
	private double ticksPerInch;
	
	private void initFromConfigFile(HardwareMap hardwareMap, String fileName) {
		JSONReader fileReader = null;
		try {
			fileReader = new JSONReader(hardwareMap, fileName);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		assert fileReader != null;
		ticksPerRevolution = fileReader.getDouble("ticks_per_revolution");
		wheelDiameter = fileReader.getDouble("wheel_diameter");
	}
	
	public Encoder(HardwareMap hardwareMap, DcMotor motor, String fileName, int direction) {
		encoder = motor;
		initFromConfigFile(hardwareMap, fileName);
		encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		encoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		
		this.direction = direction;
		ticksPerInch = ticksPerRevolution / (wheelDiameter * Math.PI);
		ticksPerDegree = ticksPerRevolution / 360.0;
		
		position = 0;
		deltaTime = 0;
		deltaTicks = 0;
		timer = System.currentTimeMillis();
	}
	
	public Encoder(HardwareMap hardwareMap, DcMotor motor, String fileName) {
		this(hardwareMap, motor, fileName, 1);
	}
	
	public Encoder(HardwareMap hardwareMap, String motorName, String fileName) {
		this(hardwareMap, hardwareMap.get(DcMotor.class, motorName), fileName);
	}
	
	public Encoder(HardwareMap hardwareMap, String motorName, String fileName, int direction) {
		this(hardwareMap, hardwareMap.get(DcMotor.class, motorName), fileName, direction);
	}
	
	public void update() {
		int newTicks = encoder.getCurrentPosition() * direction;
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
	public double getTickDiff() { return deltaTicks; }
	public double getDegreeDiff() { return deltaTicks / ticksPerDegree; }
	public double getInchesDiff() { return deltaTicks / ticksPerInch; }
}
