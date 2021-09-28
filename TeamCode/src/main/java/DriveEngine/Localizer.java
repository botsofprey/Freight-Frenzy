package DriveEngine;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.Encoder;
import UtilityClasses.JSONReader;
import UtilityClasses.Location;
import UtilityClasses.Vec2d;

import static DataFiles.DriveBaseConstants.*;

public class Localizer {
	private Encoder leftEncoder, rightEncoder, centerEncoder;
	private Location currentLocation;
	
	public Localizer(HardwareMap hardwareMap, String leftEncoderName, String rightEncoderName,
	                 String centerEncoderName, String configFile, Location startLocation) {
		leftEncoder = new Encoder(hardwareMap, leftEncoderName,
				configFile, LEFT_ENCODER_DIRECTION);
		rightEncoder = new Encoder(hardwareMap, rightEncoderName,
				configFile, RIGHT_ENCODER_DIRECTION);
		centerEncoder = new Encoder(hardwareMap, centerEncoderName,
				configFile, CENTER_ENCODER_DIRECTION);
		currentLocation = startLocation;
	}
	
	public Localizer(HardwareMap hardwareMap, String leftEncoderName, String rightEncoderName,
	                  String centerEncoderName, String configFile) {
		this(hardwareMap, leftEncoderName, rightEncoderName, centerEncoderName,
				configFile, new Location(0, 0, 0));
	}
	
	public Localizer(HardwareMap hardwareMap, String configFile) {
		this(hardwareMap, LEFT_ENCODER_NAME, RIGHT_ENCODER_NAME, CENTER_ENCODER_NAME, configFile);
	}
	
	public Localizer(HardwareMap hardwareMap, String configFile, Location startLocation) {
		this(hardwareMap, LEFT_ENCODER_NAME, RIGHT_ENCODER_NAME,
				CENTER_ENCODER_NAME, configFile, startLocation);
	}
	
	public void update(OpMode mode) {
		leftEncoder.update();
		rightEncoder.update();
		centerEncoder.update();
		calculateNewPosition();
		mode.telemetry.addData("Left", Location.round(leftEncoder.getInchesFromStart()));
		mode.telemetry.addData("Right", Location.round(rightEncoder.getInchesFromStart()));
		mode.telemetry.addData("Center", Location.round(centerEncoder.getInchesFromStart()));
		mode.telemetry.addData("Location", currentLocation.toString());
	}
	
	private void calculateNewPosition() {
		double angleDiff = (rightEncoder.getInchesDiff() - leftEncoder.getInchesDiff()) /
				ENCODER_LATERAL_DISTANCE;
		double forwardDiff = (rightEncoder.getInchesDiff() + leftEncoder.getInchesDiff()) / 2;
		double rightDiff = centerEncoder.getInchesDiff() + (ENCODER_FORWARD_OFFSET * angleDiff);
		Vec2d movementTangent = new Vec2d(rightDiff, forwardDiff);
		movementTangent.convertToAngleMagnitude();
		double angle = currentLocation.getHeading();
		angle += movementTangent.angle;
		currentLocation.addHeading(Math.toDegrees(angleDiff));
		angleDiff /= 2.0;
		angle += Math.toDegrees(angleDiff);
		double magnitude;
		if (angleDiff != 0) {
			magnitude = movementTangent.magnitude * Math.sin(angleDiff) / angleDiff;
		}
		else {
			magnitude = movementTangent.magnitude;
		}
		Vec2d movement = new Vec2d(angle, magnitude);
		movement.convertToXY();
		currentLocation.addX(movement.x);
		currentLocation.addY(movement.y);
	}
	
	public Location getCurrentLocation() { return currentLocation; }
}
