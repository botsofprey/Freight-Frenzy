package UtilityClasses;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

/**
 * <p>
 * This class is an implementation of a PIDF controller.
 * </p>
 *
 * @author Alex Prichard
 */
public class PIDController {
	private interface Feedforward {
		double calculate(double setPoint, double processVariable);
	}
	private static final Feedforward DEFAULT_FEEDFORWARD = (double sP, double pV) -> 0;
	private static final double DEFAULT_IMAX = 1;
	
	private double setPoint;
	private double Kp;
	private double Ki;
	private double Kd;
	private double IMax;
	private double I;
	private Feedforward feedforward;
	private long previousTime;
	private double previousError;
	
	public PIDController(double p, double i, double d, double iMax, Feedforward f) {
		Kp = p;
		Ki = i;
		Kd = d;
		I = 0;
		IMax = iMax;
		feedforward = f;
		previousTime = System.nanoTime();
		previousError = Double.NaN;
	}
	
	public PIDController(double p, double i, double d, double iMax) {
		this(p, i, d, iMax, DEFAULT_FEEDFORWARD);
	}
	
	public PIDController(double p, double i, double d, Feedforward f) {
		this(p, i, d, DEFAULT_IMAX, f);
	}
	
	public PIDController(double p, double i, double d) {
		this(p, i, d, DEFAULT_IMAX, DEFAULT_FEEDFORWARD);
	}
	
	public PIDController(PIDCoefficients coefficients, double iMax, Feedforward f) {
		this(coefficients.p, coefficients.i, coefficients.d, iMax, f);
	}
	
	public PIDController(PIDCoefficients coefficients, Feedforward f) {
		this(coefficients.p, coefficients.i, coefficients.d, DEFAULT_IMAX, f);
	}
	
	public PIDController(PIDCoefficients coefficients, double iMax) {
		this(coefficients.p, coefficients.i, coefficients.d, iMax, DEFAULT_FEEDFORWARD);
	}
	
	public PIDController(PIDCoefficients coefficients) {
		this(coefficients.p, coefficients.i, coefficients.d, DEFAULT_IMAX, DEFAULT_FEEDFORWARD);
	}
	
	public double getSP() {
		return setPoint;
	}
	public void setSP(double newTarget) { setPoint = newTarget; }
	
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
	
	public double calculateAdjustment(double processVariable) {
		long time = System.nanoTime();
		double deltaTime = (time - previousTime) / 1_000_000_000.0;
		previousTime = time;
		double error = setPoint - processVariable;
		double P = Kp * error;
		I += Ki * error * deltaTime;
		I = Math.min(IMax, Math.max(-IMax, I));
		if (deltaTime == 0) { // if no time has passed, d term is zero
			previousError = error;
			deltaTime = 1;
		}
		else if (Double.isNaN(previousError)) { // if no previous error exists, d term is zero
			previousError = error;
		}
		double D = Kd * (error - previousError) / deltaTime;
		previousError = error;
		double F = feedforward.calculate(setPoint, processVariable);
		return P + I + D + F;
	}
}
