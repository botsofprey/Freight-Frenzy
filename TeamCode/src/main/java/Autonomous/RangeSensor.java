package Autonomous;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Range", group = "test")
public class RangeSensor extends LinearOpMode {
	ModernRoboticsI2cRangeSensor rangeSensor;

	@Override public void runOpMode() {

		// get a reference to our compass
		rangeSensor = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "sensor_range");

		// wait for the start button to be pressed
		waitForStart();

		while (opModeIsActive()) {
			telemetry.addData("raw ultrasonic", rangeSensor.rawUltrasonic());
			telemetry.addData("raw optical", rangeSensor.rawOptical());
			telemetry.addData("cm optical", "%.2f cm", rangeSensor.cmOptical());
			telemetry.addData("cm", "%.2f cm", rangeSensor.getDistance(DistanceUnit.CM));
			telemetry.update();
		}
	}
}