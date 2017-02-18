/**
 * 
 */
package org.usfirst.frc.team1529.robot;

import com.ctre.CANTalon; // For pivot of arm
import edu.wpi.first.wpilibj.DoubleSolenoid; // for basket release flap
import edu.wpi.first.wpilibj.VictorSP; // for intake wheel

/**
 * @author CyberCards
 *
 */
public class GearArm {
	/*
	 * Instance Variables
	 */
	// Main arm motion*********************************
	/* Talon SRX to rotate arm
	 * Input is CAN Device ID
	 * See reference manual as needed.
	 */
	CANTalon pivot;
	int position; // arm position

	// Encoder to measure rotation ********************
	// TODO add encoder
	
	// Intake System **********************************
	DoubleSolenoid flap; // Pneumatic Flap
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
		pivot = new CANTalon(talonCANID);
		flap = new DoubleSolenoid(flapPCMID1, flapPCMID2);
		intake = new VictorSP(intakeMotorPWM);
		intakeOff(); // ensure off; and set isIntake
		position = 0;
	}
	
	/**
	 * Instance Methods
	 */
	
	// TODO reset Encoder position
	
	/**
	 * Rotate arm speed
	 * @param speed
	 */
	public void setSpeed(double speed) {
		pivot.set(speed);
	}
	
	/**
	 * Stops the arm from moving.
	 */
	public void stop() {
		pivot.set(0);
	}
	
	// TODO set Arm position; need encoder; probably PID controller
	

	
	/*
	 * Intake sub-system methods
	 */
	/**
	 * Open the flap.
	 */
	public void releaseFlap() {
		flap.set(DoubleSolenoid.Value.kForward);
	}
	
	/**
	 * Close the flap.
	 */
	public void closeFlap() {
		flap.set(DoubleSolenoid.Value.kReverse);
	}
	
	/**
	 * Turn intake wheels on.
	 */
	public void intakeOn() {
		intake.setSpeed(1);
		isIntake = true;
	}
	
	/**
	 * Turn intake wheels off.
	 */
	public void intakeOff() {
		intake.setSpeed(0);
		isIntake = false;
	}
}
