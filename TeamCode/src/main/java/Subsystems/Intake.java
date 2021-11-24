package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.HardwareWrappers.MotorController;

public class Intake {
	public static final int INTAKE_BUTTON = 0;
	public static final int OUTTAKE_BUTTON = 1;

	private static final double MOTOR_POWER = 1;
	private static final int BRAKE = 0;
	private static final int INTAKE = 1;
	private static final int OUTTAKE = 2;
	private static final int[][] STATE_TABLE = {
			{ INTAKE, OUTTAKE },
			{ BRAKE, OUTTAKE },
			{ INTAKE, BRAKE }
	};
	private int state;


	private MotorController intakeMotor;

	private LinearOpMode mode;

	public Intake(HardwareMap hw, LinearOpMode m, boolean errors) {
		mode = m;

		intakeMotor = new MotorController(hw, "intakeMotor", mode, errors);
		intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
		state = BRAKE;
	}

	public void intake() {
		intakeMotor.setPower(MOTOR_POWER);
		state = INTAKE;
	}

	public void outtake() {
		intakeMotor.setPower(-MOTOR_POWER);
		state = OUTTAKE;
	}

	public void brake() {
		intakeMotor.setPower(0);
		state = BRAKE;
	}

	public void switchState(int button) {
		state = STATE_TABLE[state][button];
		intakeMotor.setPower(getPowerFromState());
	}

	private double getPowerFromState() {
		return state == 2 ? -MOTOR_POWER : (state * MOTOR_POWER);
	}
}
