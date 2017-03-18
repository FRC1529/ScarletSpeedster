/**
 * 
 */
package org.usfirst.frc.team1529.robot;

import com.ctre.CANTalon; // For pivot of arm
import edu.wpi.first.wpilibj.DoubleSolenoid; // for basket release flap
//import edu.wpi.first.wpilibj.Encoder; // NOT CURRENTLY USING ENCODER ON THE ARM.
import edu.wpi.first.wpilibj.VictorSP; // for intake wheel

/**
 * @author CyberCards
 *
 */
public class GearArm {
	
	private boolean intake_enabled;
	/*
	 * Instance Variables
	 */
	CANTalon pivot;

	// Encoder to measure rotation ********************
	
	// Intake System **********************************
	DoubleSolenoid flap; // Pneumatic Flap
	private DoubleSolenoid.Value CLOSE 	= DoubleSolenoid.Value.kForward;
	private DoubleSolenoid.Value OPEN 	= DoubleSolenoid.Value.kReverse;
	private DoubleSolenoid.Value OFF	= DoubleSolenoid.Value.kOff;
	VictorSP intake;// Intake Motor Victor
	boolean isIntake;
	
	/**
	 * Constructor Methods for GearArm
	 */
	
	/**
	 * 
	 * @param talonCANID
	 * @param flapPCMID1
	 * @param flapPCMID2
	 * @param intakeMotorPWM
	 */
	public GearArm(int talonCANID, int flapPCMID1, int flapPCMID2, int intakeMotorPWM) {
//		enableIntake(); //TODO: currently just sets and returns enable_intake; potentially want to disable.
		
		pivot 	= new CANTalon(talonCANID);
		flap 	= new DoubleSolenoid(flapPCMID1, flapPCMID2);
		intake 	= new VictorSP(intakeMotorPWM);
		flap.set(CLOSE);
		intakeOff(); // ensure off; and set isIntake
	}
	
	/**
	 * Instance Methods
	 */
	
	public void control(EnhancedDriverStation station) {
		controlArmSystem(station);
		controlIntakeSystem(station);
	}
	
	private void controlArmSystem(EnhancedDriverStation station) {
		double direction 	= 1.0;
		double upSpeed 		= direction * 0.2;
		double downSpeed 	= -direction * 0.2;
		Logger.log("TRYING TO SET ARM");
		if(station.armUp()) {
			pivot.set(upSpeed);
		} else if(station.armDown()) {
			pivot.set(downSpeed);
		} else {
			stop();
		}
	}
	
	private void controlIntakeSystem(EnhancedDriverStation station) {
		controlIntake(station);
		controlFlap(station);
	}
	
	private void controlIntake(EnhancedDriverStation station) {
		if(station.isIntake()) {
			intakeOn();
		} else if(station.isOuttake()) {
			outtakeOn();
		} else {
			intakeOff();
		}
	}
	
	private void controlFlap(EnhancedDriverStation station) {
		if(station.flapOpen()) {
			openFlap();
		} else if(station.flapClose()) {
			closeFlap();
		} else {
			flapOff();
		}
	}
	
	private void flapOff() {
		flap.set(OFF);
	}
	
	
	/**
	 * Rotate arm speed
	 * @param speed
	 */
//	private void setSpeed(double speed) { pivot.set(speed); } // NOT USED
	
	/**
	 * Stops the arm from moving.
	 */
	private void stop() { pivot.set(0); }
	
	/**
	 * Open the flap.
	 */
	private void openFlap() { flap.set(OPEN); }
	
	/**
	 * Close the flap.
	 */
	public void closeFlap() { flap.set(CLOSE); }
	
	/**
	 * Turn intake wheels on.
	 */
	public void intakeOn() { intakeOn(1.0); }
	
	public void outtakeOn() { intakeOn(-1.0); }
	
	/**
	 * Set intake speed. Created to adjust more quickly elsewhere in code, if needed.
	 * @param val
	 */
	public void intakeOn(Double val) {
		intake.setSpeed(-val);
		isIntake = true;
	}
	
	/**
	 * Turn intake wheels off.
	 */
	public void intakeOff() {
		intake.setSpeed(0.0);
		isIntake = false;
	}
	
	
	//TODO: only potential code. Not implemented yet.
	//TODO: update comment when implemented.
	private boolean enableIntake() { return intake_enabled = true; }
	
	private boolean disableIntake() { return intake_enabled = false; }
}
