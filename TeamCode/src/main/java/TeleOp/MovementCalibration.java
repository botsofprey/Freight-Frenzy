package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import DriveEngine.MecanumDrive;
import UtilityClasses.Location;

@TeleOp(name="MovementCalibration", group="TeleOp")
public class MovementCalibration extends LinearOpMode {
	@Override
	public void runOpMode() throws InterruptedException {
		MecanumDrive drive = new MecanumDrive(hardwareMap, "RobotConfig.json",
				new Location(0, 0, 0), false, this, true);
		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();
		drive.update();
		long previousTime = System.nanoTime();
		double previousSpeed = drive.getCurrentVelocity().getY();
		double maxAccel = 0;

		while(opModeIsActive()) {
			drive.update();
			drive.moveRobot(gamepad1.left_stick_x,
					-gamepad1.left_stick_y, -gamepad1.right_stick_x);
			long time = System.nanoTime();
			double speed = drive.getCurrentVelocity().getY();
			double accel = (speed - previousSpeed) / (time - previousTime) * 1_000_000_000L;
			maxAccel = Math.max(maxAccel, accel);
			telemetry.addData("Speed", speed);
			telemetry.addData("Accel", accel);
			telemetry.addData("Max Accel", maxAccel);
			telemetry.update();
			previousTime = time;
			previousSpeed = speed;

			while (System.nanoTime() - previousTime < 10_000_000)
				drive.moveRobot(gamepad1.left_stick_x,
						-gamepad1.left_stick_y, -gamepad1.right_stick_x);
		}
	}
}
