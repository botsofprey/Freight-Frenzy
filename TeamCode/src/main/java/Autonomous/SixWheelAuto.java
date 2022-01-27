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
			sleep(1000);
			bucketArm.setLiftPower(0);
			bucketArm.resetLiftEncoder();
		}

		telemetry.addData("Lift Position", bucketArm.getLiftPos());
		telemetry.addData("Status", "Initialized");
		telemetry.update();

		waitForStart();

		double distanceFromWarehouse = rightSensor.getDistance(DistanceUnit.INCH);

		//Go to warehouse
		{
			//Rotate towards warehouse
			sixDrive.rotate(90, .01);
			while (opModeIsActive() && sixDrive.rotating()) {
				telemetry.addData("Target", sixDrive.targetAngle);
				telemetry.addData("Current Angle", sixDrive.getAngle());
				telemetry.update();
				sixDrive.update();
			}
			stop();
			sleep(1000);

			//Move to warehouse
			sixDrive.move(distanceFromWarehouse - 1, 1);
			while (sixDrive.isBusy() && opModeIsActive()) {
			}
			sleep(1000);
		}
//
//    //Drop freight in shipping hub
//    {
//       //Intake freight
//       bucketArm.setBucketPower(BucketArm.INTAKE);
//       sleep(1000);
//       bucketArm.setBucketPower(0);
//
//       //Move out to warehouse
//       sixDrive.move(-48, 1);
//       while (sixDrive.isBusy() && opModeIsActive()) {
//       }
//       sleep(1000);
//
//       //Rotate to shipping hub
//       sixDrive.rotate(sixDrive.LEFT, 1);
//       while (opModeIsActive() && compare(sixDrive.getAngle(), -90, 5)) {
//       }
//       stop();
//       sleep(1000);
//
//       //Move to shipping hub
//       sixDrive.move(36, 0.5);
//       while (sixDrive.isBusy() && opModeIsActive()) {
//       }
//       sleep(1000);
//
//       //Lift bucket
//       bucketArm.liftMoveTowards(BucketArm.TOP / BucketArm.TICKS_PER_INCH, 0.75);
//       while (bucketArm.liftIsBusy() && opModeIsActive()) {
//       }
//
//       //Drop freight
//       bucketArm.setBucketPower(BucketArm.OUTTAKE);
//       sleep(2000);
//       bucketArm.setBucketPower(0);
//    }
//
//    //Spin Carousel
//    {
//    //Back up to wall
//       sixDrive.move(-24, .75);
//       while (sixDrive.isBusy() && opModeIsActive()) {
//       }
//
//       //Find distance to wall
//       double distanceFromCarousel = leftSensor.getDistance(DistanceUnit.INCH);
//
//       //Rotate towards
//       sixDrive.rotate(sixDrive.RIGHT, 1);
//       while (opModeIsActive() && compare(sixDrive.getAngle(), 90, 5)) {
//       }
//       stop();
//       sleep(1000);
//
//       servoLeft.setPower(1);
//
//       sixDrive.move(distanceFromCarousel - 4, 1);
//       while (sixDrive.isBusy() && opModeIsActive()) {
//       }
//       sleep(5000);
//    }
//
//    //Park in warehouse
//    {
//       //Rotate towards warehouse
//       sixDrive.rotate(sixDrive.RIGHT, 1);
//       while (opModeIsActive() && compare(sixDrive.getAngle(), -180, 5)) {
//          if(compare(sixDrive.getAngle(), -90, 5)){
//             //Find distance to warehouse
//             distanceFromWarehouse = rightSensor.getDistance(DistanceUnit.INCH);
//          }
//       }
//
//       //Park in warehouse
//       sixDrive.move(distanceFromWarehouse, 1);
//       while (sixDrive.isBusy() && opModeIsActive()) {
//       }
	}
//	}

	private boolean compare(double a, double b, double range){
		return Math.abs(a - b) < range;
	}
}

