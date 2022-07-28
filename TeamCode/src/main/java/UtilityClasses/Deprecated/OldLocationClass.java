package UtilityClasses.Deprecated;

import java.util.Objects;

import UtilityClasses.Location;

@Deprecated
public class OldLocationClass {
	public static final OldLocationClass ORIGIN = new OldLocationClass(0, 0, 0);

	private double x;
	private double y;
	private double heading;//range [-180, 180)
	
	public OldLocationClass(double x, double y, double heading) {
		this.x = x;
		this.y = y;
		this.heading = heading;
	}
	
	public OldLocationClass(double x, double y) {
		this(x, y, 0);
	}
	
	public OldLocationClass(OldLocationClass location, double heading) {
		this(location.x, location.y, heading);
	}
	
	public OldLocationClass(Location location) {
		this(-location.getY(), location.getX(), location.getHeading());
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	public double getHeading() { return heading; }
	
	public OldLocationClass setX(double x) { this.x = x; return this; }
	public OldLocationClass setY(double y) { this.y = y; return this; }
	public OldLocationClass setHeading(double heading) { this.heading = heading; return this; }
	
	public OldLocationClass addX(double dx) { x += dx; return this; }
	public OldLocationClass addY(double dy) { y += dy; return this; }
	public OldLocationClass addHeading(double dHeading) {
		heading += dHeading;
		return this;
	}
	public OldLocationClass addXY(double dx, double dy) {
		x += dx;
		y += dy;
		return this;
	}
	public OldLocationClass add(OldLocationClass location) {
		x += location.x;
		y += location.y;
		heading += location.heading;
		normalizeHeading();
		return this;
	}
	public OldLocationClass addWithoutNormalizing(OldLocationClass location) {
		x += location.x;
		y += location.y;
		heading += location.heading;
		return this;
	}
	public OldLocationClass subXY(OldLocationClass location) {
		x -= location.x;
		y -= location.y;
		return this;
	}
	public OldLocationClass scale(double s) {
		x *= s;
		y *= s;
		heading *= s;
		normalizeHeading();
		return this;
	}
	
	public double distanceToLocation(OldLocationClass location) {
		return Math.hypot(x - location.x, y - location.y);
	}
	
	public double headingDifference(OldLocationClass location) {
		return normalizeHeading(location.heading - heading);
	}
	
	public double headingDifference(double h) {
		return normalizeHeading(h - heading);
	}
	
	public double headingToLocation(OldLocationClass location) {
		return Math.toDegrees(Math.atan2(-x, y));
	}
	
	public void normalizeHeading() {
		heading = normalizeHeading(heading);
	}

	public static double normalizeHeading(double h) {
		h %= 360;
		if (h < -180) {
			h += 360;
		}
		else if (h >= 180) {
			h -= 360;
		}
		return h;
	}
	
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OldLocationClass location = (OldLocationClass) o;
		return Double.compare(location.x, x) == 0 && Double.compare(location.y, y) == 0 && Double.compare(location.heading, heading) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y, heading);
	}
	
	@Override
	public String toString() {
		return "Location{" +
				"x=" + round(x) +
				", y=" + round(y) +
				", heading=" + round(heading) +
				'}';
	}
	
	public static double round(double number) {
		return (int)(number * 100) / 100.0;
	}
}
