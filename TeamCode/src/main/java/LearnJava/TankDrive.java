package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Instructions:
 * write code for the functions here to make a class for moving the robot
 * use motor encoders for more precise movement
 * work from top to bottom
 * if you don't know if you code will work, try it and see what happens
 * don't be afraid to use google
 * you will have to use a bit of math
 * if you have questions about the robot's hardware, ask Cole or Caleb
 */

//comments that have the word todo in them turn yellow


public class TankDrive {
	private static final double TICKS_PER_INCH = 560.0 / (4 * Math.PI);
	private static final double TRACK_WIDTH = 11;

	public DcMotorEx leftMotor;
	public DcMotorEx rightMotor;
	private LinearOpMode mode;

	public TankDrive(HardwareMap hardwareMap, LinearOpMode mode) {
		leftMotor = hardwareMap.get(DcMotorEx.class, "left_motor");
		rightMotor = hardwareMap.get(DcMotorEx.class, "right_motor");
		this.mode = mode;

		leftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
		rightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
	}
	
	public void move(double inches, double inchesPerSecond) {
		leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//this stops the motor and defines its current position as zero
		rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

		leftMotor.setTargetPosition((int)(inches * TICKS_PER_INCH));
		rightMotor.setTargetPosition((int)(inches * TICKS_PER_INCH));

		leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);//the motor is told to go to the target location
		rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

		leftMotor.setVelocity(inchesPerSecond * TICKS_PER_INCH);//sets the maximum number of ticks per second the motor can move
		rightMotor.setVelocity(inchesPerSecond * TICKS_PER_INCH);

		while (leftMotor.isBusy() && mode.opModeIsActive()) {}//waits for the motor to finish moving before continuing
		while (rightMotor.isBusy() && mode.opModeIsActive()) {}//waits for the motor to finish moving before continuing
		mode.sleep(250);
	}
	
	public void turnLeft(double degrees, double inchesPerSecond) {
		double turnInches = TRACK_WIDTH * Math.PI * degrees / 360;

		leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//this stops the motor and defines its current position as zero
		rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

		leftMotor.setTargetPosition((int)(-turnInches * TICKS_PER_INCH));
		rightMotor.setTargetPosition((int)(turnInches * TICKS_PER_INCH));

		leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);//the motor is told to go to the target location
		rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

		leftMotor.setVelocity(inchesPerSecond * TICKS_PER_INCH);//sets the maximum number of ticks per second the motor can move
		rightMotor.setVelocity(inchesPerSecond * TICKS_PER_INCH);

		while (leftMotor.isBusy() && mode.opModeIsActive()) {}//waits for the motor to finish moving before continuing
		while (rightMotor.isBusy() && mode.opModeIsActive()) {}//waits for the motor to finish moving before continuing
		mode.sleep(250);
	}
	
	public void turnRight(double degrees, double inchesPerSecond) {
		turnLeft(-degrees, inchesPerSecond);
	}
	
	public void turnToAngle(double angle, double inchesPerSecond) {//todo update some of the functions you have already written so that the robot keeps track of its heading(what angle it is turned to)
		//todo continued: use a variable to store this value, this function should use the robot's current heading and the given desired heading to find how it should turn
	
	}//todo test the function once it is done
}

//if you finish early, you can look through some of the other code files
//try to see if you can figure out what some of the code does
//some of the code uses complicated formulas, and I haven't commented the code much, so don't feel bad if you get lost with some bits of code
//I would recommend looking at UtilityClasses/Location and UtilityClasses/Controller first
//if you feel overwhelmed by the amount of new code, try focusing on small parts and think through what the computer does at each step (run through to code in your head)