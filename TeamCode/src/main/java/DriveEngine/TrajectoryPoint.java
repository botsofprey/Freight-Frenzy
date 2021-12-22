package DriveEngine;

import UtilityClasses.Location;

public class TrajectoryPoint {
	public double t;
	public double x;
	public double y;
	public double h;
	public double vx;
	public double vy;
	public double vh;
	public double ax;
	public double ay;
	public double ah;
	
	public TrajectoryPoint(Location location) {
		x = location.getX();
		y = location.getY();
		h = location.getHeading();
	}
	
	public double getDistance(TrajectoryPoint other) {
		return Math.hypot(other.x - x, other.y - y);
	}
	
	public double getVelocity() {
		return Math.hypot(vx, vy);
	}
	
	public void calculateVelocity(TrajectoryPoint next, DriveConstraints constraints) {
		vx = next.x - x;
		vy = next.y - y;
		double scale = constraints.maxVelocity / Math.hypot(vx, vy);
		vx *= scale;
		vy *= scale;
		vh = next.h - h;
		vh /= (vh == 0 ? 1 : Math.abs(vh));
		vh *= constraints.maxAngularVelocity;
	}
}
