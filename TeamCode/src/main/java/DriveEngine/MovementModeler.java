package DriveEngine;

import UtilityClasses.Matrix;

public class MovementModeler {
	 private double driveBaseConstant;
	 private double trackWidth;
	 private double wheelbase;
	 private double wheelRadius;
	 
	 public MovementModeler(double trackWidth, double wheelbase, double wheelRadius) {
	 	this.trackWidth = trackWidth;
	 	this.wheelbase = wheelbase;
	 	this.wheelRadius = wheelRadius;
	 	driveBaseConstant = (trackWidth + wheelbase) / 2.0;
	 }
	 
	 public double[] getWheelVelocities(double forwardVelocity, double leftVelocity,
	                                    double rotateUpVelocity) {
	 	Matrix movementVector = new Matrix(new double[][]{
			    {forwardVelocity},
			    {leftVelocity},
			    {rotateUpVelocity}
	    });
	 	Matrix calculate = new Matrix(new double[][]{
			    { 1, -1, -driveBaseConstant },
			    { 1,  1,  driveBaseConstant },
			    { 1, -1,  driveBaseConstant },
			    { 1,  1, -driveBaseConstant }
	    });
		 double[][] data = calculate.mul(movementVector).scale(1 / wheelRadius).getData();
		 double[] result = new double[4];
		 for (int i = 0; i < 4; i++) {
			 result[i] = data[i][0];
		 }
		 return result;
	 }
}
