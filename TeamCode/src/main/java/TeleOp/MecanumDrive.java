package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

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

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            double x = gamepad1.left_stick_x;
            double y = -gamepad1.left_stick_y;
            double a = -gamepad1.right_stick_x;
            double[] powers = {
                    x + y + a,
                    x - y + a,
                    x + y - a,
                    x - y - a
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
        for (int i = 0; i < powers.length; i++) {
            powers[i] /= scale;
        }
        return powers;
    }
}
