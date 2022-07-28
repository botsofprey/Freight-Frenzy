package DriveEngine;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import UtilityClasses.Deprecated.OldLocationClass;
import UtilityClasses.PIDController;

public class SixDrive {
	private LinearOpMode opmode;

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
			DcMotorSimple.Direction.FORWARD,
			DcMotorSimple.Direction.FORWARD
	};

	private double[] startPos = new double[4], endPos = new double[4];

	private BNO055IMU imu;
	private Orientation lastAngles = new Orientation();
	private double globalAngle = 0;
	public String RIGHT = "right", LEFT = "left";

	public double TICKS_PER_INCH = 537.7 / (4 * Math.PI), movementPower;
	public double targetAngle = 0;

	PIDController headingPid, driveHeadingPid, b_driveHeadingPid;

	public SixDrive(HardwareMap hardwareMap, LinearOpMode opmode){
		this.opmode = opmode;

		for (int i = 0; i < 4; i++) {
			motors[i] = hardwareMap.get(DcMotor.class, names[i]);
			motors[i].setDirection(directions[i]);
			motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
			motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		}
		BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

		parameters.mode = BNO055IMU.SensorMode.IMU;
		parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
		parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
		parameters.loggingEnabled = false;

		imu = hardwareMap.get(BNO055IMU.class, "imu");

		imu.initialize(parameters);

		headingPid = new PIDController(.00125,.00125,.00075);
		driveHeadingPid = new PIDController(.0285,0,0);
		b_driveHeadingPid = new PIDController(.05,.0001,0);
	}

	boolean backwards;
	public void move(double inches, double power){
		for(int i = 0; i < motors.length; i++){
			motors[i].setTargetPosition(motors[i].getCurrentPosition() +  (int)(inches * TICKS_PER_INCH));
			motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
		}

		opmode.sleep(500);

		for(int i =0; i < motors.length; i++){
			motors[i].setPower(power);
		}

		movementPower = power;

		backwards = inches < 0;
	}

	public void setMotorPower(double leftPower, double rightPower){
		motors[0].setPower(leftPower);
		motors[1].setPower(leftPower);
		motors[2].setPower(rightPower);
		motors[3].setPower(rightPower);
	}

	boolean rotating;
	double rotationRange;
	public void rotatePID(double angle){
		for(int i = 0; i < motors.length; i++){
			motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		}
		headingPid.reset();
		headingPid.setSP(angle);
		targetAngle = angle;

		opmode.sleep(500);

		rotating = true;
	}
	public void rotate(double angle, double motorPower, double range){
		targetAngle = angle + getAngle();
		if (targetAngle < -180)
			targetAngle += 360;
		else if (targetAngle > 180)
			targetAngle -= 360;

		for(int i = 0; i < motors.length; i++){
			motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
		targetAngle = OldLocationClass.normalizeHeading(targetAngle);
		movementPower = motorPower;
		rotating = true;
	}
	public void rotateLeft(double angle, double motorPower){
		for(int i = 0; i < motors.length; i++){
			motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		}

		motors[2].setPower(motorPower);
		motors[3].setPower(motorPower);

		targetAngle = getAngle() - (angle-4);
		targetAngle = OldLocationClass.normalizeHeading(targetAngle);
		movementPower = motorPower;
		rotating = true;
	}

	public boolean rotating(){
		return rotating;
	}

	public void stopMotors(){
		for(int i = 0; i < motors.length; i++){
			motors[i].setPower(0);
		}

		opmode.sleep(500);
	}

	public boolean isBusy(){
		if(motors[0].isBusy() || motors[1].isBusy() || motors[2].isBusy() || motors[3].isBusy()) {
			return true;
		}else{
			return false;
		}
	}

	public void resetAngle()
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
//
//		if (globalAngle < -180)
//			globalAngle += 360;
//		else if (globalAngle > 180)
//			globalAngle -= 360;

		return -lastAngles.firstAngle;
	}

	public double getLeftPower(){
		return motors[0].getPower();
	}
	public double getRightPower(){
		return motors[2].getPower();
	}

	public double getDistanceTraveled(){
		double startTotal = 0, endTotal = 0;

		for(int i = 0; i < motors.length; i++){
			endPos[i] = motors[i].getCurrentPosition();
		}

		for(int i = 0; i < 4; i++){
			startTotal += startPos[i];
			endTotal += endPos[i];
		}
		startTotal /=4; endTotal /=4;

		return endTotal - startTotal;
	}
	public void setStartPos(){
		startPos[0] = (motors[0].getCurrentPosition());
		startPos[1] = (motors[1].getCurrentPosition());
		startPos[2] = (motors[2].getCurrentPosition());
		startPos[3] = (motors[3].getCurrentPosition());
	}

	public void update(){
		if(rotating){
		//	double power = Math.min((movementPower/45)*(Math.abs(targetAngle - getAngle())),
		//			movementPower);
			double power = headingPid.calculateResponse(getAngle());
			System.out.println("Current Motor power: " + power + " Current angle: " + getAngle());

			power = Range.clip(power, -1, 1);

			motors[0].setPower(power);
			motors[1].setPower(power);
			motors[2].setPower(-power);
			motors[3].setPower(-power);

			if(compareCurrentAngles()){
				rotating = false;
				this.stopMotors();
			}
		} else if(isBusy()){
			double difference = 0;
			if(!backwards){
				difference = driveHeadingPid.calculateResponse(-getAngle());
			}else{
				difference = b_driveHeadingPid.calculateResponse(getAngle());
			}
			System.out.println("Adjustment: " + difference + " Angle: " + getAngle());

//			double angleError = getAngle() - targetAngle;
//			double newPower = movementPower * (angleError / 360);
			double leftPower = movementPower - difference,
					rightPower = movementPower + difference;

			setMotorPower(leftPower, rightPower);
		}
	}

	private boolean compareAngles(double a, double b, double range){
		if(a >b){
			return a - b <= range;
		}else{
			return b-a<=range;
		}
	}
	private boolean compareCurrentAngles(){
		return Math.abs(getAngle() - targetAngle) <= 2;
	}
}

