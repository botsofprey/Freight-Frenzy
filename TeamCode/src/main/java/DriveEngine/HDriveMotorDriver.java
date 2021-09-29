package DriveEngine;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.Matrix;

public class HDriveMotorDriver {
	private static final double CENTER_DISTANCE = 3.0;
	private static final double TRACK_WIDTH = 11.75;
	
	private DcMotorEx leftMotor;
	private DcMotorEx rightMotor;
	private DcMotorEx centerMotor;
	
	private boolean trueNorthEnabled;
	
	public HDriveMotorDriver(HardwareMap hw, boolean trueNorthEnabled) {
		leftMotor = hw.get(DcMotorEx.class, "left_motor");
		rightMotor = hw.get(DcMotorEx.class, "right_motor");
		centerMotor = hw.get(DcMotorEx.class, "center_motor");
		this.trueNorthEnabled = trueNorthEnabled;
		
		leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
		rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
		centerMotor.setDirection(DcMotorSimple.Direction.REVERSE);
	}
	
	public void moveRobot(double x, double y, double a) {
		Matrix forwardPowers = new Matrix(new double[][]{
				{ 1, 1, 0 }
		});
		forwardPowers.scale(y);
		
		Matrix rightMovement = new Matrix(new double[][]{
				{ 0, 0, 1 }
		});
		rightMovement.scale(x);
		
		Matrix rotation = new Matrix(new double[][]{
				{ -0.5, 0.5, CENTER_DISTANCE / TRACK_WIDTH }
		});
		rotation.scale(a);
		
		double[] powers = normalize(
				forwardPowers
						.add(rightMovement)
						.add(rotation)
						.getData()[0]
		);
		leftMotor.setPower(powers[0]);
		rightMotor.setPower(powers[1]);
		centerMotor.setPower(powers[2]);
	}
	
	private double[] normalize(double[] values) {
		double scaleFactor = 1;
		for (double value : values) {
			scaleFactor = Math.max(Math.abs(value), scaleFactor);
		}
		for (int i = 0; i < values.length; i++) {
			values[i] /= scaleFactor;
		}
		return values;
	}
}
