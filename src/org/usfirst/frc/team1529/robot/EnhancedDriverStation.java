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
	Joystick operator;
	
	private static double DEADBAND = .05;
	private static double STRAIGHT_DEADBAND = 0.1;
	private static double DOWNSHIFT_DEADBAND = 0.25;
	private static double UPSHIFT_DEADBAND = 0.8;
	
	private static int kShiftUpButton = 7;
	private static int kShiftDownButton = 8;
	
	/*
	 * Operator Controls: 8 buttons
	 * 1-4 Digital Joystick
	 * 5-10 Buttons
	 * 1. Right
	 * 2. Left
	 * 3. Up
	 * 4. Down
	 * 5. Orange Up
	 * 6. Orange Down
	 * 7. White Up
	 * 8. Black up
	 * 9. White Down
	 * 10. Black Down
	 */
	
	private int GEAR_ARM_UP   	= 4; // Operator controller
	private int GEAR_ARM_DOWN 	= 3; // Operator controller
	
	private int FLAP_OPEN 		= 6; // Operator controller
	private int FLAP_CLOSE		= 9;
	private int INTAKE			= 10; // Operator controller
	private int OUTTAKE			= 8;
//	private int FLAP_OFF		= 10; // Operator controller
	
	public EnhancedDriverStation(int leftStickUSB, int rightStickUSB, int operatorUSB) {
		leftStick 	= new Joystick(leftStickUSB);
		rightStick 	= new Joystick(rightStickUSB);
		operator 	= new Joystick(operatorUSB);
	}
	
	public boolean shiftUp() {
		return leftStick.getRawButton(kShiftUpButton);
	}
	
	public boolean shiftDown() {
		return leftStick.getRawButton(kShiftDownButton);
	}
	
	private boolean isStraight() { return Math.abs(stickValue(leftStick) - stickValue(rightStick)) < STRAIGHT_DEADBAND; }
	
	private double avgValue() { return (stickValue(leftStick) + stickValue(rightStick))/ 2.0; }
	
	public double leftStickValue() {
		if (isStraight())
			return avgValue();
		else
			return stickValue(leftStick);
		}
	
	public double rightStickValue() {
		if(isStraight())
			return avgValue();
		else
			return stickValue(rightStick);
		}
	
	private double stickValue(Joystick js) {
		double value = js.getY();
		Logger.log(String.format("Stick value: %f", value));
	
		if(value > DEADBAND || value < DEADBAND)
			return value;
		else
			return 0.0;
	}
	
	/**
	 * 
	 * @return true when the correct controls are pressed to shift to climbing
	 */
	public boolean shiftToClimber() { return leftStick.getRawButton(2) && rightStick.getRawButton(2); }
	
	public boolean shiftToDrive() { return leftStick.getRawButton(1) && rightStick.getRawButton(1); }
	
	public boolean isDownShiftBand() { return rightAbs() <= DOWNSHIFT_DEADBAND || leftAbs() <= DOWNSHIFT_DEADBAND; }
	
	public boolean isUpShiftBand() { return rightAbs() >= UPSHIFT_DEADBAND && leftAbs() >= UPSHIFT_DEADBAND; }
	
	public double rightAbs() { return stickAbs(rightStick); }
	
	public double leftAbs() { return stickAbs(leftStick); }
	
	private double stickAbs(Joystick js) { return Math.abs(stickValue(js)); }
	
	public boolean intakeStatus() {
		//TODO: add code when buttons ready.
		return operator.getRawButton(INTAKE);
	}
	
	public boolean flapStatus() {
		//TODO: add code when buttons are ready.
		return false;
	}
	
	public int gearArmMode() {
		if(operator.getRawButton(GEAR_ARM_UP))
			return 1;
		else if(operator.getRawButton(GEAR_ARM_DOWN))
			return -1;
		else
			return 0;
	}
	
	public boolean armUp() {
		if(operator.getRawButton(GEAR_ARM_UP))
			Logger.log("Arm UP*****************************");
		return operator.getRawButton(GEAR_ARM_UP);
	}
	
	public boolean armDown() {
		if(operator.getRawButton(GEAR_ARM_DOWN));
			Logger.log("ARM DOWN************#^*@&^*(#^(*@#*^&#(*&^@#*(&^(*#&^(*@#^(*@#^");
		return operator.getRawButton(GEAR_ARM_DOWN);
	}
	
	public boolean isIntake() {
		Logger.log(String.format("Button value: %s: ", operator.getRawButton(INTAKE)));
		return operator.getRawButton(INTAKE);
	}
	
	public boolean isOuttake() {
		return operator.getRawButton(OUTTAKE);
	}
	
	public boolean flapOpen() {
		return operator.getRawButton(FLAP_OPEN);
	}
	
	public boolean flapClose() {
		return operator.getRawButton(FLAP_CLOSE);
	}
	
//	public boolean flapOff() {
//		return operator.getRawButton(FLAP_OFF);
//	}
}
