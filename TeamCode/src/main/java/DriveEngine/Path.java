package DriveEngine;

import androidx.annotation.NonNull;

import java.util.Objects;

import UtilityClasses.Location;

public class Path {
	private Location start;
	private Location end;
	private double error;
	private double angleError;

	private double m;
	private double b;
	private double pathLength;



	public Path(Location s, Location e, double error) {
		start = s;
		end = e;
		this.error = error;
		this.angleError = error;
		calculateConstants();
	}

	public Path(Location s, Location e) {
		this(s, e, 1.5);
	}

	public Path(Location location, double error) {
		this(location, location, error);
	}

	public Path(Location location) {
		this(location, 1.5);
	}

	public Path(double error) {
		this(new Location(0, 0, 0), error);
	}

	public Path() {
		this(1.5);
	}

	public Path(Path prevPath, double error) {
		this(prevPath.end, error);
	}

	public Path(Path prevPath) {
		this(prevPath, 1.5);
	}


	public Location getStart() {
		return start;
	}

	public Location getEnd() {
		return end;
	}
	
	public double getError() {
		return error;
	}
	
	public double getAngleError() {
		return angleError;
	}

	public double getPathLength() { return pathLength; }
	
	public double getPathAngleChange() {
		return Math.abs(Location.normalizeHeading(start.getHeading() - end.getHeading()));
	}

	public void setStart(Location start) {
		this.start = start;
		calculateConstants();
	}

	public void setEnd(Location end) {
		this.end = end;
		calculateConstants();
	}
	
	public void setError(double error) {
		this.error = error;
	}
	
	public void setAngleError(double angleError) {
		this.angleError = angleError;
	}

	private double interpolate(double a, double b, double t) {
		return a + t * (b - a);
	}

	public Location interpolateLocation(double t) {
		double x = interpolate(start.getX(), end.getX(), t);
		double y = interpolate(start.getY(), end.getY(), t);
		double rotatedEndAngle =
				Location.normalizeHeading(end.getHeading() - start.getHeading());
		double a = Location.normalizeHeading(
				start.getHeading() + interpolate(0, rotatedEndAngle, t)
		);
		return new Location(x, y, a);
	}

	public double reverse_interpolate(Location location) {
		double newM = -1 / m;
		double newB = location.getY() - newM * location.getX();

		double x = (b - newB) / (newM - m);
		return (start.getX() - x) / (start.getX() - end.getX()); // TODO handle case of / by 0
	}

	public Location getTargetLocation(Location location, double distance) {
		double t = reverse_interpolate(location);
		double e = location.distanceToLocation(interpolateLocation(t));
		double distForward = Math.sqrt(distance * distance - e * e);
		t = Math.min(t + distForward / pathLength, 1);
		Location targetLocation = interpolateLocation(t);
		double r = Location.normalizeHeading(start.getHeading()
				+ t * (end.getHeading() - start.getHeading()));
		targetLocation.setHeading(r);
		return targetLocation;
	}

	private void calculateConstants() {
		m = (start.getY() - end.getY()) / (start.getX() - end.getX());
		b = start.getY() - m * start.getX();
		pathLength = start.distanceToLocation(end);
	}



	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Path path = (Path) o;
		return Objects.equals(start, path.start) && Objects.equals(end, path.end);
	}

	@Override
	public int hashCode() {
		return Objects.hash(start, end);
	}

	@Override
	public String toString() {
		return "Path{" +
				"start=" + start +
				", end=" + end +
				'}';
	}

	@NonNull
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
