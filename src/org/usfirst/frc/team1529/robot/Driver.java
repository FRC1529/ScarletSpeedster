package org.usfirst.frc.team1529.robot;

/**
 * Driver specific info.
 * @author CyberCards
 *
 */
public class Driver {
	private double straight_band;
	Driver(double straightBand) {
		straight_band = straightBand;
	}
	
	public double getStraightBand() {
		return straight_band;
	}
}
