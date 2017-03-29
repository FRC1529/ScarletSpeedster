package org.usfirst.frc.team1529.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.VictorSP;

public class TankDriveSystem {
	/*
	 * Instance Variables
	 */
	
	/* Left and Right Drive
	 * inputs: PWM ports for 3 victors per side
	 */
	DriveSystem leftDrive, rightDrive;
	
	/* Pneumatic Shifter
	 * 2 params: kForward port, kReverse port
	 * 3 params: moduleID, kForward port, kReverse port
	 */
	DoubleSolenoid driveShifter; // module 0, kForward, kReverse; kForward should be first gear
	DoubleSolenoid climbShifter;
	private DoubleSolenoid.Value FORWARD 	= DoubleSolenoid.Value.kForward;
	private DoubleSolenoid.Value REVERSE 	= DoubleSolenoid.Value.kReverse;
//	private DoubleSolenoid.Value OFF 		= DoubleSolenoid.Value.kOff; // NOT USED
	private DoubleSolenoid.Value SHIFT_UP 	= FORWARD;
	private DoubleSolenoid.Value SHIFT_DN 	= REVERSE;
	private DoubleSolenoid.Value TOCLIMB	= FORWARD;
	private DoubleSolenoid.Value TODRIVE	= REVERSE;
	
	private boolean LEFT_ENCODER_REVERSE_DIRECTION = false;
	private boolean RIGHT_ENCODER_REVERSE_DIRECTION = true;
	private int ENCODER_BAND = 3;
	
	private Robot robot;
	
	/*
	 * TankDriveSystem constructor
	 */
	
	public TankDriveSystem(Robot theRobot, int[] leftPorts, int[] rightPorts, int[] driveSolenoid, int[] climbSolenoid) {
		this(theRobot, new boolean[] {false, false}, leftPorts, rightPorts, driveSolenoid, climbSolenoid);
	}
	
	public TankDriveSystem(Robot theRobot, boolean[] motorDirection, int[] leftPorts, int[] rightPorts, int[] driveSolenoid, int[] climbSolenoid) {
		robot = theRobot;
		
		leftDrive 	= new DriveSystem(motorDirection[0], leftPorts[0], leftPorts[1], leftPorts[2], leftPorts[3], leftPorts[4], LEFT_ENCODER_REVERSE_DIRECTION);
		rightDrive 	= new DriveSystem(motorDirection[1], rightPorts[0], rightPorts[1], rightPorts[2], rightPorts[3], rightPorts[4], RIGHT_ENCODER_REVERSE_DIRECTION);
		driveShifter = new DoubleSolenoid(driveSolenoid[0], driveSolenoid[1], driveSolenoid[2]);
		driveShifter.set(SHIFT_DN);
		
		climbShifter = new DoubleSolenoid(climbSolenoid[0], climbSolenoid[1], climbSolenoid[2]);
		climbShifter.set(TODRIVE);
//		resetTestingVariables();
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
		printEncoders();
//		testDriveSystems(station);
	}
	
//	private void testDriveSystems(EnhancedDriverStation station) {
//		if(station.isRunTest()) {
//			if(!isTesting) {
//				isTesting = true;
//			}
//			runDriveSystemTest(station);
//		} else if(isTesting) {
//			resetTestingVariables();
//		}
//	}
	
//	private void resetTestingVariables() {
//		testStep = 0;
//		isTesting = false;
//		testTime = 0;
//	}
	
//	private void runDriveSystemTest(EnhancedDriverStation station) {
//		Logger.log("Running Drive Test!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//		switch(testStep) {
//		case 0: testStep++; break;
//		case 1: testMotor(leftDrive.victor1); break;
//		}
//	}
	
//	private void testMotor(VictorSP motor) {
//		if(testTime == 0) {
//			motor.setSpeed(1.0);
//		} else if(testTime >= TEST_LENGTH) {
//			motor.setSpeed(0.0);
//			testStep++;
//			return;
//		}
//		
//		testTime++;
//	}
	
	private void setSpeed(EnhancedDriverStation station) {
		leftDrive.setSpeed(station.leftStickValue());
		rightDrive.setSpeed(station.rightStickValue());
	}
	
	private void checkShifters(EnhancedDriverStation station) {
		if(station.shiftUp()) upShift();
		if(station.shiftDown()) downShift();
		autoShift(station);
	}
	
	private void autoShift(EnhancedDriverStation station) {
		autoDownShift(station);
		autoUpShift(station);
	}
	
	private void autoUpShift(EnhancedDriverStation station) {
		if(isDownShifted(driveShifter) && station.isUpShiftBand()) upShift("Auto up shift!!!!!");
	}
	
	private void autoDownShift(EnhancedDriverStation station) {
		if(isUpShifted(driveShifter) && station.isDownShiftBand()) downShift("Auto down shift!!!!!!!");
	}
	
	private boolean isUpShifted(DoubleSolenoid shifter) { return shifter.get() == SHIFT_UP; }
	private boolean isDownShifted(DoubleSolenoid shifter) { return shifter.get() == SHIFT_DN; }

	private void downShift() { downShift("Down shifting..."); }
	
	private void downShift(String msg) {
		Logger.log(msg);
		driveShifter.set(SHIFT_DN);
	}
	
	private void upShift() { upShift("Shifting Up"); }
	
	private void upShift(String msg) {
		Logger.log(msg);
		driveShifter.set(SHIFT_UP);
	}
	
	/**
	 * Shifts from drive train to climb train.
	 * 
	 */
	public void shiftToClimb() {
		Logger.log("Shifted to climb!");
		climbShifter.set(TOCLIMB);
	}
	
	/**
	 * Shifts out of climb to drive train.
	 */
	public void shiftToDrive() { climbShifter.set(TODRIVE); }
	
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
	
	/**
	 * Sets the climb speed based on driver station input.
	 * @param station
	 * @return
	 */
	private double climbSpeed(EnhancedDriverStation station) {
		double direction = -1.0;
		double left = station.leftStickValue();
		double right = station.rightStickValue();
		return direction * average(left, right);
	}
	
	/**
	 * Average of two numbers.
	 * @param num1
	 * @param num2
	 * @return
	 */
	private double average(double num1, double num2) { return (num1 + num2) / 2.0; }
	
	public void printEncoders() {
		Logger.log(String.format("Left Count: %d; Right Count: %d", leftDrive.encoder.get(), rightDrive.encoder.get()));
	}
	
	public void resetEncoders() {
		Logger.log("Encoders reset.");
		leftDrive.encoder.reset();
		rightDrive.encoder.reset();
	}
	
	public void autoMoveTo(int encoder_counts) { autoMoveTo(encoder_counts, encoder_counts); }
	
	public void autoMoveTo(int leftTarget, int rightTarget) {
		String msg = String.format("********* Auto Moving to: left: %d; right: %d", leftTarget, rightTarget);
		Logger.log(msg);
		printEncoders();
		encoderSetDriveAdvanced(leftDrive, leftTarget, "Left");
		encoderSetDriveAdvanced(rightDrive, rightTarget, "Right");
		if(isTargetReached(leftTarget, rightTarget)) {
			Logger.log(String.format("Step %d was successfully achieved.", robot.auto_step));
			robot.auto_step++;
			resetEncoders();
		}
	}
	
	/**
	 * Untested code.
	 * @param drive
	 * @param target
	 */
//	private double pSpeedSet(DriveSystem drive, int target) {
//		double kP = 0.15;
//		int max_encoder_distance = 50;
//		int encoder_distance = target - drive.encoder.get();
//		if(encoder_distance > max_encoder_distance) {
//			return 1.0;
//		} else if(encoder_distance < -max_encoder_distance) {
//			return -1.0;
//		} else {
//			return kP * (max_encoder_distance - encoder_distance) / max_encoder_distance;
//		}
//	}
	
	private void encoderSetDrive(DriveSystem drive, int target, String driveName) {
		double direction = -1.0;
		double speed = direction * 0.3;
		
		if(drive.encoder.get() < target - ENCODER_BAND) {
			// do nothing
		} else if (drive.encoder.get() > target + ENCODER_BAND) {
			speed = -speed;
		} else {
			speed = 0.0;
		}
		
//		String msg = String.format("Encoder value: %d; target: %d; Encoder Band: %d; Speed: %d", drive.encoder.get(), target, ENCODER_BAND, speed);
		Logger.log(String.format("-----------------%s------------", driveName));
		String enc = String.format("Encoder Value: %d", drive.encoder.get());
		String tar = String.format("Target: %d", target);
		String band = String.format("Encoder Band: %d", ENCODER_BAND);
		String spd = String.format("Speed: %f", speed);
		Logger.log(enc);
		Logger.log(tar);
		Logger.log(band);
		Logger.log(spd);
		
		drive.setSpeed(speed);
	}
	
	private void logAutoStatus(String name, DriveSystem drive, int target, int error, double speed) {
		Logger.log(String.format("-----------------%s------------", name));
		String enc = String.format("Encoder Value: %d", drive.encoder.get());
		String tar = String.format("Target: %d", target);
		String band = String.format("Error: %d", error);
		String spd = String.format("Speed: %f", speed);
		Logger.log(enc);
		Logger.log(tar);
		Logger.log(band);
		Logger.log(spd);
	}
	
	private void encoderSetDriveAdvanced(DriveSystem drive, int target, String driveName) {
		Logger.log("Advanced Encoder Setting");
		int error = target - drive.encoder.get();
		double direction = -1.0;
		int max_error = 1000;
		
		double max_speed;
		if(error > max_error) {
			max_speed = 0.5;
		} else {
			max_speed = 0.25;
		}
		
		double min_speed;
		if(Math.abs(error) > 100) {
			min_speed = 0.3;
		} else {
			min_speed = 0.15;
		}
		
		int error_tolerance = 50;
		
		double speed;
		
		if(Math.abs(error) > max_error) {
			Logger.log("Above max speed");
			if(error > 0) {
				speed = direction * max_speed;
			} else {
				speed = -1.0 * direction * max_speed;
			}
		} else if(Math.abs(error) <= error_tolerance) {
			Logger.log("Within tolerance");
			speed = 0.0;
		} else {
			Logger.log("Somewhere in between tolerance and max speed.");
			speed = direction * speedSettingAlternate(max_speed, max_error, error, min_speed, error_tolerance);
		}
		
		logAutoStatus(driveName, drive, target, error, speed);
		
		drive.setSpeed(speed);
	}
	
	private double speedSetting(double maxSpeed, int maxError, int error, double power) {
		double val = (double) maxError;
		String msg = String.format("Max speed: %f\nMax Error: %d\nError: %d\nTo the power of: %f", maxSpeed, maxError, error, power);
		Logger.log(msg);
		double speedToSet = maxSpeed / (Math.pow(val, power)) * ((double) error);
		return speedToSet;
	}
	
	private double speedSettingAlternate(double maxSpeed, int maxError, int error, double minSpeed, int tolerance) {
		double t = (double) tolerance;
		double maxErr = (double) maxError;
		double slope = (minSpeed - maxSpeed) / (t - maxErr);
		double intercept = minSpeed - slope * t;
		double speed = line(slope, intercept, Math.abs((double) error));
		
		String msg = String.format("Slope: %f\nIntercept: %f\n Speed: %f", slope, intercept, speed);
		Logger.log(msg);
		if(error < 0) { speed = -speed; }
		return speed;
	}
	
	private double line(double slope, double intercept, double x) {
		return slope * x + intercept;
	}
	
	private boolean isTargetReached(int leftTarget, int rightTarget) {
		boolean value = isReached(leftDrive, leftTarget) && isReached(rightDrive, rightTarget);
		String msg = String.format("Is Target reached? %s", value);
		Logger.log(msg);
		return value;
	}
	
	private boolean isReached(DriveSystem drive, int target) {
		return drive.encoder.get() >= (target - ENCODER_BAND) && drive.encoder.get() <= (target + ENCODER_BAND);
	}
}
