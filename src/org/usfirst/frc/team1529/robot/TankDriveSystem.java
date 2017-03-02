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
	private DoubleSolenoid.Value SHIFT_UP 	= REVERSE;
	private DoubleSolenoid.Value SHIFT_DN 	= FORWARD;
	
	// TODO Encoder
	private boolean LEFT_ENCODER_REVERSE_DIRECTION = false;
	private boolean RIGHT_ENCODER_REVERSE_DIRECTION = true;
	private int ENCODER_BAND = 3;
	
	private Robot robot;
	
	/*
	 * TankDriveSystem constructor
	 */
	
	public TankDriveSystem(Robot theRobot, int[] leftPorts, int[] rightPorts, int[] driveSolenoid, int[] climbSolenoid) {
		robot = theRobot;
		
		leftDrive 	= new DriveSystem(false, leftPorts[0], leftPorts[1], leftPorts[2], leftPorts[3], leftPorts[4], LEFT_ENCODER_REVERSE_DIRECTION);
		rightDrive 	= new DriveSystem(true, rightPorts[0], rightPorts[1], rightPorts[2], rightPorts[3], rightPorts[4], RIGHT_ENCODER_REVERSE_DIRECTION);
		driveShifter = new DoubleSolenoid(driveSolenoid[0], driveSolenoid[1], driveSolenoid[2]);
		driveShifter.set(SHIFT_DN);
		
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
		printEncoders();
	}
	
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
	
	private void upShift() {
		upShift("Shifting Up");
	}
	
	private void upShift(String msg) {
		Logger.log(msg);
		driveShifter.set(SHIFT_UP);
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
	
	public void printEncoders() {
		Logger.log(String.format("Left Count: %d; Right Count: %d", leftDrive.encoder.get(), rightDrive.encoder.get()));
	}
	
	public void resetEncoders() {
		Logger.log("Encoders reset.");
		leftDrive.encoder.reset();
		rightDrive.encoder.reset();
	}
	
	public void autoMoveTo(int encoder_counts) {
		autoMoveTo(encoder_counts, encoder_counts);
	}
	
	public void autoMoveTo(int leftTarget, int rightTarget) {
		printEncoders();
		pSpeedSet(leftDrive, leftTarget);
		pSpeedSet(rightDrive, rightTarget);
		if(isTargetReached(leftTarget, rightTarget)) {
			Logger.log(String.format("Step %d was successfully achieved.", robot.auto_step));
			robot.auto_step++;
			leftDrive.encoder.reset();
			rightDrive.encoder.reset();
		}
	}
	
	private double pSpeedSet(DriveSystem drive, int target) {
		double kP = 0.15;
		int max_encoder_distance = 50;
		int encoder_distance = target - drive.encoder.get();
		if(encoder_distance > max_encoder_distance) {
			return 1.0;
		} else if(encoder_distance < -max_encoder_distance) {
			return -1.0;
		} else {
			return kP * (max_encoder_distance - encoder_distance) / max_encoder_distance;
		}
	}
	
//	private void encoderSetDrive(DriveSystem drive, int target) {
//		double speed = 0.25;
//		if(drive.encoder.get() < target - ENCODER_BAND) {
//			drive.setSpeed(-speed);
//		} else if (drive.encoder.get() > target + ENCODER_BAND) {
//			drive.setSpeed(speed);
//		} else {
//			drive.setSpeed(0.0);
//		}
//	}
	
	private boolean isTargetReached(int leftTarget, int rightTarget) {
		return isReached(leftDrive, leftTarget) && isReached(rightDrive, rightTarget);
	}
	
	private boolean isReached(DriveSystem drive, int target) {
		return drive.encoder.get() >= (target - ENCODER_BAND) && drive.encoder.get() <= (target + ENCODER_BAND);
	}
}
