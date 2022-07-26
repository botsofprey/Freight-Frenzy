package DriveEngine;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.HardwareWrappers.OdometryWheel;
import UtilityClasses.JSONReader;
import UtilityClasses.Location;

public class NewLocalizer {
	private OdometryWheel leftWheel;
	private OdometryWheel rightWheel;
	private OdometryWheel perpendicularWheel;
	
	private double trackWidth;
	private double forwardOffset;
	
	private Location location;
	private Location velocity;
	private double angularVelocity;
	
	private long previousTime;
	
	public NewLocalizer(HardwareMap hw, String fileName, Location start) {
		JSONReader reader = new JSONReader(hw, fileName);
		String wheelFile = reader.getString("deadWheelFile");
		
		leftWheel = new OdometryWheel(hw,
				reader.getString("leftDeadWheelMotorName"), wheelFile);
		rightWheel = new OdometryWheel(hw,
				reader.getString("rightDeadWheelMotorName"), wheelFile);
		perpendicularWheel = new OdometryWheel(hw,
				reader.getString("perpendicularDeadWheelMotorName"), wheelFile);
		
		leftWheel.setDirection(reader.getString("leftDeadWheelDirection"));//todo make an op mode to tune encoder directions
		rightWheel.setDirection(reader.getString("rightDeadWheelDirection"));
		perpendicularWheel.setDirection(reader.getString("perpendicularDeadWheelDirection"));
		
		trackWidth = reader.getDouble("odometryTrackWidth");
		forwardOffset = reader.getDouble("odometryForwardOffset");
		
		location = start;
		previousTime = System.nanoTime();
	}
	public NewLocalizer(HardwareMap hw, String fileName) {
		this(hw, fileName, Location.ORIGIN);
	}
	
	//updates to position and velocity for a detailed description of the algorithm see the link
	//https://gm0.org/en/latest/docs/software/odometry.html
	public void update(long timeNanos) {
		double deltaLeft = leftWheel.getInchDiff();
		double deltaRight = rightWheel.getInchDiff();
		double deltaPerpendicular = perpendicularWheel.getInchDiff();
		
		//this formula gives angle counterclockwise instead of clockwise,
		// so it is the negative of what is described in the link
		double phi = (deltaRight - deltaLeft) / trackWidth;
		double deltaCenter = (deltaLeft + deltaRight) / 2.0;
		double deltaHorizontal = deltaPerpendicular - forwardOffset * phi;
		
		//calculate a few values to use in the matrices
		double theta = Math.toRadians(location.getHeading());
		double sinPhi = phi == 0 ? 1 : Math.sin(phi) / phi;//ternary operator handles limiting case
		double cosPhi = phi == 0 ? 0 : (1 - Math.cos(phi)) / phi;
		double sinTheta = Math.sin(theta);
		double cosTheta = Math.cos(theta);
		
		//first matrix expansion
		double a = sinPhi * deltaCenter - cosPhi * deltaHorizontal;
		double b = cosPhi * deltaCenter + sinPhi * deltaHorizontal;
		
		//second matrix expansion
		double deltaX = cosTheta * a - sinTheta * b;
		double deltaY = sinTheta * a + cosTheta * b;
		
		//convert radians for math to degrees for ease of use
		phi = Math.toDegrees(phi);
		
		//calculate time difference in seconds
		double timeDiff = (timeNanos - previousTime) / 1_000_000_000.0;
		
		//update velocity information
		velocity = new Location(deltaX * timeDiff, deltaY * timeDiff);
		angularVelocity = phi * timeDiff;
		
		//update position information
		location.addX(deltaX);
		location.addY(deltaY);
		location.addHeading(phi);
		
		//update the time
		previousTime = timeNanos;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	public Location getCurrentLocation() { return location; }
	public Location getVelocity() { return velocity; }
	public double getAngularVelocity() { return angularVelocity; }
	public void outputEncoders(LinearOpMode mode) {
		mode.telemetry.addData("LeftDeadWheel", leftWheel.getInch());
		mode.telemetry.addData("RightDeadWheel", rightWheel.getInch());
		mode.telemetry.addData("PerpendicularDeadWheel", perpendicularWheel.getInch());
	}
}
