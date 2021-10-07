package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;


@Autonomous(name="Motor Test", group="LearnJava")
public class HelloWorld extends LinearOpMode {
    TankDrive driveBase;
    DcMotor spinMotor;

    @Override
    public void runOpMode() throws InterruptedException {
        driveBase = new TankDrive(hardwareMap, this);
        spinMotor = hardwareMap.get(DcMotor.class, "Carousel");

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        //Move forward
        driveBase.move(24, 12);

        //Turn towards duck
        driveBase.turnRight(75, 8);

        //Push duck into box thing
        driveBase.move(40, 12);

        //Backward to turn toward Carosol
        driveBase.move(-10, 7.5);

        //Turn towards carosol
        driveBase.turnRight(50, 12);

        //Move to carosol but not all the way
        driveBase.move(24, 12);

        //re angle
        driveBase.turnRight(45, 8);

        //move to carosol
        driveBase.move(3, 6);

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
    }
}
