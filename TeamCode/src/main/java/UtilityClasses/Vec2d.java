package UtilityClasses;

public class Vec2d {
	public double x, y, angle, magnitude;
	
	public Vec2d(double f, double s) {
		x = f;
		y = s;
		angle = f;
		magnitude = s;
	}
	
	public void convertToXY() {
		y = Math.cos(Math.toRadians(angle)) * magnitude;
		x = -Math.sin(Math.toRadians(angle)) * magnitude;
	}
	
	public void convertToAngleMagnitude() {
		angle = Math.toDegrees(Math.atan2(-x, y));
		magnitude = Math.hypot(x, y);
	}
}
