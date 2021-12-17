package TeleOp;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Const;
import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import UtilityClasses.Controller;
import UtilityClasses.Location;
import UtilityClasses.Vec2d;

@TeleOp(name = "Mecanum Drive", group = "TeleOp")
//@Disabled
public class MecanumDrive extends LinearOpMode {
    @Override
    public void runOpMode() {
        DriveEngine.MecanumDrive drive = new DriveEngine.MecanumDrive(hardwareMap,
                "RobotConfig.json", new Location(0, 0, 0),
                true, this, true);
        Controller controller = new Controller(gamepad1);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        Func<int[]> func = drive::getMotorLocations;
        telemetry.addData("Location", func);
        waitForStart();

        while (opModeIsActive()) {
            controller.update();
            drive.update();

            telemetry.update();
            sleep(17);
        }
    }
}
