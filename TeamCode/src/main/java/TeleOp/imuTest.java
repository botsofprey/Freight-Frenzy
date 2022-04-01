package TeleOp;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp(name="imu Test", group="test")
@Disabled
public class imuTest extends LinearOpMode {
	@Override
	public void runOpMode() throws InterruptedException {
		BNO055IMU imu;

		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

		parameters.mode                = BNO055IMU.SensorMode.IMU;
		parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
		parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		parameters.loggingEnabled      = false;

		// Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
		// on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
		// and named "imu".
		imu = hardwareMap.get(BNO055IMU.class, "imu");

		imu.initialize(parameters);

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		while (opModeIsActive()) {
			Orientation orientation = imu.getAngularOrientation(AxesReference.INTRINSIC,
					AxesOrder.XYZ, AngleUnit.DEGREES);
			telemetry.addData("X", orientation.firstAngle);
			telemetry.addData("Y", orientation.secondAngle);
			telemetry.addData("Z", orientation.thirdAngle);
			telemetry.update();
		}
	}
}
