package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import DriveEngine.BadMecanumAuto;


@Autonomous(name="Motor Test", group="LearnJava")
public class HelloWorld extends LinearOpMode {
    private BadMecanumAuto driveBase;
    private DcMotor spinMotor;
    private DistanceSensor distanceSensor;
    private Servo numberBlue;
    private CRServo letterGrape;

    @Override
    public void runOpMode() throws InterruptedException {
        driveBase = new BadMecanumAuto(hardwareMap, this);
        spinMotor = hardwareMap.get(DcMotor.class, "Carousel");

        numberBlue = hardwareMap.get(Servo.class, "colourSix");

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        //Move forward
        driveBase.move(24, 12);

        numberBlue.setPosition(0.5);

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

    public int[] Sort(int[] input) {
        int[] test = { 8636, 2432, 483, 7654, 1145 };
        //483, 1145, 2432, 7654, 8636
        int minimum = test[0];
        for (int i = 0; i < test.length; i++) {

        }
        return test;

    }
}
