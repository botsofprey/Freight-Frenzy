package UtilityClasses;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p style="color:#FF0000";>
 * DOES NOT CURRENTLY WORK
 * </p>
 * <p>
 * This is an experimental class for saving and replaying a tele op game.
 * It should store a list of TimeSteps, each of which stores the power of each motor and cr servo,
 * the position of each servo, and the time it was recorded.
 * After the tele op is done, the class can be saved to a file on the control hub
 * and later replayed in an autonomous op mode.
 * </p>
 * <p>
 * Currently, saving the data to a file does not work because
 * the directory the code tries to save to is read only.
 * </p>
 */
public class OutputCapture {
	//stores all powers and positions at one moment in time
	private class TimeStep implements Serializable {
		public long timeStamp;
		public double[] motorPowers;
		public double[] servoPositions;
		public double[] crServoPowers;
		
		public TimeStep(long time, DcMotor[] motors, Servo[] servos, CRServo[] crServos) {
			timeStamp = time;
			motorPowers = new double[motors.length];
			for (int i = 0; i < motors.length; i++)
				motorPowers[i] = motors[i].getPower();
			servoPositions = new double[servos.length];
			for (int i = 0; i < servos.length; i++)
				servoPositions[i] = servos[i].getPosition();
			crServoPowers = new double[crServos.length];
			for (int i = 0; i < crServos.length; i++)
				crServoPowers[i] = crServos[i].getPower();
		}
		public TimeStep(long time, OutputCapture capture) {
			this(time, capture.motors, capture.servos, capture.crServos);
		}
		
		public void output(DcMotor[] motors, Servo[] servos, CRServo[] crServos) {
			for (int i = 0; i < motors.length; i++) {
				motors[i].setPower(motorPowers[i]);
			}
			for (int i = 0; i < servos.length; i++) {
				servos[i].setPosition(servoPositions[i]);
			}
			for (int i = 0; i < crServos.length; i++) {
				crServos[i].setPower(crServoPowers[i]);
			}
		}
		public void output(OutputCapture capture) {
			output(capture.motors, capture.servos, capture.crServos);
		}
	}
	
	
	private ArrayList<TimeStep> timeSteps;
	
	private DcMotor[] motors;
	private Servo[] servos;
	private CRServo[] crServos;
	
	private long startTime = System.nanoTime();
	private boolean capturing = false;
	
	public OutputCapture(HardwareMap hw) {
		motors = hw.getAll(DcMotor.class).toArray(new DcMotor[0]);
		servos = hw.getAll(Servo.class).toArray(new Servo[0]);
		crServos = hw.getAll(CRServo.class).toArray(new CRServo[0]);
	}
	
	public void startCapturing(long nanos) {
		startTime = nanos;
		capturing = true;
		timeSteps = new ArrayList<>();
		timeSteps.add(new TimeStep(0, this));
	}
	public void capture(long nanos) {
		if (capturing) timeSteps.add(new TimeStep(nanos - startTime, this));
	}
	public void pause() {
		capturing = false;
	}
	public void resume() {
		capturing = true;
	}
	
	//writes a list of time steps to the specified file
	public void store(String fileName) {
		try {
			FileOutputStream file = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(file);
			for (TimeStep timeStep : timeSteps)
				out.writeObject(timeStep);
			out.close();
			file.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//reads a list of time steps from the specified file
	public void retrieve(String fileName) {
		try {
			FileInputStream file = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(file);
			Object obj = null;
			timeSteps = new ArrayList<>();
			while (true) {
				try {
					obj = in.readObject();
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				if (obj != null) {
					timeSteps.add((TimeStep) obj);
				}
				else {
					in.close();
					file.close();
					return;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void replayTimeSteps(LinearOpMode mode) {
		startTime = System.nanoTime();
		for (TimeStep timeStep : timeSteps) {
			if (!mode.opModeIsActive()) return;
			timeStep.output(this);
			long time = System.nanoTime() - startTime;
			long diff = timeStep.timeStamp - time;
			long millis = diff / 1_000_000L;
			int nanos = (int)(diff % 1_000_000L);
			try {
				Thread.sleep(millis, nanos);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isCapturing() {
		return capturing;
	}
}
