package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name="Motor Test", group="LearnJava")
public class HelloWorld extends LinearOpMode {
    TankDrive driveBase;
    DcMotor spinMotor;

    @Override
    public void runOpMode() throws InterruptedException {
        driveBase = new TankDrive(hardwareMap, this);
        spinMotor = hardwareMap.get(DcMotor.class, "Carosol");

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();



        //Move forward
        driveBase.move(24, 12);

        //Turn towards duck
        driveBase.turnRight(75, 8);

        //Push duck into box thing
        driveBase.move(40, 12);


        driveBase.move(10, 3);

        driveBase.turnRight(105, 12);

        driveBase.move(18, 12);

        spinMotor.setPower(1);
        sleep(5000);

    }
}
