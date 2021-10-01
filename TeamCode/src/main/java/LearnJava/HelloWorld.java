package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;


@TeleOp(name="Motor Test", group="LearnJava")
public class HelloWorld extends LinearOpMode {
    TankDrive driveBase;

    @Override
    public void runOpMode() throws InterruptedException {
        driveBase = new TankDrive(hardwareMap, this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        //Move to pos
        driveBase.leftMotor.setPower(0.4);
        driveBase.rightMotor.setPower(0.4);
        sleep(830);

        //Stop
        driveBase.leftMotor.setPower(0);
        driveBase.rightMotor.setPower(0);
        sleep(500);

        //Turn to duck
        driveBase.leftMotor.setPower(0.475);
        driveBase.rightMotor.setPower(-0.475);
        sleep(250);

        //Stop
        driveBase.leftMotor.setPower(0);
        driveBase.rightMotor.setPower(0);
        sleep(500);

        /* {
        driveBase.leftMotor.setPower(0.3);
        driveBase.rightMotor.setPower(-0.3);
        sleep(250);

        //Stop
        driveBase.leftMotor.setPower(0);
        driveBase.rightMotor.setPower(0);
        sleep(500); }*/

        //Put duck in box
        driveBase.leftMotor.setPower(0.35);
        driveBase.rightMotor.setPower(0.35);
        sleep(2500);

        driveBase.leftMotor.setPower(0);
        driveBase.rightMotor.setPower(0);
        sleep(500);

        //Turn
        driveBase.leftMotor.setPower(0.2);
        driveBase.rightMotor.setPower(-0.2);
        sleep(200);

        driveBase.leftMotor.setPower(0);
        driveBase.rightMotor.setPower(0);
        sleep(500);

        //Move backwards
        driveBase.leftMotor.setPower(-0.95);
        driveBase.rightMotor.setPower(-0.95);
        sleep(1500);

        /*
        driveBase.leftMotor.setPower(0.5);
        driveBase.rightMotor.setPower(-0.5);
        sleep(200);
                                        */
        driveBase.leftMotor.setPower(-1);
        driveBase.rightMotor.setPower(-1);
        sleep(1750);

        /*

        driveBase.move(24, 12);

        driveBase.leftMotor.setPower(0.5);
        sleep(500);

        driveBase.move(30, 10);

        driveBase.leftMotor.setPower(0.8);
        driveBase.rightMotor.setPower(0.8);

         */
    }
}
