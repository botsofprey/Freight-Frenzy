package UtilityClasses;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

public class PIDController {
	private volatile double targetPoint;
	private volatile double Kp;
	private volatile double Ki;
	private volatile double Kd;
	private volatile double IMax;
	private double I;
	private long previousTime;
	private double previousError;
	
	public PIDController(double p, double i, double d, double iMax) {
		Kp = p;
		Ki = i;
		Kd = d;
		I = 0;
		IMax = iMax;
		previousTime = System.nanoTime();
		previousError = Double.NaN;
	}
	
	public PIDController(double p, double i, double d) {
		this(p, i, d, 1);
	}
	
	public PIDController(PIDCoefficients coefficients) {
		this(coefficients.p, coefficients.i, coefficients.d);
	}
	
	public PIDController(PIDCoefficients coefficients, double iMax) {
		this(coefficients.p, coefficients.i, coefficients.d, iMax);
	}
	
	public double getTargetPoint() {
		return targetPoint;
	}
	public void setTargetPoint(double newTarget) { targetPoint = newTarget; }
	
	public double getKp() { return Kp; }
	public double getKi() { return Ki; }
	public double getKd() { return Kd; }
	public double getIMax() { return IMax; }
	
	public void setKp(double kp) { Kp = kp; }
	public void setKi(double ki) { Ki = ki; if (ki == 0) I = 0; }
	public void setKd(double kd) { Kd = kd; }
	public void setIMax(double iMax) { IMax = iMax; }
	
	public void reset() {
		I = 0;
		previousTime = System.nanoTime();
		previousError = Double.NaN;
	}
	
	public double calculateAdjustment(double currentValue) {
		long time = System.nanoTime();
		double deltaTime = (time - previousTime) / 1_000_000_000.0;
		previousTime = time;
		double error = targetPoint - currentValue;
		double P = Kp * error;
		I += Ki * error * deltaTime;
		I = Math.min(IMax, Math.max(-IMax, I));
		if (deltaTime == 0) {
			previousError = error;
			deltaTime = 1;
		}
		else if (Double.isNaN(previousError)) {
			previousError = error;
		}
		double D = Kd * (error - previousError) / deltaTime;
		previousError = error;
		return P + I + D;
	}
}
