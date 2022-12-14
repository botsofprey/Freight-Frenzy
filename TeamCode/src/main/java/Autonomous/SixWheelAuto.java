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

	private DistanceSensor rightSensor, leftSensor;

	private CRServoController servoLeft, servoRight;

	@Override
	public void runOpMode() throws InterruptedException {
		bucketArm = new BucketArm(hardwareMap);
		sixDrive = new SixDrive(hardwareMap);

		rightSensor = hardwareMap.get(DistanceSensor.class, "rightSensor");
		leftSensor = hardwareMap.get(DistanceSensor.class, "leftSensor");

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

		sixDrive.setMotorPower(0.5, 0.5);

		while(opModeIsActive()){
			telemetry.addData("Left Power", sixDrive.getLeftPower());
			telemetry.addData("Right Power", sixDrive.getRightPower());
			telemetry.update();
		}

		sixDrive.move(30, 1);
		while (sixDrive.isBusy()){}

		double distanceFromHub = leftSensor.getDistance(DistanceUnit.INCH);

		sixDrive.rotateRight(.5, 90);
		while(sixDrive.rotating()){}
		sleep(500);

		sixDrive.move(distanceFromHub - 3, .25);
		while (sixDrive.isBusy()){}
		sleep(500);

		bucketArm.liftMoveTowards(BucketArm.TOP/BucketArm.TICKS_PER_INCH, .5);
		while (bucketArm.liftIsBusy()){}
		sleep(500);

		bucketArm.setBucketPower(BucketArm.OUTTAKE);
		sleep(2500);
		bucketArm.setBucketPower(0);

		bucketArm.liftMoveTowards(0, .5);
		while (bucketArm.liftIsBusy()){}
		sleep(500);

		sixDrive.rotateRight(90, 0.5);
		while(sixDrive.rotating()){}
		sleep(1000);

		double distanceFromWall = rightSensor.getDistance(DistanceUnit.INCH);

		sixDrive.rotateRight(90, 0.5);
		while(sixDrive.rotating()){}
		sleep(500);

		sixDrive.move(distanceFromWall - 2, .75);
		while(sixDrive.isBusy()){}
		sleep(500);

		double distanceFromCaro = rightSensor.getDistance(DistanceUnit.INCH);

		sixDrive.rotateLeft(90, 0.5);
		while (sixDrive.rotating()){}
		sleep(500);

		servoLeft.setPower(1);

		sixDrive.move(distanceFromCaro - 3, .85);
		while(sixDrive.isBusy()){}

		sleep(10000);
	}
//	}

	private boolean compare(double a, double b, double range){
		return Math.abs(a - b) < range;
	}
}

