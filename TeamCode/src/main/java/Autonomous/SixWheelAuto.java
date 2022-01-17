package Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import Subsystems.BucketArm;


@Autonomous (name="Better Robot", group="Autonomous")
public class SixWheelAuto extends LinearOpMode {

	private BucketArm bucketArm;

	@Override
	public void runOpMode() throws InterruptedException {
		bucketArm = new BucketArm(hardwareMap);
	}
}
