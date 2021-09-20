package UtilityClasses;

public class PIDController {
	private volatile double targetPoint;
	private volatile double Kp;
	private volatile double Ki;
	private volatile double Kd;
	private double P;
	private double I;
	private double D;
	private long previousTime;
	private double previousError;
	
	public PIDController(double p, double i, double d) {
		Kp = p;
		Ki = i;
		Kd = d;
		I = 0;
		previousTime = System.currentTimeMillis();
		previousError = Double.NaN;
	}
	
	public double getTargetPoint() {
		return targetPoint;
	}
	public void setTargetPoint(double newTarget) {
		targetPoint = newTarget;
		previousTime = System.currentTimeMillis();
		previousError = Double.NaN;
	}
	
	public double getKp() { return Kp; }
	public double getKi() { return Ki; }
	public double getKd() { return Kd; }
	
	public void setKp(double kp) { Kp = kp; }
	public void setKi(double ki) { Ki = ki; }
	public void setKd(double kd) { Kd = kd; }
	
	public double calculateAdjustment(double currentValue) {
		long time = System.currentTimeMillis();
		double deltaTime = (time - previousTime) / 1000.0;
		previousTime = time;
		double error = targetPoint - currentValue;
		P = Kp * error;
		I += Ki * error * deltaTime;
		if (Ki == 0) I = 0;
		if (Double.isNaN(previousError) || deltaTime == 0) {
			previousError = error;
			deltaTime = 1;
		}
		D = Kd * (error - previousError) / deltaTime;
		previousError = error;
		return P + I - D;
	}
}
