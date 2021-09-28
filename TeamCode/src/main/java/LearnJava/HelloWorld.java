package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;


@TeleOp(name="Motor Test", group="LearnJava")
public class HelloWorld extends LinearOpMode {
    
    private DcMotorEx leftMotor;//to use motor encoders, use the DcMotorEx class
    //it has all of the functions DcMotor has, but it can also use encoders
    
    TankDrive driveBase;

    @Override
    public void runOpMode() throws InterruptedException {
        leftMotor = hardwareMap.get(DcMotorEx.class, "left_motor");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        
        driveBase = new TankDrive(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        
        
        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);//this stops the motor and defines its current position as zero
        leftMotor.setTargetPosition(1000);//this sets the motor's target 1000 ticks forward
        leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);//the motor is told to go to the target location
        leftMotor.setVelocity(200);//sets the maximum number of ticks per second the motor can move
        
        while (leftMotor.isBusy()) {}//waits for the motor to finish moving before continuing
    }
}
