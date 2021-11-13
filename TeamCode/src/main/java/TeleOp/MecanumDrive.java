package TeleOp;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import UtilityClasses.Vec2d;

@TeleOp(name = "Mecanum Drive", group = "TeleOp")
//@Disabled
public class MecanumDrive extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "frontLeftDriveMotor");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "frontRightDriveMotor");
        DcMotor leftRear = hardwareMap.get(DcMotor.class, "backLeftDriveMotor");
        DcMotor rightRear = hardwareMap.get(DcMotor.class, "backRightDriveMotor");

        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRear.setDirection(DcMotorSimple.Direction.REVERSE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

//        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        BNO055IMU imu;

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hardwareMap.get(BNO055IMU.class, "");
        imu.initialize(parameters);


        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        double heading;
        double prevHeading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;

        while (opModeIsActive()) {
            double x = gamepad1.left_stick_x;
            double y = -gamepad1.left_stick_y;
            double a = -gamepad1.right_stick_x;
            //a *= 0.5;
/*
            heading = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
            Vec2d movementVector = new Vec2d(x, y);
            movementVector.convertToAngleMagnitude();
            movementVector.angle -= heading;
            movementVector.convertToXY();

 */
//            x = movementVector.x;
//            y = movementVector.y;
//            if (a == 0) {
//                double diff = heading - prevHeading;
//                diff = Math.abs(diff) < 0.1 ? 0 : diff;
//                diff /= 90;
//                a += diff;
//            }
//            else {
//                prevHeading = heading;
//            }

            double[] powers = {
                    -x + y - a,
                    -x - y - a,
                    -x + y + a,
                    -x - y + a
            };
            normalize(powers);

            leftFront.setPower(powers[0]);
            rightFront.setPower(powers[1]);
            rightRear.setPower(powers[2]);
            leftRear.setPower(powers[3]);
        }
    }

    private double[] normalize(double[] powers) {
        double scale = 1;
        for (double power : powers) {
            scale = Math.max(scale, Math.abs(power));
        }
        //scale *= 2;
        for (int i = 0; i < powers.length; i++) {
            powers[i] /= scale;
        }
        return powers;
    }
}
