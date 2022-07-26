package DriveEngine;

import java.util.ArrayList;

import UtilityClasses.OldLocationClass;

public class TrajectoryBuilder {
	private ArrayList<OldLocationClass> waypoints = new ArrayList<>();
	private ArrayList<OldLocationClass> tangents = new ArrayList<>();
	private DriveConstraints constraints;
	
	public TrajectoryBuilder(OldLocationClass start, DriveConstraints driveConstraints) {
		waypoints.add(start);
		tangents.add(null);
		constraints = driveConstraints;
	}
	
	public TrajectoryBuilder(OldLocationClass start, double tangentAngle,
	                         double tangentMagnitude, DriveConstraints driveConstraints) {
		waypoints.add(start);
		tangents.add(new OldLocationClass(tangentMagnitude * Math.cos(tangentAngle),
				tangentMagnitude * Math.sin(tangentAngle)));
	}
	
	public TrajectoryBuilder splineToLocation(OldLocationClass location) {
		waypoints.add(location);
		tangents.add(null);
		return this;
	}
	
	public TrajectoryBuilder splineToLocation(OldLocationClass location,
	                                          double tangentAngle, double tangentMagnitude) {
		waypoints.add(location);
		tangents.add(new OldLocationClass(tangentMagnitude * Math.cos(tangentAngle),
				tangentMagnitude * Math.sin(tangentAngle)));
		return this;
	}
	
	public TrajectoryBuilder lineToLocation(OldLocationClass location) {
		double xDiff = location.getX() - waypoints.get(waypoints.size() - 1).getX();
		double yDiff = location.getY() - waypoints.get(waypoints.size() - 1).getY();
		tangents.set(tangents.size(), new OldLocationClass(xDiff, yDiff));
		waypoints.add(location);
		tangents.add(null);
		return this;
	}
	
	public Trajectory build() {
		return new Trajectory(waypoints, tangents, constraints);
	}
}
