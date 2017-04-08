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
	private static final boolean ENABLED = false;
	
	public static void log(String msg) {
		if(ENABLED) {
			System.out.println(HEADER + msg);
		}
	}
	
	public static void title(String msg) {
		if(ENABLED) {
			String title = String.format("============== %s =============\n", msg);
			System.out.print(title);
		}
	}
}
