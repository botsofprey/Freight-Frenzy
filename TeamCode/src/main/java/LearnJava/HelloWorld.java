package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@TeleOp(name="Motor Test", group="LearnJava")
public class HelloWorld extends LinearOpMode {
    private DcMotor leftMotor;
    private DcMotor rightMotor;
    private DistanceSensor distanceSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        leftMotor = hardwareMap.get(DcMotor.class, "left_motor");
        rightMotor = hardwareMap.get(DcMotor.class, "right_motor");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distance_sensor");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("distance", distanceSensor.getDistance(DistanceUnit.INCH));
            telemetry.update();
//            double motorPower = -gamepad1.left_stick_y / 2.0;
//            double turnPower = gamepad1.left_stick_x / 2.0;
//
//            leftMotor.setPower(motorPower + turnPower);
//            rightMotor.setPower(motorPower - turnPower);

        }
    }
}
