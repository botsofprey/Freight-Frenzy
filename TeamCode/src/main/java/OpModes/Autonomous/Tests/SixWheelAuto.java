package OpModes.Autonomous.Tests;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import DriveEngine.SixDrive;
import Subsystems.BucketArm;
import UtilityClasses.HardwareWrappers.CRServoController;
import UtilityClasses.BatterySaving;


@Autonomous (name="Six R-blued", group="Autonomous")
public class SixWheelAuto extends LinearOpMode {

	private BucketArm bucketArm;

	private SixDrive sixDrive;

	private DistanceSensor rightSensor, leftSensor, backSensor;
	private double backStartPos = 2, rightStartPos = 22;
	private double rightDistances[] = new double[8], backDistances[] = new double[8];

	private CRServoController servoLeft, servoRight;

	private RevBlinkinLedDriver led;



	@Override
	public void runOpMode() throws InterruptedException {
		bucketArm = new BucketArm(hardwareMap);
		sixDrive = new SixDrive(hardwareMap, this);

		rightSensor = hardwareMap.get(DistanceSensor.class, "rightSensor");
		leftSensor = hardwareMap.get(DistanceSensor.class, "leftSensor");
		backSensor = hardwareMap.get(DistanceSensor.class, "backSensor");

		servoLeft = new CRServoController(hardwareMap, "leftWheel");
		servoRight = new CRServoController(hardwareMap, "rightWheel");

		led = hardwareMap.get(RevBlinkinLedDriver.class, "Led Indicate");



		//Zero Lift Position
		{
			bucketArm.liftMoveTowards(2, 0.5);
			while (bucketArm.liftIsBusy()) {
			}

			bucketArm.setLiftPower(-.75);
			while (!bucketArm.limitSwitch()) {
				telemetry.addData("Magnet sensor pressed", bucketArm.limitSwitch());
				telemetry.addData("Lift Position", bucketArm.getLiftPos());
				telemetry.update();
			}
			bucketArm.setLiftPower(0);
			bucketArm.resetLiftEncoder();
		}

		int index = 0;

		//Find Start Position
		while(!isStarted()) {
			rightDistances[index] = rightSensor.getDistance(DistanceUnit.INCH);
			backDistances[index] = backSensor.getDistance(DistanceUnit.INCH);
			index++;
			if (index >= rightDistances.length) index = 0;

			telemetry.addData("Current Distance From Carocel", averageValue(rightDistances));
			telemetry.addData("At Start Distance On Right", compare(rightStartPos, averageValue(rightDistances), 1));
			telemetry.addData("Current Distance From Wall", averageValue(backDistances));
			telemetry.addData("At Start Distance Behind", compare(backStartPos, averageValue(backDistances), 1));
			telemetry.update();

			if (inPosition()) {
				led.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
				telemetry.addData("Status", "Initialized");
			} else {
				led.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);
			}

			telemetry.update();
		}
			BatterySaving lowBattery = new BatterySaving(hardwareMap, led);

		while(!isStarted()) {
			telemetry.addData("Voltage", lowBattery.getBatteryVoltage());
			telemetry.update();
		}

		waitForStart();
		sixDrive.resetAngle();

		//Move to Hub
		sixDrive.move(36, .5);
		while (sixDrive.isBusy()){
			sixDrive.update();
		}

		double distanceFromHub = leftSensor.getDistance(DistanceUnit.INCH);

		sixDrive.rotatePID(-90);
		while(sixDrive.rotating()){
			sixDrive.update();
			telemetry.addData("Power", sixDrive.getLeftPower());
			telemetry.addData("Angle", sixDrive.getAngle());
			telemetry.update();
		}
		sleep(500);


		//Drop freight
		bucketArm.liftMoveTowards(BucketArm.TOP / BucketArm.TICKS_PER_INCH, .5);
		while (bucketArm.liftIsBusy()){}
		sleep(500);

		sixDrive.move(8, .25);
		while(sixDrive.isBusy()){
			sixDrive.update();
		}

		bucketArm.setBucketPower(BucketArm.OUTTAKE);
		sleep(2500);
		bucketArm.setBucketPower(0);

		//Move to wall
		bucketArm.liftMoveTowards(0, .5);
		sixDrive.move(-32, .1);
		while (sixDrive.isBusy()){
			sixDrive.update();}
		sleep(500);

		//Move to carosel
		sixDrive.rotatePID(0);
		while (sixDrive.rotating()){
			sixDrive.update();}
		sleep(500);

		double distanceFromCaro = backSensor.getDistance(DistanceUnit.INCH);

			//Turn carosel
		servoRight.setPower(1);
		servoLeft.setPower(-1);

		sixDrive.move(-30, .425);
		while(sixDrive.isBusy()){
			sixDrive.update();
		}
		sleep(10000);

		servoRight.setPower(0);
		servoLeft.setPower(0);

		//Park
		sixDrive.move(18, .5);
		while(sixDrive.isBusy()){
			sixDrive.update();
		}

		sixDrive.rotatePID(-90);
		while (sixDrive.rotating()){
			sixDrive.update();}
		sleep(500);

		while (opModeIsActive());
	}


	private boolean inPosition(){
		return compare(averageValue(rightDistances), rightStartPos, 1)
				&& compare(averageValue(backDistances), backStartPos, 1);
	}

	private double averageValue(double[] numbers){
		double total = 0;

		for(int i = 0; i < numbers.length; i++){
			total += numbers[i];
		}

		return total/(double)numbers.length;
	}

	private boolean compare(double a, double b, double range){
		return Math.abs(a - b) < range;
	}
}

