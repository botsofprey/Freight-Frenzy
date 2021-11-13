package Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

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


	private DcMotor intakeMotor;

	public Intake(HardwareMap hw) {
		intakeMotor = hw.get(DcMotor.class, "intakeMotor");
		state = 0;
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
		intakeMotor.setPower(state * MOTOR_POWER);
	}
}
