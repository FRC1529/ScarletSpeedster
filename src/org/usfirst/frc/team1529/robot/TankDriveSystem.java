package org.usfirst.frc.team1529.robot;

import edu.wpi.first.wpilibj.Joystick;

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
	 * 
	 */
	
	// TODO Encoder
	
	/*
	 * TankDriveSystem constructor
	 */
	
	public TankDriveSystem(int[] leftPorts, int[] rightPorts) {
		leftDrive = new DriveSystem(false, leftPorts[0], leftPorts[1], leftPorts[2]);
		rightDrive = new DriveSystem(true, rightPorts[0], rightPorts[1], rightPorts[2]);
	}
	
	/***************************
	 * Methods
	 ***************************/
	/**
	 * Drives the robot based on station input. Assumes station has 2 joysticks.
	 * @param station
	 */
	public void drive(EnhancedDriverStation station) {
		String strRightString = String.format("Right Joystick: ", station.rightStickValue());
		if(station.rightStickValue() != 0) Logger.log(strRightString);
		leftDrive.setSpeed(station.leftStickValue());
		rightDrive.setSpeed(station.rightStickValue());
	}
	
	/**
	 * Set motor speeds to climb. Forces motors to turn in only one direction.
	 * @param station
	 */
	public void climb(EnhancedDriverStation station) {
		// climbSpeed: average of the abs values of each joystick
		double climbSpeed = (Math.abs(station.leftStickValue()) + Math.abs(station.rightStickValue()))/ 2.0;
		
		// Set speeds
		// TODO make sure motors turn in correct direction.
		leftDrive.setSpeed(climbSpeed);
		rightDrive.setSpeed(climbSpeed);
	}
}
