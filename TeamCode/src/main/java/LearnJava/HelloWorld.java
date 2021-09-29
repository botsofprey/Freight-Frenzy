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

        driveBase.move(24, 12);

        driveBase.leftMotor.setPower(0.5);
        sleep(500);

        driveBase.move(30, 10);

        driveBase.leftMotor.setPower(0.8);
        driveBase.rightMotor.setPower(0.8);

        /*
        driveBase.leftMotor.setPower(0.5);
        driveBase.rightMotor.setPower(0.5);
        sleep(800);

        driveBase.leftMotor.setPower(0);
        driveBase.rightMotor.setPower(0);
        sleep(500);

        driveBase.leftMotor.setPower(0.5);
        driveBase.rightMotor.setPower(-0.5);
        sleep(300);

        driveBase.leftMotor.setPower(0);
        driveBase.rightMotor.setPower(0);
        sleep(500);

        driveBase.leftMotor.setPower(0.2);
        driveBase.rightMotor.setPower(0.2);
        sleep(5000);

        driveBase.leftMotor.setPower(0);
        driveBase.rightMotor.setPower(0);
        sleep(500);

        driveBase.leftMotor.setPower(-0.9);
        driveBase.rightMotor.setPower(-0.9);
        sleep(3500);
         */
    }
}
