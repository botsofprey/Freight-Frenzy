package DriveEngine;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.Encoder;
import UtilityClasses.JSONReader;

import static DataFiles.DriveBaseConstants.*;

public class Localizer {
	private Encoder leftEncoder, rightEncoder, centerEncoder;
	
	public Localizer(HardwareMap hardwareMap, String leftEncoderName, String rightEncoderName,
	                 String centerEncoderName, String configFile) {
		leftEncoder = new Encoder(hardwareMap, leftEncoderName,
				configFile, LEFT_ENCODER_DIRECTION);
		rightEncoder = new Encoder(hardwareMap, rightEncoderName,
				configFile, RIGHT_ENCODER_DIRECTION);
		centerEncoder = new Encoder(hardwareMap, centerEncoderName,
				configFile, CENTER_ENCODER_DIRECTION);
	}
	
	public Localizer(HardwareMap hardwareMap, String configFile) {
		this(hardwareMap, LEFT_ENCODER_NAME, RIGHT_ENCODER_NAME, CENTER_ENCODER_NAME, configFile);
	}
	
	public void update(OpMode mode) {
		leftEncoder.update();
		rightEncoder.update();
		centerEncoder.update();
		mode.telemetry.addData("Left", leftEncoder.getInchesFromStart());
		mode.telemetry.addData("Right", rightEncoder.getInchesFromStart());
		mode.telemetry.addData("Center", centerEncoder.getInchesFromStart());
	}
}
