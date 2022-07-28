package UtilityClasses;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

/**
 * <p>
 * This class is an implementation of a PIDF controller
 * with a low pass filter for the derivative term.
 * Give it a set point to target with setSP.
 * Run calculateResponse with the current state of the thing you are controlling
 * and apply the output to that thing. ex:
 * <pre>
 * {@code motor1.setPower(controller.calculateResponse(motor1.getCurrentPosition()));}
 * </pre>
 * </p>
 *
 * @author Alex Prichard
 */
public class PIDController {
	private interface Feedforward {
		double calculate(double setPoint, double processVariable);
	}
	public static final double DEFAULT_IMAX = 1;
	// I picked 20 as the recommended value just because
	// it worked kind of well in a few python simulations.
	// Feel free to adjust it if you find a value that works better.
	public static final double RECOMMENDED_VALUE = 20;
	// By default, there is no derivative filter so old code doesn't break.
	public static final double DEFAULT_ATTENUATION_FREQUENCY = Double.POSITIVE_INFINITY;
	public static final Feedforward DEFAULT_FEEDFORWARD = (double sP, double pV) -> 0;
	
	private double setPoint;
	private double Kp;
	private double Ki;
	private double Kd;
	private double IMax;
	private double I;
	private double attenuationFrequency;
	private double D;
	private Feedforward feedforward;
	private long previousTime;
	private double previousError;
	
	public PIDController(double p, double i, double d,
	                     double iMax, Feedforward f, double filter_cutoff) {
		Kp = p;
		Ki = i;
		Kd = d;
		IMax = iMax;
		I = 0;
		attenuationFrequency = filter_cutoff;
		D = Double.NaN;
		feedforward = f;
		previousTime = System.nanoTime();
		previousError = Double.NaN;
	}
	
	public PIDController(double p, double i, double d, double iMax, double filter_cutoff) {
		this(p, i, d,
				iMax, DEFAULT_FEEDFORWARD, filter_cutoff);
	}
	
	public PIDController(double p, double i, double d, double iMax) {
		this(p, i, d,
				iMax, DEFAULT_FEEDFORWARD, DEFAULT_ATTENUATION_FREQUENCY);
	}
	
	public PIDController(double p, double i, double d, Feedforward f, double filter_cutoff) {
		this(p, i, d,
				DEFAULT_IMAX, f, filter_cutoff);
	}
	
	public PIDController(double p, double i, double d, Feedforward f) {
		this(p, i, d,
				DEFAULT_IMAX, f, DEFAULT_ATTENUATION_FREQUENCY);
	}
	
	public PIDController(double p, double i, double d) {
		this(p, i, d,
				DEFAULT_IMAX, DEFAULT_FEEDFORWARD, DEFAULT_ATTENUATION_FREQUENCY);
	}
	
	public PIDController(@NonNull PIDCoefficients coefficients,
	                     double iMax, Feedforward f, double filter_cutoff) {
		this(coefficients.p, coefficients.i, coefficients.d,
				iMax, f, filter_cutoff);
	}
	
	public PIDController(@NonNull PIDCoefficients coefficients, double iMax, Feedforward f) {
		this(coefficients.p, coefficients.i, coefficients.d,
				iMax, f, DEFAULT_ATTENUATION_FREQUENCY);
	}
	
	public PIDController(@NonNull PIDCoefficients coefficients,
	                     Feedforward f, double filter_cutoff) {
		this(coefficients.p, coefficients.i, coefficients.d,
				DEFAULT_IMAX, f, filter_cutoff);
	}
	
	public PIDController(@NonNull PIDCoefficients coefficients, Feedforward f) {
		this(coefficients.p, coefficients.i, coefficients.d,
				DEFAULT_IMAX, f, DEFAULT_ATTENUATION_FREQUENCY);
	}
	
	public PIDController(@NonNull PIDCoefficients coefficients, double iMax, double filter_cutoff) {
		this(coefficients.p, coefficients.i, coefficients.d,
				iMax, DEFAULT_FEEDFORWARD, filter_cutoff);
	}
	
	public PIDController(@NonNull PIDCoefficients coefficients, double iMax) {
		this(coefficients.p, coefficients.i, coefficients.d,
				iMax, DEFAULT_FEEDFORWARD, DEFAULT_ATTENUATION_FREQUENCY);
	}
	
	public PIDController(@NonNull PIDCoefficients coefficients) {
		this(coefficients.p, coefficients.i, coefficients.d,
				DEFAULT_IMAX, DEFAULT_FEEDFORWARD, DEFAULT_ATTENUATION_FREQUENCY);
	}
	
	public double getSP() { return setPoint; }
	public void setSP(double newTarget) { setPoint = newTarget; }
	
	public double getKp() { return Kp; }
	public double getKi() { return Ki; }
	public double getKd() { return Kd; }
	public double getIMax() { return IMax; }
	public double getAttenuationFrequency() { return attenuationFrequency; }
	public Feedforward getFeedforward() { return feedforward; }
	
	public void setKp(double kp) { Kp = kp; }
	public void setKi(double ki) { Ki = ki; if (ki == 0) I = 0; }
	public void setKd(double kd) { Kd = kd; }
	public void setIMax(double iMax) { IMax = iMax; }
	public void setAttenuationFrequency(double attenuationFrequency) {
		this.attenuationFrequency = attenuationFrequency;
	}
	public void setFeedforward(Feedforward feedforward) { this.feedforward = feedforward; }
	
	public void reset() {
		I = 0;
		D = Double.NaN;
		previousTime = System.nanoTime();
		previousError = Double.NaN;
	}
	
	/**
	 * This applies a low pass filter to the derivative term calculated,
	 * which has an effect of reducing noise.
	 * @see <a href="https://en.wikipedia.org/wiki/Low-pass_filter">Low pass filter</a>
	 *
	 * @param input
	 * @param deltaTime
	 * @author Alex Prichard
	 */
	private void singlePoleLowPassFilter(double input, double deltaTime) {
		double alpha = 2 * Math.PI * deltaTime * attenuationFrequency;
		alpha /= alpha + 1;
		if (Double.isInfinite(attenuationFrequency)) {
			alpha = 1;
		}
		if (Double.isNaN(D)) {
			D = alpha * input;
		}
		else {
			D += alpha * (input - D);
		}
	}
	
	public double calculateResponse(double processVariable) {
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
		singlePoleLowPassFilter(Kd * (error - previousError) / deltaTime, deltaTime);
		previousError = error;
		
		double F = feedforward.calculate(setPoint, processVariable);
		return P + I + D + F;
	}
}
