package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import DriveEngine.SixDrive;
import Subsystems.BucketArm;
import UtilityClasses.HardwareWrappers.CRServoController;


@Autonomous (name="Better Robot", group="Autonomous")
public class SixWheelAuto extends LinearOpMode {

	private BucketArm bucketArm;

	private SixDrive sixDrive;

	private DistanceSensor rightSensor, leftSensor, frontSensor;

	private CRServoController servoLeft, servoRight;

	@Override
	public void runOpMode() throws InterruptedException {
		bucketArm = new BucketArm(hardwareMap);
		sixDrive = new SixDrive(hardwareMap);

		rightSensor = hardwareMap.get(DistanceSensor.class, "rightSensor");
		leftSensor = hardwareMap.get(DistanceSensor.class, "leftSensor");
		//frontSensor = hardwareMap.get(DistanceSensor.class, "frontSensor");

		servoLeft = new CRServoController(hardwareMap, "leftWheel");
		servoRight = new CRServoController(hardwareMap, "rightWheel");

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

		telemetry.addData("Lift Position", bucketArm.getLiftPos());
		telemetry.addData("Current angle", sixDrive.getAngle());
		telemetry.addData("Status", "Initialized");
		telemetry.update();

		waitForStart();

		sixDrive.move(60, .5);
		while (sixDrive.isBusy() && opModeIsActive()){
			sixDrive.update();
		}

		double distanceFromHub = leftSensor.getDistance(DistanceUnit.INCH);

		sixDrive.rotateRight(270, .5);
		while(sixDrive.rotating()){
			sixDrive.update();
		}
		sleep(500);

		bucketArm.liftMoveTowards(BucketArm.TOP/BucketArm.TICKS_PER_INCH, .5);
		while (bucketArm.liftIsBusy()){}
		sleep(500);

		bucketArm.setBucketPower(BucketArm.OUTTAKE);
		sleep(2500);
		bucketArm.setBucketPower(0);

		sixDrive.move(-distanceFromHub, .5);
		while (sixDrive.isBusy()){
			sixDrive.update();}
		sleep(500);

		bucketArm.liftMoveTowards(0, .5);
		while (bucketArm.liftIsBusy()){}
		sleep(500);

		sixDrive.rotateRight(90, 0.5);
		while(sixDrive.rotating()){
			sixDrive.update();}
		sleep(1000);

		double distanceFromWall = rightSensor.getDistance(DistanceUnit.INCH);

		sixDrive.rotateRight(90, 0.5);
		while(sixDrive.rotating()){
			sixDrive.update();}
		sleep(500);

		sixDrive.move(distanceFromWall - 2, .75);
		while(sixDrive.isBusy()){
			sixDrive.update();}
		sleep(500);

		double distanceFromCaro = rightSensor.getDistance(DistanceUnit.INCH);

		sixDrive.rotateLeft(90, 0.5);
		while (sixDrive.rotating()){
			sixDrive.update();}
		sleep(500);

		servoLeft.setPower(1);

		sixDrive.move(distanceFromCaro - 3, .85);
		while(sixDrive.isBusy()){
			sixDrive.update();}

		sleep(10000);
	}
//	}

	private boolean compare(double a, double b, double range){
		return Math.abs(a - b) < range;
	}

	private char[] startLocation(){
		char[] location = new char[2];
		double left = leftSensor.getDistance(DistanceUnit.INCH),
				right = rightSensor.getDistance(DistanceUnit.INCH);


		return location;
	}
}

