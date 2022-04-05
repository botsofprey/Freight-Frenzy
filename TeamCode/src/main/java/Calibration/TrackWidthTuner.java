package Calibration;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

import DriveEngine.NewLocalizer;

@TeleOp(name="Track Width Tuner", group="tuners")
public class TrackWidthTuner extends LinearOpMode {
	private static final String[] MOTOR_NAMES = {
			"flMotor",
			"blMotor",
			"brMotor",
			"frMotor"
	};
	private static final DcMotorSimple.Direction[] DIRECTIONS = {
			DcMotorSimple.Direction.FORWARD,
			DcMotorSimple.Direction.FORWARD,
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.REVERSE
	};


	DcMotorEx[] motors = new DcMotorEx[4];

	NewLocalizer localizer;
	BNO055IMU imu;

	double imuAngle = 0;
	double prevImuAngle = 0;
	double localizerAngle = 0;
	double prevLocalizerAngle = 0;
	double imuInit;

	@Override
	public void runOpMode() throws InterruptedException {
		localizer = new NewLocalizer(hardwareMap, "RobotConfig.json");
		localizer.update(System.nanoTime());

		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
		parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
		parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		parameters.loggingEnabled      = true;
		parameters.loggingTag          = "IMU";
		parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

		imu = hardwareMap.get(BNO055IMU.class, "imu");
		imu.initialize(parameters);

		imuInit = imu.getAngularOrientation(AxesReference.INTRINSIC,
				AxesOrder.XYZ, AngleUnit.DEGREES).firstAngle;
		prevImuAngle = imuInit;

		for (int i = 0; i < 4; i++) {
			motors[i] = hardwareMap.get(DcMotorEx.class, MOTOR_NAMES[i]);
			motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
			motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
			motors[i].setDirection(DIRECTIONS[i]);
		}

		telemetry.addData("Status", "Initialized");
		telemetry.update();
		waitForStart();

		double[] powers = {
				-1,
				-1,
				1,
				1
		};
		for (int i = 0; i < 4; i++) {
			motors[i].setPower(powers[i]);
		}
		long time = System.currentTimeMillis();
		while (opModeIsActive() && time + 10_000 > System.currentTimeMillis()) {
			localizer.update(System.nanoTime());
			double currentLocalizerAngle = localizer.getCurrentLocation().getHeading();
			if (prevLocalizerAngle > 0 && currentLocalizerAngle < 0) localizerAngle += 360;
			prevLocalizerAngle = currentLocalizerAngle;

			double currentImuAngle = imu.getAngularOrientation(AxesReference.INTRINSIC,
					AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
			if (prevImuAngle > 0 && currentImuAngle < 0) imuAngle += 360;
			prevImuAngle = currentImuAngle;


			telemetry.addData("IMU", imuAngle + prevImuAngle - imuInit);
			telemetry.addData("Localizer", localizerAngle + prevLocalizerAngle);
			telemetry.update();

			sleep(10);
		}
		for (int i = 0; i < 4; i++) {
			motors[i].setPower(0);
		}

		telemetry.addData("IMU", imuAngle + prevImuAngle - imuInit);
		telemetry.addData("Localizer", localizerAngle + prevLocalizerAngle);
		telemetry.update();

		while (opModeIsActive()) idle();
	}
}
