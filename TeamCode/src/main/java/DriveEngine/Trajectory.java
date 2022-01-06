package DriveEngine;

import java.util.ArrayList;

import UtilityClasses.Location;

public class Trajectory {
	private TrajectoryPoint[] trajectory;
	private int previousMotion;
	
	public Trajectory(ArrayList<Location> waypoints,
	                  ArrayList<Location> tangents, DriveConstraints constraints) {
		ArrayList<SplineCurve> splines = new ArrayList<>();
		for (int i = 1; i < waypoints.size(); i++) {
			Location start = waypoints.get(i - 1);
			Location end = waypoints.get(i);
			Location startTangent = tangents.get(i - 1);
			Location endTangent = tangents.get(i);
			switch ((startTangent == null ? 0 : 1) | (endTangent == null ? 0 : 2)) {
				case 0:
					splines.add(new SplineCurve(start, end));
					tangents.set(i, splines.get(i).getEndTangent());
					break;
				case 1:
					splines.add(new SplineCurve(start, end, startTangent, SplineCurve.FIRST_POINT));
					tangents.set(i, splines.get(i).getEndTangent());
					break;
				case 2:
					splines.add(new SplineCurve(start, end, endTangent, SplineCurve.SECOND_POINT));
					break;
				case 3:
					splines.add(new SplineCurve(start, end, startTangent, endTangent));
					break;
			}
		}
		int numPoints = 0;
		double spacing = 0.1;
		for (int i = 0; i < splines.size(); i++) {
			numPoints += (int)(splines.get(i).getLength() / spacing);
		}
		Location[] points = new Location[numPoints];
		int index = 0;
		for (int i = 0; i < splines.size(); i++) {
			Location[] temp = splines.get(i).getEvenlySpacedPoints(spacing, 0.005);
			System.arraycopy(temp, 0, points, index, temp.length);
			index += temp.length;
		}
		
		calculateMotionControlledTrajectory(points, constraints);
		
		previousMotion = 0;
	}
	
	private void calculateMotionControlledTrajectory(Location[] points,
	                                                 DriveConstraints constraints) {
		trajectory = new TrajectoryPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			trajectory[i] = new TrajectoryPoint(points[i]);
		}
		for (int i = 0; i < trajectory.length - 1; i++) {
			trajectory[i].calculateVelocity(trajectory[i + 1], constraints);
		}
		trajectory[0].vx = 0;
		trajectory[0].vy = 0;
		trajectory[0].vh = 0;
		trajectory[trajectory.length - 1].vx = 0;
		trajectory[trajectory.length - 1].vy = 0;
		trajectory[trajectory.length - 1].vh = 0;
		for (int i = 0; i < trajectory.length - 1; i++) {
			double vx0 = trajectory[i].vx;
			double vx1 = trajectory[i + 1].vx;
			double x = trajectory[i + 1].x - trajectory[i].x;
			double ax = (vx1 * vx1 - vx0 * vx0) / (2 * x);
			double vy0 = trajectory[i].vy;
			double vy1 = trajectory[i + 1].vy;
			double y = trajectory[i + 1].y - trajectory[i].y;
			double ay = (vy1 * vy1 - vy0 * vy0) / (2 * y);
			double l = Math.hypot(ax, ay);
			if (l > constraints.maxAcceleration) {
				ax /= l;
				ay /= l;
				trajectory[i + 1].vx = Math.sqrt(vx0 * vx0 + 2 * x * ax);
				trajectory[i + 1].vy = Math.sqrt(vy0 * vy0 + 2 * y * ay);
			}
			double vh0 = trajectory[i].vh;
			double vh1 = trajectory[i + 1].vh;
			double h = trajectory[i + 1].h - trajectory[i].h;
			double ah = (vh1 * vh1 - vh0 * vh0) / (2 * h);
			l = Math.abs(ah);
			if (l > constraints.maxAngularAcceleration) {
				ah /= l;
				trajectory[i + 1].vh = Math.sqrt(vh0 * vh0 + 2 * h * ah);
			}
		}
		trajectory[trajectory.length - 1].vx = 0;
		trajectory[trajectory.length - 1].vy = 0;
		trajectory[trajectory.length - 1].vh = 0;
		for (int i = trajectory.length - 1; i > 1; i--) {
			double vx0 = trajectory[i - 1].vx;
			double vx1 = trajectory[i].vx;
			double x = trajectory[i].x - trajectory[i - 1].x;
			double ax = (vx1 * vx1 - vx0 * vx0) / (2 * x);
			double vy0 = trajectory[i - 1].vy;
			double vy1 = trajectory[i].vy;
			double y = trajectory[i].y - trajectory[i - 1].y;
			double ay = (vy1 * vy1 - vy0 * vy0) / (2 * y);
			double l = Math.hypot(ax, ay);
			if (l > constraints.maxAcceleration) {
				ax /= l;
				ay /= l;
				trajectory[i - 1].vx = Math.sqrt(vx1 * vx1 - 2 * ax * x);
				trajectory[i - 1].vy = Math.sqrt(vy1 * vy1 - 2 * ay * y);
			}
			trajectory[i - 1].ax = ax;
			trajectory[i - 1].ay = ay;
			double vh0 = trajectory[i - 1].vh;
			double vh1 = trajectory[i].vh;
			double h = trajectory[i].h - trajectory[i - 1].h;
			double ah = (vh1 * vh1 - vh0 * vh0) / (2 * h);
			l = Math.abs(ah);
			if (l > constraints.maxAngularAcceleration) {
				ah /= l;
				trajectory[i - 1].vh = Math.sqrt(vh1 * vh1 - 2 * ah * h);
			}
			trajectory[i - 1].ah = ah;
		}
		trajectory[0].t = 0;
		for (int i = 1; i < trajectory.length; i++) {
			double moveTime = 2 * (trajectory[i].x - trajectory[i - 1].x) /
						(trajectory[i].vx + trajectory[i - 1].vx);
			double turnTime = 2 * (trajectory[i].h - trajectory[i - 1].h) /
					(trajectory[i].vh + trajectory[i - 1].vh);
			trajectory[i].t = trajectory[i - 1].t + Math.max(moveTime, turnTime);
		}
	}
	
	public TrajectoryPoint getMotion(double t) {
		int point = trajectory.length - 1;
		for (int i = previousMotion; i < trajectory.length; i++) {
			if (t - trajectory[i].t < 0) {
				point = i;
				break;
			}
		}
		double t0 = Math.abs(trajectory[point - 1].t - t);
		double t1 = Math.abs(trajectory[point].t - t);
		return t0 > t1 ? trajectory[point] : trajectory[point - 1];
	}

	public double getDuration() {
		return trajectory[trajectory.length - 1].t;
	}
}
