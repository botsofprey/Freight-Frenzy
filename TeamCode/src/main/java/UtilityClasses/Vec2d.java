package UtilityClasses;

/**
 * This is a class for storing a coordinate without an associated heading.
 * It is also used to convert between polar and cartesian coordinates and vice versa.
 * Conversion between coordinate systems is handled lazily and automatically.
 * When using this class, assume any change to one coordinate system will
 * immediately and instantly update the other coordinate system to keep them both in sync.
 *
 * @author Alex Prichard
 */
public class Vec2d {
	public static final boolean CARTESIAN = true; // x, y coordinates
	public static final boolean POLAR = false; // angle, magnitude coordinates
	
	// These tell if a given coordinate system is currently valid.
	// If an invalid coordinate system is accessed,
	// Vec2d converts the other system into the desired system
	// before the operation or read is completed.
	// If a write operation is attempted to one coordinate system,
	// Vec2d invalidates the other coordinate system
	// because it no longer contains up to date information.
	// It basically automatically updates both coordinate systems
	// to stay in sync with each other by using lazy evaluation,
	// while allowing reads and writes to both coordinate systems.
	private boolean isCartesian, isPolar;
	private double x, y, angle, distance;
	
	public Vec2d(double x_a, double y_d, boolean system) {
		x = x_a;
		y = y_d;
		angle = x_a;
		distance = y_d;
		isCartesian = system;
		isPolar = !system;
	}
	public Vec2d(double x, double y) {
		this(x, y, CARTESIAN);
	}
	
	// Neither of these methods should ever be called manually because
	// the class will always call them automatically if they are needed.
	private void convertToCartesian() {
		x = Math.cos(Math.toRadians(angle)) * distance;
		y = Math.sin(Math.toRadians(angle)) * distance;
		isCartesian = true;
	}
	private void convertToPolar() {
		angle = Math.toDegrees(Math.atan2(y, x));
		distance = Math.hypot(x, y);
		isPolar = true;
	}
	
	public void setX(double x) {
		if (!isCartesian) convertToCartesian();
		isPolar = false;
		this.x = x;
	}
	public void setY(double y) {
		if (!isCartesian) convertToCartesian();
		isPolar = false;
		this.y = y;
	}
	public void setAngle(double angle) {
		if (!isPolar) convertToPolar();
		isCartesian = false;
		this.angle = angle;
	}
	public void setDistance(double magnitude) {
		if (!isPolar) convertToPolar();
		isCartesian = false;
		this.distance = magnitude;
	}
	
	public double getX() {
		if (!isCartesian) convertToCartesian();
		return x;
	}
	public double getY() {
		if (!isCartesian) convertToCartesian();
		return y;
	}
	public double getAngle() {
		if (!isPolar) convertToPolar();
		return angle;
	}
	public double getDistance() {
		if (!isPolar) convertToPolar();
		return distance;
	}
}
