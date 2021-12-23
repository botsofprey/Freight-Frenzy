package DriveEngine;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.Location;
import UtilityClasses.Matrix;

public class Localizer {
	private MotorController[] driveMotors = new MotorController[4];
	private BNO055IMU imu;
	private long previousTime;
	private long[] previousPositions = new long[]{ 0, 0, 0, 0 };
	private double encoderCPR;
	private double wheelDiameter;

	private Location currentLocation;



	private void updateLocation() {//  calculate new position from odometry data
		long[] positions = new long[4];
		for (int i = 0; i < 4; i++) {
			positions[i] = driveMotors[i].getCurrentPosition();
		}
		long currentTime = System.nanoTime();

		long deltaTime = currentTime - previousTime;
		previousTime = currentTime;
		double timeDiff = deltaTime / 1_000_000_000.0;//convert nanoseconds to seconds
		double[] rotationAngles = new double[4];
		double[] motorDistances = new double[4];
		for (int i = 0; i < 4; i++) {
			positions[i] -= previousPositions[i];
			previousPositions[i] += positions[i];
			rotationAngles[i] = positions[i] / (encoderCPR * 2 * Math.PI);
			motorDistances[i] = Math.PI * wheelDiameter * positions[i] / encoderCPR;
//			motorSpeeds[i] = rotationAngles[i] / timeDiff;
//			motorRPMs[i] = rotationAngles[i] * 2 * Math.PI;
		}
		double currentRotation = imu.getAngularOrientation(AxesReference.INTRINSIC,
				AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
		double rotation = Math.toRadians(currentLocation.getHeading() - currentRotation);
		double xMovement =
				(motorDistances[0] + motorDistances[1] +
						motorDistances[2] + motorDistances[3]) * 0.25;
		double yMovement =
				(motorDistances[1] - motorDistances[0] +
						motorDistances[3] - motorDistances[2]) * 0.25;
		double currentHeading = -Math.toRadians(currentLocation.getHeading() + 90);

		Matrix vector = new Matrix(new double[][] {
				{xMovement,
						yMovement,
						rotation}
		});
		Matrix PoseExponential = new Matrix(3, 3);
		if (rotation != 0) {
			PoseExponential = new Matrix(new double[][]{
					{Math.sin(rotation) / rotation, (Math.cos(rotation) - 1) / rotation, 0},
					{(1 - Math.cos(rotation)) / rotation, Math.sin(rotation) / rotation, 0},
					{0, 0, 1}
			});
		}
		Matrix rotationMatrix = new Matrix(new double[][] {
				{ Math.cos(currentHeading), -Math.sin(currentHeading),  0 },
				{ Math.sin(currentHeading), Math.cos(currentHeading),   0 },
				{ 0,                        0,                          1}
		});
		vector.mul(PoseExponential);
		vector.mul(rotationMatrix);
		double[] movementVectors = vector.getData()[0];
		Location deltaLocation = new Location(movementVectors[0], movementVectors[1],
				currentRotation - currentLocation.getHeading());

		currentLocation.add(deltaLocation);
	}
}
