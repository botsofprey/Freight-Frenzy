package DriveEngine;

import UtilityClasses.Location;
import UtilityClasses.Matrix;

public class SplineCurve {
	public static final boolean FIRST_POINT = true;
	public static final boolean SECOND_POINT = false;
	
	private static final double[] QUADRATURE_POINTS = new double[]{
			0,
			1.0 / 3 * Math.sqrt(5 - 2 * Math.sqrt(10.0 / 7)),
			-1.0 / 3 * Math.sqrt(5 - 2 * Math.sqrt(10.0 / 7)),
			1.0 / 3 * Math.sqrt(5 + 2 * Math.sqrt(10.0 / 7)),
			-1.0 / 3 * Math.sqrt(5 + 2 * Math.sqrt(10.0 / 7))
	};
	private static final double[] QUADRATURE_WEIGHTS = new double[]{
			128.0 / 255,
			(322 + 13 * Math.sqrt(70)) / 900.0,
			(322 + 13 * Math.sqrt(70)) / 900.0,
			(322 - 13 * Math.sqrt(70)) / 900.0,
			(322 - 13 * Math.sqrt(70)) / 900.0,
	};
	
	private int degree;
	private double[][] coefficients;//the coefficient at n is multiplied by x^n
	private double length;
	
	public SplineCurve(Location start, Location end, Location startTangent, Location endTangent) {
		degree = 5;
		coefficients = new double[3][degree + 1];
		Matrix solution = new Matrix(new double[][]{//  this matrix solves for the given
				{   1,      0,      0,      0   },//    restrictions of the spline
				{   0,      0,      1,      0   },
				{   0,      0,      0,      0   },
				{   -10,    10,     -6,     -4  },
				{   15,     -15,    8,      7   },
				{   -6,     6,      -3,     -3  }
		});
		double x0 = start.getX();
		double x1 = end.getX();
		double xPrime0 = startTangent.getX();
		double xPrime1 = endTangent.getX();
		Matrix xVector = new Matrix(new double[][]{
				{   x0,     x1,     xPrime0,    xPrime1 }
		});
		coefficients[0] = xVector.transpose().mul(solution).transpose().getData()[0];
		double y0 = start.getY();
		double y1 = end.getY();
		double yPrime0 = startTangent.getX();
		double yPrime1 = endTangent.getY();
		Matrix yVector = new Matrix(new double[][]{
				{   y0,     y1,     yPrime0,    yPrime1 }
		});
		coefficients[1] = yVector.transpose().mul(solution).transpose().getData()[0];
		double h0 = start.getHeading();
		double hPrime0 = startTangent.getHeading();
		double hPrime1 = endTangent.getHeading();
		double h1 = getHeadingTarget(h0, end.getHeading(), hPrime0, hPrime1);
		Matrix hVector = new Matrix(new double[][]{
				{   h0,     h1,     hPrime0,    hPrime1 }
		});
		coefficients[2] = hVector.transpose().mul(solution).transpose().getData()[0];
		length = getIntervalLength(0, 1);
	}
	
	public SplineCurve(Location start, Location end, Location tangent, boolean fixedAngle) {
		degree = 4;
		double x0 = start.getX();
		double x1 = end.getX();
		double y0 = start.getY();
		double y1 = end.getY();
		double h0 = start.getHeading();
		double h1 = end.getHeading();
		double xPrime = tangent.getX();
		double yPrime = tangent.getY();
		double hPrime;
		Matrix solution;
		if (fixedAngle) {//first point has angle data
			solution = new Matrix(new double[][]{
					{   1,  0,  0   },
					{   0,  0,  1   },
					{   0,  0,  0   },
					{   -2, 2,  -2  },
					{   1,  -1, 1   }
			});
			hPrime = getHeadingTarget(h0, h1, tangent.getHeading(), 0);
		} else {//second point has angle data
			solution = new Matrix(new double[][]{
					{   1,  0,  0   },
					{   -2, 2,  -1  },
					{   0,  0,  0   },
					{   2,  -2, 2   },
					{   -1, 1,  -1  }
			});
			hPrime = getHeadingTarget(h0, h1, 0, tangent.getHeading());
		}
		Matrix xVector = new Matrix(new double[][]{
				{   x0, x1, xPrime  }
		});
		Matrix yVector = new Matrix(new double[][]{
				{   y0, y1, yPrime  }
		});
		Matrix hVector = new Matrix(new double[][]{
				{   h0, h1, hPrime  }
		});
		coefficients = new double[][]{
				xVector.transpose().mul(solution).transpose().getData()[0],
				yVector.transpose().mul(solution).transpose().getData()[0],
				hVector.transpose().mul(solution).transpose().getData()[0]
		};
		length = getIntervalLength(0, 1);
	}
	
	public SplineCurve(Location start, Location end) {
		degree = 1;
		double b0 = start.getX();
		double a0 = end.getX() - b0;
		double b1 = start.getY();
		double a1 = end.getY() - b1;
		double b2 = start.getHeading();
		double a2 = getHeadingTarget(b2, end.getHeading(), 0, 0) - b2;
		coefficients = new double[][]{
				{   b0, a0  },
				{   b1, a1  },
				{   b2, a2  }
		};
		length = getIntervalLength(0, 1);
	}
	
	public Location getPoint(double t) {
		t = Math.max(-1, Math.min(1, t));
		double x = 0;
		double y = 0;
		double h = 0;
		for (int i = 0; i < coefficients[0].length; i++) {
			double pow = Math.pow(t, i);
			x += coefficients[0][i] * pow;
			y += coefficients[1][i] * pow;
			h += coefficients[2][i] * pow;
		}
		return new Location(x, y, Location.normalizeHeading(h));
	}
	
	private double getIntegrand(double t) {//@see https://medium.com/@all2one/how-to-compute-the-length-of-a-spline-e44f5f04c40
		double x = 0;
		double y = 0;
		double h = 0;
		for (int i = 1; i < coefficients[0].length; i++) {
			double pow = Math.pow(t, i - 1);
			x += i * pow * coefficients[0][i];
			y += i * pow * coefficients[1][i];
			h += i * pow * coefficients[2][i];
		}
		return Math.sqrt(x * x + y * y + h * h / 100.0);
	}
	
	public double getIntervalLength(double a, double b) {//uses gaussian quadrature
		double slope = (b - a) / 2.0;
		double average = (a + b) / 2.0;
		double area = 0;
		for (int i = 0; i < 5; i++) {//@see https://en.wikipedia.org/wiki/Gaussian_quadrature
			area += QUADRATURE_WEIGHTS[i] *
					getIntegrand(slope * QUADRATURE_POINTS[i] + average);
		}
		return slope * area;
	}
	
	public Location getEndTangent() {
		Location acc = new Location(0, 0, 0);
		for (int i = 0; i < coefficients[0].length; i++) {
			acc.addWithoutNormalizing(new Location(
					i * coefficients[0][i],
					i * coefficients[1][i],
					i * coefficients[2][i]
			));
		}
		return acc;
	}
	
	private double getHeadingTarget(double start, double end,
	                                double startTangent, double endTangent) {
		double startPoint = start + 0.5 * startTangent;
		double endPoint = end - 0.5 *  endTangent;
		if (startPoint - endPoint > 180) {
			return end + 360;
		}
		if (startPoint - endPoint < -180) {
			return end - 360;
		}
		return end;
	}
	
	public double getLength() {
		return length;
	}
	
	public Location[] getEvenlySpacedPoints(double spacing, double precision) {
		int numPoints = (int)(length / spacing);
		double[] distances = new double[numPoints];
		for (int i = 0; i < numPoints; i++) {
			distances[i] = spacing * i;
		}
		Location[] points = new Location[numPoints];
		findPoints(distances, points, spacing, precision, 0, numPoints, 0, 1);
		return points;
	}
	
	private void findPoints(double[] distances, Location[] points, double spacing,
	                        double precision, int a, int b, double lower, double upper) {
		if (a == b) return;
		if (a + 1 == b) {
			findPoint(distances[a], points, a, precision, lower, upper);
			return;
		}
		double mid = (lower + upper) / 2.0;
		double midDistance = getIntervalLength(0, mid);
		int c = (int)(midDistance / spacing);
		findPoints(distances, points, spacing, precision, a, c, lower, mid);
		findPoints(distances, points, spacing, precision, c, b, mid, upper);
	}
	
	private void findPoint(double distance, Location[] points, int index,
	                           double precision, double lower, double upper) {
		while (true) {
			double mid = (lower + upper) / 2.0;
			double midDistance = getIntervalLength(0, mid);
			if (distance - precision > midDistance) {
				lower = mid;
				continue;
			}
			if (distance + precision < midDistance) {
				upper = mid;
				continue;
			}
			points[index] = getPoint(mid);
			return;
		}
	}
}