package DriveEngine;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class SixDrive {

	private DcMotor[] motors = new DcMotor[4];
	private String[] names = new String[] {
			"frontLeft",
			"backLeft",
			"backRight",
			"frontRight"
	};
	private DcMotorSimple.Direction[] directions = new DcMotorSimple.Direction[] {
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.REVERSE,
			DcMotorSimple.Direction.FORWARD
	};


	private BNO055IMU imu;
	private Orientation lastAngles = new Orientation();
	private double globalAngle;
	public String RIGHT = "right", LEFT = "left";

	private double TICKS_PER_INCH = 537.6 / (3 * Math.PI), movementPower;
	public double targetAngle;

	public SixDrive(HardwareMap hardwareMap){
		for (int i = 0; i < 4; i++) {
			motors[i] = hardwareMap.get(DcMotor.class, names[i]);
			motors[i].setDirection(directions[i]);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
			motors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		}
		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

		parameters.mode = BNO055IMU.SensorMode.IMU;
		parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
		parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		parameters.loggingEnabled = false;

		imu = hardwareMap.get(BNO055IMU.class, "imu");

		imu.initialize(parameters);

		resetAngle();
	}

	public void move(double inches, double power){
		for(int i = 0; i < motors.length; i++){
			motors[i].setTargetPosition(motors[i].getCurrentPosition() +  (int)(inches * TICKS_PER_INCH));
			motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
			motors[i].setPower(power);
		}
	}

	boolean rotating;
	double rotationRange;
	public void rotate(double angle, double motorPower, double range){
		targetAngle = angle + getAngle();
		if (targetAngle < -180)
			targetAngle += 360;
		else if (targetAngle > 180)
			targetAngle -= 360;

		for(int i = 0; i < motors.length; i++){
			motors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		}

		String direction = (targetAngle > getAngle() ? RIGHT : LEFT);
		double power = motorPower/2;

		if(direction == RIGHT){
			motors[0].setPower(power);
			motors[1].setPower(power);
			motors[2].setPower(-power);
			motors[3].setPower(-power);
		}else{
			motors[0].setPower(-power);
			motors[1].setPower(-power);
			motors[2].setPower(power);
			motors[3].setPower(power);
		}

		rotating = true;
		movementPower = motorPower;
		rotationRange = range;
	}
	public void rotateRight(double angle, double motorPower){
		for(int i = 0; i < motors.length; i++){
			motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		}

		motors[0].setPower(motorPower);
		motors[1].setPower(motorPower);

		targetAngle = getAngle() + (angle-4);
		movementPower = motorPower;
		rotating = true;
	}
	public void rotateLeft(double angle, double motorPower){
		for(int i = 0; i < motors.length; i++){
			motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		}

		motors[2].setPower(motorPower);
		motors[3].setPower(motorPower);

		targetAngle =getAngle() - (angle-4);
		movementPower = motorPower;
		rotating = true;
	}

	public void setMotors(double power){
	motors[0].setPower(power);
	motors[1].setPower(power);
	}


	public boolean rotating(){
		return rotating;
	}

	public void stop(){
		for(int i = 0; i < motors.length; i++){
			motors[i].setPower(0);
		}
	}

	public boolean isBusy(){
		return  motors[0].isBusy();
	}

	private void resetAngle()
	{
		lastAngles = imu.getAngularOrientation
				(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

		globalAngle = 0;
	}

	public double getAngle()
	{
		// We experimentally determined the Z axis is the axis we want to use for heading angle.
		// We have to process the angle because the imu works in euler angles so the Z axis is
		// returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
		// 180 degrees. We detect this transition and track the total cumulative angle of rotation.

		Orientation angles = imu.getAngularOrientation
				(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

		double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

		if (deltaAngle < -180)
			deltaAngle += 360;
		else if (deltaAngle > 180)
			deltaAngle -= 360;

		globalAngle += deltaAngle;

		lastAngles = angles;

		if (globalAngle < -180)
			globalAngle += 360;
		else if (globalAngle > 180)
			globalAngle -= 360;

		return -globalAngle;
	}

	public double getMovementPower(){
		return motors[0].getPower();
	}

	public void update(){
		if(rotating){
//			double power = Math.min((movementPower/45)*(Math.abs(targetAngle - getAngle())),
//					movementPower);
//			setMotors(power);
//			System.out.println("power: " + power);
			if(compareAngles(targetAngle, getAngle(), 2)){
				rotating = false;
				stop();
			}
		}
	}

	private boolean compareAngles(double a, double b, double range){
		if(a >b){
			return a - b <= range;
		}else{
			return b-a<=range;
		}
	}
}

