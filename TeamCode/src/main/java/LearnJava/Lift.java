package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Lift {
	private static final double TICKS_PER_INCH = 537.7 / (0.91 * Math.PI);


	private Servo bucketWall;


	private DcMotorEx slide;
	private LinearOpMode mode;

	public Lift(HardwareMap hardwareMap, LinearOpMode opMode) {
		slide = hardwareMap.get(DcMotorEx.class, "Slider");
		slide.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
		slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
		slide.setDirection(DcMotorSimple.Direction.REVERSE);

		bucketWall = hardwareMap.get(Servo.class, "bucket");

		mode = opMode;
	}

	public void move(double height, double inchesPerSecond) {
		height -= 7 ;

		slide.setTargetPosition((int)(height * TICKS_PER_INCH));
		slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		slide.setVelocity(inchesPerSecond * TICKS_PER_INCH);

		while(mode.opModeIsActive() && slide.isBusy());
	}

	public double getCurrentHeight(){
		return 7 + slide.getCurrentPosition() / TICKS_PER_INCH;
	}

	public void dropFreight() {
		bucketWall.setPosition(1);
		mode.sleep(1000);
		bucketWall.setPosition(0);
	}
}
