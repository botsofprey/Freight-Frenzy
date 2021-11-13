package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import Subsystems.Carousel;
import Subsystems.Lift;


@Autonomous(name="Motor Test", group="LearnJava")
public class HelloWorld extends LinearOpMode {
    TankDrive tankDrive;
    Lift lift;
    Carousel carousel;

    @Override
    public void runOpMode() throws InterruptedException {
        tankDrive = new TankDrive(hardwareMap, this);
        lift = new Lift(hardwareMap, this);
        carousel = new Carousel(hardwareMap);



        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        lift.dropFreight();

        sleep(10000);

//        //Turn towards duck
//        driveBase.turnRight(75, 8);
//
//        //Push duck into box thing
//        driveBase.move(40, 12);
//
//        //Backward to turn toward Carosol
//        driveBase.move(-10, 7.5);
//
//        //Turn towards carosol
//        driveBase.turnRight(60, 12);
//
//        //Move towards wall to re angle
//        driveBase.move(22, 12);
//
//        //re angle against the wall
//        driveBase.turnRight(145, 8);
//
//        //angle towards carosol using wall
//        telemetry.addData("Distance from wall (inches)", distanceSensor.getDistance(DistanceUnit.INCH));
//        telemetry.update();
//
//        //move to carosol
//        driveBase.move(-6, 6);
//
/*
        //Spin carosol
        spinMotor.setPower(1);
        sleep(5000);
        spinMotor.setPower(0);

        //Back up to go to warehouse
        driveBase.move(-6, 12);

        //Straighten up
        driveBase.turnLeft(72, 12);

        //Back up into warehouse
        driveBase.move(-125, 300);
        */
    }
}
