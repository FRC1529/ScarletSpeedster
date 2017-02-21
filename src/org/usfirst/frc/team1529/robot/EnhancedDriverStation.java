/**
 * 
 */
package org.usfirst.frc.team1529.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

/**
 * @author CyberCards
 *
 */
public class EnhancedDriverStation {
	// Instance Variables
	// DriverStation
	DriverStation driverStation = DriverStation.getInstance();
	
	/*
	 * Driver Controls
	 */
	Joystick leftStick;
	Joystick rightStick;
	private static double DEADBAND = .05;
	
	/*
	 * Operator Controls: 8 buttons
	 * 1.
	 * 2.
	 * 3.
	 * 4.
	 * 5.
	 * 6.
	 * 7.
	 * 8.
	 */
	
	public EnhancedDriverStation(int leftStickUSB, int rightStickUSB) {
		leftStick = new Joystick(leftStickUSB);
		rightStick = new Joystick(rightStickUSB);
	}
	
	public double leftStickValue() { return stickValue(leftStick); }
	
	public double rightStickValue() { return stickValue(rightStick); }
	
	private double stickValue(Joystick js) {
		double value = js.getY();
		Logger.log(String.format("Stick value: %f", value));
	
		if(value > DEADBAND || value < DEADBAND)
			return value;
		else
			return 0.0;
	}
	
	public boolean shiftToClimber() {
		// Both left and right stick's button #2 must be pressed
		return leftStick.getRawButton(2) && leftStick.getRawButton(2);
	}
}
