package org.usfirst.frc.team1529.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class TankDriveSystem {
	/*
	 * Instance Variables
	 */
	
	/* Left and Right Drive
	 * inputs: PWM ports for 3 victors per side
	 */
	// TODO make sure left and right drive's direction is set correctly.
	DriveSystem leftDrive, rightDrive;
	
	/* Pneumatic Shifter
	 * 2 params: kForward port, kReverse port
	 * 3 params: moduleID, kForward port, kReverse port
	 */
	DoubleSolenoid driveShifter; // module 0, kForward, kReverse; kForward should be first gear
	DoubleSolenoid climbShifter;
	private DoubleSolenoid.Value FORWARD 	= DoubleSolenoid.Value.kForward;
	private DoubleSolenoid.Value REVERSE 	= DoubleSolenoid.Value.kReverse;
	private DoubleSolenoid.Value OFF 		= DoubleSolenoid.Value.kOff;
	
	// TODO Encoder
	
	/*
	 * TankDriveSystem constructor
	 */
	
	public TankDriveSystem(int[] leftPorts, int[] rightPorts, int[] driveSolenoid, int[] climbSolenoid) {
		leftDrive 	= new DriveSystem(false, leftPorts[0], leftPorts[1], leftPorts[2]);
		rightDrive 	= new DriveSystem(true, rightPorts[0], rightPorts[1], rightPorts[2]);
		driveShifter = new DoubleSolenoid(driveSolenoid[0], driveSolenoid[1], driveSolenoid[2]);
		driveShifter.set(FORWARD);
		
		climbShifter = new DoubleSolenoid(climbSolenoid[0], climbSolenoid[1], climbSolenoid[2]);
		climbShifter.set(REVERSE);

	}
	
	/***************************
	 * Methods
	 ***************************/
	/**
	 * Drives the robot based on station input. Assumes station has 2 joysticks.
	 * @param station
	 */
	public void drive(EnhancedDriverStation station) {
		setSpeed(station);
		checkShifters(station);
	}
	
	private void setSpeed(EnhancedDriverStation station) {
		leftDrive.setSpeed(station.leftStickValue());
		rightDrive.setSpeed(station.rightStickValue());
	}
	
	private void checkShifters(EnhancedDriverStation station) {
		if(station.shiftUp()) upShift();
		if(station.shiftDown()) downShift();
		if(isUpShifted(driveShifter) && station.isDownShiftBand()) downShift("Auto down shift!!!!!!!");
	}
	
	private boolean isUpShifted(DoubleSolenoid shifter) { return shifter.get() == FORWARD; }
	
	private void downShift() { downShift("Down shifting..."); }
	
	private void downShift(String msg) {
		Logger.log(msg);
		driveShifter.set(REVERSE);
	}
	
	private void upShift() {
		Logger.log("Shifting Up");
		driveShifter.set(FORWARD);
	}
	
	/**
	 * shiftToClimb: shifts the pneumatic solenoid out such that it shifts the drive motors to the climb system.
	 * @param station
	 */
	public void shiftToClimb(EnhancedDriverStation station) {
		Logger.log("Shifted to climb!");
		climbShifter.set(FORWARD);
	}
	
	/**
	 * Set motor speeds to climb. Forces motors to turn in only one direction.
	 * @param station
	 */
	public void climb(EnhancedDriverStation station) {
		double speed = climbSpeed(station);

		Logger.log("you're trying to climb!!!!");
		leftDrive.setSpeed(speed);
		rightDrive.setSpeed(speed);
	}
	
	private double climbSpeed(EnhancedDriverStation station) {
		double left = station.leftAbs();
		double right = station.rightAbs();
		return (left + right) / 2.0;
	}
}
