/**
 * Drive System:
 * 1 side of our drive system
 * Victors(3)
 * Encoders(1)
 */
package org.usfirst.frc.team1529.robot;

import edu.wpi.first.wpilibj.VictorSP;

/**
 * @author CyberCards
 *
 */
public class DriveSystem {
	//Victors
	VictorSP victor1, victor2, victor3;
	double direction;
	
	//Constructors
	/**
	 * Sets up one side of our overall drive system.
	 * @param inverted: sets orientation of motors
	 * @param victorPWMPort1: PWM for VictorSP 1
	 * @param victorPWMPort2: PWM for VictorSP 2
	 * @param victorPWMPort3: PWM for VictorSP 3
	 */
	public DriveSystem(boolean inverted, int victorPWMPort1, int victorPWMPort2, int victorPWMPort3) {
		if(inverted) direction = -1.0;
		victor1 = new VictorSP(victorPWMPort1);
		victor2 = new VictorSP(victorPWMPort2);
		victor3 = new VictorSP(victorPWMPort3);
	}
	
	// setSpeed
	// Sets the speeds of each victor
	/**
	 * Sets speed for all Victor SPs
	 * @param speed: speed setting
	 */
	public void setSpeed(double speed) {
		speed = direction * speed;
		victor1.setSpeed(speed);
		victor2.setSpeed(speed);
		victor3.setSpeed(speed);
	}
}
