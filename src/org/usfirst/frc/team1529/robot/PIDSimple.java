package org.usfirst.frc.team1529.robot;

import edu.wpi.first.wpilibj.Encoder;

public class PIDSimple {
	private double kP, kI, kD, tolerance;
	private Encoder encoder;
	private int target;
	private double current_error, prior_error, slope, sum_errors;
	private boolean reset_status;
	
	public PIDSimple(double kp, double ki, double kd, double tol, Encoder enc) {
		kP = kp;
		kI = ki;
		kD = kd;
		tolerance = tol;
		encoder = enc;
		reset();
	}
	
	public void reset() {
		current_error = 0;
		prior_error = 0;
		slope = 0;
		sum_errors = 0;
		reset_status = true;
	}
	
	public void set_target(int targ) {
		target = targ;
		reset_status = false;
	}
	
	public boolean isReset() {
		return reset_status;
	}
	
	public double getOutput() {
		setVars();
		if(isWithinTolerance()) {
			return 0.0;
		} else {
			return kP * current_error + kD * slope + kI * sum_errors;
		}
	}
	
	public boolean isWithinTolerance() {
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
