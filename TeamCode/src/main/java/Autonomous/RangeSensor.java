package Autonomous;

import android.graphics.Color;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.concurrent.TimeUnit;

@TeleOp(name = "Range Linear", group = "MRI")
public class RangeSensor extends LinearOpMode {

	private ElapsedTime runtime = new ElapsedTime();

	byte[] range1Cache; //The read will return an array of bytes. They are stored in this variable

	I2cAddr RANGE1ADDRESS = new I2cAddr(0x28); //Default I2C address for MR Range  (7-bit)
	public static final int RANGE1_REG_START = 0x04; //Register to start reading
	public static final int RANGE1_READ_LENGTH = 2; //Number of byte to read

	public I2cDevice RANGE1;
	public I2cDeviceSynch RANGE1Reader;
	public ModernRoboticsI2cRangeSensor sense;

	private DistanceSensor distanceSensor;

	private ColorSensor colorSensor;
	private static final int[] block = new int[] {204, 126, 8},
			ball = new int[] {255, 255, 255}, duck = new int[] {224, 183, 31};
	private static  final int range = 25;

	private DcMotor motor;

	@Override
	public void runOpMode() throws InterruptedException {
		motor = hardwareMap.get(DcMotor.class, "motor");
		motor.setPower(.1);

		telemetry.addData("Status", "Initialized");
		telemetry.update();

		//distanceSensor = hardwareMap.get(DistanceSensor.class, "sensor");

		colorSensor = hardwareMap.get(ColorRangeSensor.class, "sensor");

		//sense = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "sensor");
		//RANGE1 = hardwareMap.get(I2cDevice.class, "sensor");
		//RANGE1Reader = new I2cDeviceSynchImpl(RANGE1, RANGE1ADDRESS, false);
		//RANGE1Reader.engage();

		waitForStart();
		runtime.reset();
		long prev = runtime.now(TimeUnit.MICROSECONDS);
		int power = 1;

		while (opModeIsActive()) {
			long temp = runtime.now(TimeUnit.MICROSECONDS);
			if (temp >= prev + 4167) {
				prev = temp;
				power *= -1;
				motor.setPower(power);
			}
		}
	}

	public double[] RGBToHSV(int r, int g, int b) {
		if (r == g && r == b) {
			return new double[] { 0, 0, 1 };
		}
		double scale = Math.max(r, Math.max(g, b));
		double[] color = new double[] { r / scale, g / scale, b / scale };
		double h = 0;
		double max = 1.0;
		double min = Math.min(color[0], Math.min(color[1], color[2]));
		double diff = max - min;
		if (scale == r) {
			h = (60 * (color[1] - color[2]) / diff + 360) % 360;
		}
		if (scale == g) {
			h = (60 * (color[2] - color[0]) / diff + 120) % 360;
		}
		if (scale == b) {
			h = (60 * (color[0] - color[1]) / diff + 240) % 360;
		}
		return new double[] { h, diff, max };
	}
/*
	private boolean detectColor() {
		red = colorSensor.red();
		green = colorSensor.green();
		blue = colorSensor.blue();

		int[] color = new int[] {red,green,blue};

		boolean blockBo = colorChecker(block, color);
		boolean ballBo = colorChecker(ball, color);
		boolean duckBo = colorChecker(duck, color);

		return ballBo || blockBo || duckBo;
	}

	private boolean colorChecker(int[] colorA, int[] colorB) {
		return Math.abs(colorA[0] - colorB[0]) <= range && Math.abs(colorA[1] - colorB[1]) <= range
				&& Math.abs(colorA[2] - colorB[2]) <= range;
	}*/
}
