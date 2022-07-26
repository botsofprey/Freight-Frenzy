package UtilityClasses;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * This is a class for representing a place on the field.
 * It is similar to the standard cartesian(x, y) plane,
 * but it is rotated 90 degrees counterclockwise.
 * This means that the x-axis is straight forward and down the field,
 * which makes the coordinates a bit cleaner.
 * A heading of 0 refers to the direction along the x-axis.
 *
 * @author Alex Prichard
 */
public class Location {
    public static final Location ORIGIN = new Location(0, 0);
    public static final Location UPPER_LEFT = new Location(72, 72);
    public static final Location LOWER_LEFT = new Location(-72, 72);
    public static final Location LOWER_RIGHT = new Location(-72, -72);
    public static final Location UPPER_RIGHT = new Location(72, -72);

    private double x;
    private double y;
    private double heading;//range [-180, 180); 0 is facing positive x

    public Location(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = normalizeHeading(heading);
    }
    public Location(double x, double y) {
        this(x, y, 0);
    }
    public Location(double heading) { this(0, 0, heading); }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getHeading() { return heading; }
    
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setHeading(double heading) { this.heading = normalizeHeading(heading); }
    
    public void addX(double x) { this.x += x; }
    public void addY(double y) { this.y += y; }
    public void addHeading(double heading) { this.heading += heading; normalizeHeading(); }

    public double getMagnitude() { return distanceToLocation(ORIGIN); }
    public double distanceToLocation(Location location) {
        return Math.hypot(x - location.x, y - location.y);
    }
    public double headingDifference(Location location) {
        return normalizeHeading(location.heading - heading);
    }
    public double headingDifference(double h) {
        return normalizeHeading(h - heading);
    }
    public double headingToLocation(Location location) {
        return Math.toDegrees(Math.atan2(location.y - y, location.x - x));
    }

    private void normalizeHeading() { heading = normalizeHeading(heading); }
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
    public String toString() {
        return "NewLocation{" +
                "x=" + round(x) +
                ", y=" + round(y) +
                ", heading=" + round(heading) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location that = (Location) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 &&
                Double.compare(that.heading, heading) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, heading);
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

    private double round(double num) {
        int scale = 100;
        num *= scale;
        int n = (int)num;
        return (double)n / scale;
    }
}
