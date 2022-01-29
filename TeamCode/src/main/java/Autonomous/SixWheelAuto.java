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
			bucketArm.setLiftPower(-.3);
			sleep(1000);
			bucketArm.setLiftPower(0);
			bucketArm.resetLiftEncoder();
		}

		telemetry.addData("Lift Position", bucketArm.getLiftPos());
		telemetry.addData("Current angle", sixDrive.getAngle());
		telemetry.addData("Status", "Initialized");
		telemetry.update();

		waitForStart();

		sixDrive.move
	}
//	}

	private boolean compare(double a, double b, double range){
		return Math.abs(a - b) < range;
	}
}

