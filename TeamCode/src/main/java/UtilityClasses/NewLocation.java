package UtilityClasses;

import androidx.annotation.NonNull;

import java.util.Objects;

public class NewLocation {
    public static final NewLocation ORIGIN = new NewLocation(0, 0);
    public static final NewLocation UPPER_LEFT = new NewLocation(72, 72);
    public static final NewLocation LOWER_LEFT = new NewLocation(-72, 72);
    public static final NewLocation LOWER_RIGHT = new NewLocation(-72, -72);
    public static final NewLocation UPPER_RIGHT = new NewLocation(72, -72);

    private double x;
    private double y;
    private double heading;//range [-180, 180); 0 is facing positive x

    public NewLocation(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = normalizeHeading(heading);
    }
    public NewLocation(double x, double y) {
        this(x, y, 0);
    }
    public NewLocation(double heading) { this(0, 0, heading); }

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
    public double distanceToLocation(NewLocation location) {
        return Math.hypot(x - location.x, y - location.y);
    }
    public double headingDifference(NewLocation location) {
        return normalizeHeading(location.heading - heading);
    }
    public double headingDifference(double h) {
        return normalizeHeading(h - heading);
    }
    public double headingToLocation(NewLocation location) {
        return Math.toDegrees(Math.atan2(location.x - x, location.y - y));
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
        NewLocation that = (NewLocation) o;
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
