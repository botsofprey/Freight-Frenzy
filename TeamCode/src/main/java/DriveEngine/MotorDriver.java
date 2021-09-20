package DriveEngine;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public interface MotorDriver {
	DcMotor[] motors = new DcMotor[4];
	
	void moveRobot(double x, double y, double a, LinearOpMode mode);
}
