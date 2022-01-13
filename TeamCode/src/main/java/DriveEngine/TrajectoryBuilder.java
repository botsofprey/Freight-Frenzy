package DriveEngine;

import java.util.ArrayList;

import UtilityClasses.Location;

public class TrajectoryBuilder {
	private ArrayList<Location> waypoints = new ArrayList<>();
	private ArrayList<Location> tangents = new ArrayList<>();
	private DriveConstraints constraints;
	
	public TrajectoryBuilder(Location start, DriveConstraints driveConstraints) {
		waypoints.add(start);
		tangents.add(null);
		constraints = driveConstraints;
	}
	
	public TrajectoryBuilder(Location start, double tangentAngle,
							 double tangentMagnitude, DriveConstraints driveConstraints) {
		waypoints.add(start);
		tangents.add(new Location(tangentMagnitude * Math.cos(tangentAngle),
				tangentMagnitude * Math.sin(tangentAngle)));
	}
	
	public TrajectoryBuilder splineToLocation(Location location) {
		waypoints.add(location);
		tangents.add(null);
		return this;
	}
	
	public TrajectoryBuilder splineToLocation(Location location,
											  double tangentAngle, double tangentMagnitude) {
		waypoints.add(location);
		tangents.add(new Location(tangentMagnitude * Math.cos(tangentAngle),
				tangentMagnitude * Math.sin(tangentAngle)));
		return this;
	}
	
	public TrajectoryBuilder lineToLocation(Location location) {
		double xDiff = location.getX() - waypoints.get(waypoints.size() - 1).getX();
		double yDiff = location.getY() - waypoints.get(waypoints.size() - 1).getY();
		tangents.set(tangents.size(), new Location(xDiff, yDiff));
		waypoints.add(location);
		tangents.add(null);
		return this;
	}
	
	public Trajectory build() {
		return new Trajectory(waypoints, tangents, constraints);
	}
}
