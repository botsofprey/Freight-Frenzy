package UtilityClasses;

import java.util.Objects;

public class Location {
	private double x;
	private double y;
	private double heading;
	
	public Location(double x, double y, double heading) {
		this.x = x;
		this.y = y;
		this.heading = heading;
	}
	
	public Location(double x, double y) {
		this(x, y, 0);
	}
	
	public Location(Location location, double heading) {
		this(location.x, location.y, heading);
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	public double getHeading() { return heading; }
	
	public void setX(double x) { this.x = x; }
	public void setY(double y) { this.y = y; }
	public void setHeading(double heading) { this.heading = heading; }
	
	public Location addX(double dx) { x += dx; return this; }
	public Location addY(double dy) { y += dy; return this; }
	public Location addHeading(double dHeading) { heading += dHeading; return this; }
	public Location addXY(double dx, double dy) {
		x += dx;
		y += dy;
		return this;
	}
	public Location add(Location location) {
		x += location.x;
		y += location.y;
		heading += location.heading;
		return this;
	}
	
	public double distanceToLocation(Location location) {
		return Math.hypot(x - location.x, y - location.y);
	}
	public double headingToLocation(Location location) {
		return Math.toDegrees(Math.atan2(-x, y));
	}
	
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Location location = (Location) o;
		return Double.compare(location.x, x) == 0 && Double.compare(location.y, y) == 0 && Double.compare(location.heading, heading) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y, heading);
	}
	
	@Override
	public String toString() {
		return "Location{" +
				"x=" + x +
				", y=" + y +
				", heading=" + heading +
				'}';
	}
}
