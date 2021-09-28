package LearnJava;

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
	//todo add variables such as motors here:
	
	
	public TankDrive(HardwareMap hardwareMap) {//todo use this to initialize motors and set them up
	
	}
	
	public void move(double inches, double inchesPerSecond) {//todo make this function move the robot the given distance forward, at the given speed
	
	}//todo once you are done, run this a few times to see if it is more consistent than using power and time
	
	public void turnLeft(double degrees, double inchesPerSecond) {//todo makes the robot turn to the left by the given amount, inches per second refer to how fast the wheels are spinning
		//tip: make the robot spin around without using encoders and watch how the wheels move, this should give you an idea of how far the wheels should move to make the robot rotate 360 degrees
	
	}//todo test the robot a few times and see if it is consistently working
	
	public void turnRight(double degrees, double inchesPerSecond) {//todo this is just like turnLeft, but the robot turns right
	
	}//todo once you make this function, rewrite the code you wrote last Friday using this class, it should work more consistently now
	
	public void turnToAngle(double angle, double inchesPerSecond) {//todo update some of the functions you have already written so that the robot keeps track of its heading(what angle it is turned to)
		//todo continued: use a variable to store this value, this function should use the robot's current heading and the given desired heading to find how it should turn
	
	}//todo test the function once it is done
}

//if you finish early, you can look through some of the other code files
//try to see if you can figure out what some of the code does
//some of the code uses complicated formulas, and I haven't commented the code much, so don't feel bad if you get lost with some bits of code
//I would recommend looking at UtilityClasses/Location and UtilityClasses/Controller first
//if you feel overwhelmed by the amount of new code, try focusing on small parts and think through what the computer does at each step (run through to code in your head)