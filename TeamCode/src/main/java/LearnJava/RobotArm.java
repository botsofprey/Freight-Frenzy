package LearnJava;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

@Deprecated
public class RobotArm {
	public static final double TICKS_PER_DEGREE = 1120. / 360.;

	private DcMotorEx arm;
	private TouchSensor limit;

	public RobotArm(HardwareMap hardwareMap) {
		arm = hardwareMap.get(DcMotorEx.class, "Arm Extension");
		arm.setDirection(DcMotorSimple.Direction.REVERSE);
		arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		limit = hardwareMap.get(TouchSensor.class, "Limit Arm");
	}

	public void setPower(double power) {
		if(limit.isPressed() && power < 0) {
			arm.setPower(0);
		} else {
			arm.setPower(power);
		}
	}

 	public void setPosition(double degrees){
		arm.setTargetPosition((int)(degrees * TICKS_PER_DEGREE));
		arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		arm.setVelocity(140);
	}

	public void update() {
		//if arm moving down and limit pressed then stop!
		if(limit.isPressed() && arm.getPower() < 0) {
			arm.setPower(0);
		}
	}
}
