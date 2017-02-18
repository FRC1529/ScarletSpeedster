/**
 * 
 */
package org.usfirst.frc.team1529.robot;

/**
 * @author CyberCards
 *
 */
public class Logger {
	private static final String HEADER = "->";
	
	public static void log(String msg) {
		System.out.println(HEADER + msg);
	}
}
