package DriveEngine.Deprecated;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import java.util.List;

import UtilityClasses.HardwareWrappers.MotorController;
import UtilityClasses.JSONReader;
import UtilityClasses.Deprecated.OldLocationClass;
import UtilityClasses.Deprecated.Matrix;

@Deprecated
public class OldLocalizerClass {
	private static final String[] MOTOR_NAMES = {
			"frontLeftDriveMotor",
			"backLeftDriveMotor",
			"backRightDriveMotor",
			"frontRightDriveMotor"
	};
	private MotorController[] driveMotors = new MotorController[4];
	private BNO055IMU imu;
	private long previousTime;
	private long[] positions = new long[]{ 0, 0, 0, 0 };
	private long[] previousPositions;
	private double[] motorSpeeds;
	public double[] motorRPMs;
	private double encoderCPR;
	private double wheelDiameter;

	private OldLocationClass currentLocation;
	private double initHeading;

	
	
	public OldLocalizerClass(HardwareMap hw, String fileName, OldLocationClass startLocation, LinearOpMode m) {
		initFromConfig(hw, fileName, m);
		
		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
		
		parameters.mode                = BNO055IMU.SensorMode.IMU;
		parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
		parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		parameters.loggingEnabled      = false;
		
		// Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
		// on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
		// and named "imu".
		imu = hw.get(BNO055IMU.class, "imu");
		
		imu.initialize(parameters);
		
		List<LynxModule> allHubs = hw.getAll(LynxModule.class);
		for (LynxModule hub : allHubs) {
			hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
		}
		
		currentLocation = startLocation;
		initHeading = startLocation.getHeading();
		previousPositions = new long[] { 0, 0, 0, 0 };
		motorSpeeds = new double[] { 0, 0, 0, 0 };
		motorRPMs = new double[] { 0, 0, 0, 0 };
		previousTime = System.nanoTime();
	}
	
	private void initFromConfig(HardwareMap hw, String fileName, LinearOpMode m) {
		JSONReader reader = new JSONReader(hw, fileName);
		for (int i = 0; i < 4; i++) {
			String motorName = reader.getString(MOTOR_NAMES[i] + "Name");
			driveMotors[i] = new MotorController(hw, motorName);
			driveMotors[i].setDirection(
					reader.getString(MOTOR_NAMES[i] + "Direction").equals("forward") ?
							DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE
			);
		}
		JSONReader motorReader = new JSONReader(hw, reader.getString("driveMotorFile"));
		encoderCPR = motorReader.getDouble("ticks_per_revolution");
		wheelDiameter = motorReader.getDouble("wheel_diameter");
	}
	
	/**
	 * Calculates the current location based on the previous location and the robot's movement.
	 * For an explanation of the algorithm implemented here see the link.
	 * <a href=https://gm0.org/en/latest/docs/software/odometry.html>Explanation link</a>
	 *
	 * @author Alex Prichard
	 */
	public void updateLocation() { // calculate new position from odometry
		for (int i = 0; i < 4; i++) {
			positions[i] = driveMotors[i].getCurrentPosition();
		}
		long currentTime = System.nanoTime();
		
		long deltaTime = currentTime - previousTime;
		previousTime = currentTime;
		double timeDiff = deltaTime / 1_000_000_000.0; // convert nanoseconds to seconds
		double[] rotationAngles = new double[4];
		double[] motorDistances = new double[4];
		for (int i = 0; i < 4; i++) {
			positions[i] -= previousPositions[i];
			previousPositions[i] += positions[i];
			rotationAngles[i] = positions[i] / (encoderCPR * 2 * Math.PI);
			motorDistances[i] = Math.PI * wheelDiameter * positions[i] / encoderCPR;
			motorSpeeds[i] = rotationAngles[i] / timeDiff;
			motorRPMs[i] = rotationAngles[i] * 2 * Math.PI;
		}
		// this code just calculates how far forward, sideways, and rotationally we have moved
		double currentRotation = imu.getAngularOrientation(AxesReference.INTRINSIC,
				AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + initHeading;
		double rotation = Math.toRadians(currentLocation.getHeading() - currentRotation);
		double xMovement =
				(motorDistances[0] + motorDistances[1] + // 0.497 is a manually tuned constant
						motorDistances[2] + motorDistances[3]) * 0.25 * 0.497;
		double yMovement =
				(motorDistances[1] - motorDistances[0] + // 0.554 is a manually tuned constant
						motorDistances[3] - motorDistances[2]) * 0.25 * 0.554;
		double currentHeading = -Math.toRadians(currentLocation.getHeading() + 90);
		
		// this code is an implementation of the Pose Exponential algorithm
		Matrix vector = new Matrix(new double[][] {
				{xMovement,
				yMovement,
				rotation}
		});
		Matrix PoseExponential = new Matrix(3, 3);
		if (rotation != 0) {
			PoseExponential = new Matrix(new double[][]{
					{	Math.sin(rotation) / rotation,			(Math.cos(rotation) - 1) / rotation,	0},
					{	(1 - Math.cos(rotation)) / rotation,	Math.sin(rotation) / rotation,			0},
					{	0,										0,										1}
			});
		}
		Matrix rotationMatrix = new Matrix(new double[][] {
				{ Math.cos(currentHeading), -Math.sin(currentHeading),  0 },
				{ Math.sin(currentHeading), Math.cos(currentHeading),   0 },
				{ 0,                        0,                          1}
		});
		vector.mul(PoseExponential.transpose());
		vector.mul(rotationMatrix.transpose());
		double[] movementVectors = vector.getData()[0];
		OldLocationClass deltaLocation = new OldLocationClass(movementVectors[0] * -1.5,
				movementVectors[1] * 2, currentRotation - currentLocation.getHeading());
		currentLocation.add(deltaLocation);
	}
	
	public OldLocationClass getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(OldLocationClass location) { currentLocation = location; }
}
