package org.usfirst.frc.team1529.robot;

import edu.wpi.first.wpilibj.Encoder;

public class PIDSimple {
	private double kP, kI, kD, tolerance;
	private Encoder encoder;
	private int target;
	private double current_error, prior_error, slope, sum_errors;
	
	public PIDSimple(double kp, double ki, double kd, int tol, Encoder enc) {
		kP = kp;
		kI = ki;
		kD = kd;
		tolerance = (double) tol;
		encoder = enc;
		reset();
	}
	
	public void reset() {
		current_error = 0;
		prior_error = 0;
		slope = 0;
		sum_errors = 0;
	}
	
	public void set_target(int targ) {
		target = targ;
	}
	
	public double getOutput() {
		setVars();
		if(isWithinTolerance()) {
			return 0.0;
		} else {
			return kP * current_error + kD * slope + kI * sum_errors;
		}
	}
	
	private boolean isWithinTolerance() {
		return Math.abs(current_error) <= tolerance;
	}
	
	private void setVars() {
		prior_error = current_error;
		current_error = getError();
		slope = current_error - prior_error;
		sum_errors += current_error;
	}
	
	private double getError() {
		return (double) (target - encoder.get());
	}
}
