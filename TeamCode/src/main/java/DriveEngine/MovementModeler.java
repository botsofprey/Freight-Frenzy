package DriveEngine;

import static DataFiles.DriveBaseConstants.*;

import UtilityClasses.Matrix;

public class MovementModeler {
	 private double DRIVE_BASE_CONSTANT = (TRACK_WIDTH + WHEELBASE) / 2;
	 
	 public double[] getWheelVelocities(double forwardVelocity, double leftVelocity,
	                                    double rotateUpVelocity) {
	 	Matrix movementVector = new Matrix(new double[][]{
			    {forwardVelocity},
			    {leftVelocity},
			    {rotateUpVelocity}
	    });
	 	Matrix calculate = new Matrix(new double[][]{
			    { 1, -1, -DRIVE_BASE_CONSTANT },
			    { 1,  1,  DRIVE_BASE_CONSTANT },
			    { 1, -1,  DRIVE_BASE_CONSTANT },
			    { 1,  1, -DRIVE_BASE_CONSTANT }
	    });
		 double[][] data = calculate.mul(movementVector).scale(1 / WHEEL_RADIUS).getData();
		 double[] result = new double[4];
		 for (int i = 0; i < 4; i++) {
			 result[i] = data[i][0];
		 }
		 return result;
	 }
}
