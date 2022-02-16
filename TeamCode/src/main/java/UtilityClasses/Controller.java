package UtilityClasses;

import com.qualcomm.robotcore.hardware.Gamepad;

public class Controller {
	private static final double TRIGGER_THRESHOLD = 0.1;
	
	private Gamepad gamepad;
	
	public boolean
			aHeld = false, bHeld = false, xHeld = false, yHeld = false,
			leftBumperHeld = false, rightBumperHeld = false,
			leftTriggerHeld = false, rightTriggerHeld = false,
			upHeld = false, downHeld = false, leftHeld = false, rightHeld = false,
			backHeld = false, startHeld = false,
	
			aPressed = false, bPressed = false, xPressed = false, yPressed = false,
			leftBumperPressed = false, rightBumperPressed = false,
			leftTriggerPressed = false, rightTriggerPressed = false,
			upPressed = false, downPressed = false, leftPressed = false, rightPressed = false,
			backPressed = false, startPressed = false,
	
			aReleased = false, bReleased = false, xReleased = false, yReleased = false,
			leftBumperReleased = false, rightBumperReleased = false,
			leftTriggerReleased = false, rightTriggerReleased = false,
			upReleased = false, downReleased = false, leftReleased = false, rightReleased = false,
			backReleased = false, startReleased = false;
	
	public double leftTrigger = 0, rightTrigger = 0;
	
	public Vec2d leftStick = new Vec2d(0, 0), rightStick = new Vec2d(0, 0);
	
	public Controller(Gamepad g) {
		gamepad = g;
	}
	
	public void update() {
		boolean previous = aHeld;
		boolean held = gamepad.a;
		aHeld = held;
		aPressed = held && !previous;
		aReleased = !held && previous;
		
		previous = bHeld;
		held = gamepad.b;
		bHeld = held;
		bPressed = held && !previous;
		bReleased = !held && previous;
		
		previous = xHeld;
		held = gamepad.x;
		xHeld = held;
		xPressed = held && !previous;
		xReleased = !held && previous;
		
		previous = yHeld;
		held = gamepad.y;
		yHeld = held;
		yPressed = held && !previous;
		yReleased = !held && previous;
		
		previous = leftBumperHeld;
		held = gamepad.left_bumper;
		leftBumperHeld = held;
		leftBumperPressed = held && !previous;
		leftBumperReleased = !held && previous;
		
		previous = rightBumperHeld;
		held = gamepad.right_bumper;
		rightBumperHeld = held;
		rightBumperPressed = held && !previous;
		rightBumperReleased = !held && previous;
		
		previous = leftTriggerHeld;
		leftTrigger = gamepad.left_trigger;
		held = leftTrigger > TRIGGER_THRESHOLD;
		leftTriggerHeld = held;
		leftTriggerPressed = held && !previous;
		leftTriggerReleased = !held && previous;
		
		previous = rightTriggerHeld;
		rightTrigger = gamepad.right_trigger;
		held = rightTrigger > TRIGGER_THRESHOLD;
		rightTriggerHeld = held;
		rightTriggerPressed = held && !previous;
		rightTriggerReleased = !held && previous;
		
		previous = upHeld;
		held = gamepad.dpad_up;
		upHeld = held;
		upPressed = held && !previous;
		upReleased = !held && previous;
		
		previous = downHeld;
		held = gamepad.dpad_down;
		downHeld = held;
		downPressed = held && !previous;
		downReleased = !held && previous;
		
		previous = leftHeld;
		held = gamepad.dpad_left;
		leftHeld = held;
		leftPressed = held && !previous;
		leftReleased = !held && previous;
		
		previous = rightHeld;
		held = gamepad.dpad_right;
		rightHeld = held;
		rightPressed = held && !previous;
		rightReleased = !held && previous;
		
		previous = backHeld;
		held = gamepad.back;
		backHeld = held;
		backPressed = held && !previous;
		backReleased = !held && previous;
		
		previous = startHeld;
		held = gamepad.start;
		startHeld = held;
		startPressed = held && !previous;
		startReleased = !held && previous;
		
		leftStick.x = gamepad.left_stick_x;
		leftStick.y = -gamepad.left_stick_y;
		leftStick.convertToAngleMagnitude();
		rightStick.x = gamepad.right_stick_x;
		rightStick.y = -gamepad.right_stick_y;
		rightStick.convertToAngleMagnitude();
	}

	public void rumble(double left, double right, int duration) {
		gamepad.rumble(left, right, duration);
	}

	public void rumble(double left, double right) {
		gamepad.rumble(left, right, Gamepad.RUMBLE_DURATION_CONTINUOUS);
	}

	public void rumble(int duration) {
		gamepad.rumble(duration);
	}

	public void rumble() {
		gamepad.rumble(Gamepad.RUMBLE_DURATION_CONTINUOUS);
	}

	public void stopRumble() {
		gamepad.stopRumble();
	}

	public void rumble(Gamepad.RumbleEffect rumbleEffect) {
		gamepad.runRumbleEffect(rumbleEffect);
	}

	public void rumbleBlips(int blips) {
		gamepad.rumbleBlips(blips);
	}

	public boolean isRumbling() {
		return gamepad.isRumbling();
	}
}
