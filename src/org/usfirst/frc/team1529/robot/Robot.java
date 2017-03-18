package org.usfirst.frc.team1529.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * Team Name: 1529 CyberCards
 * Year: 2017 Season
 * Game: FIRST Steamworks
 * What do it do(Auto)? Pass baseline, put a gear on the peg, & look for more gears
 * What do it do(Teleop)? Collect gears, put gears on pegs, & climb
 * Special, cool stuff: Pixycam to find gears & pegs, Visioncam for drivers to see, LED's, Encoders, & Pneumatics
 */
public class Robot extends IterativeRobot {
	private boolean isCompetition = false;
	
	// USB Ports
	private int leftStickPort 	= 0;
	private int rightStickPort 	= 1;
	private int OPERATOR_PORT	= 2;
	
	// PWM Ports
	private int YELLOW_PWM 	= 3;
	private int ORANGE_PWM 	= 4;
	private int RED_PWM 	= 5;
	private int GREEN_PWM 	= 6;
	private int PURPLE_PWM 	= 7;
	private int GRAY_PWM 	= 8;
	private int WHITE_PWM	= 9;
	
	// DIO Ports
	// Encoders
	private int LEFT_A_DIO 	= 0;
	private int LEFT_B_DIO 	= 1;
	private int RIGHT_A_DIO = 2;
	private int RIGHT_B_DIO = 3;
	
	private boolean[] PRACTICE_BOT_MOTOR_DIRECTION 	= {false, true};
	private boolean[] COMPETITION_BOT_MOTOR_DIRECTION = {false, false};
	private boolean[] MOTOR_DIRECTION;
	
	private int[] leftDrivePorts   = {GRAY_PWM, PURPLE_PWM, GREEN_PWM, LEFT_A_DIO, LEFT_B_DIO};
	private int[] rightDrivePorts  = {RED_PWM, ORANGE_PWM, YELLOW_PWM, RIGHT_A_DIO, RIGHT_B_DIO};

	// CAN ID
//	private int PDP_CANID			= 0; // NOT USED
	private int PCM_CANID			= 0;
	private int gearArmTalonCANID 	= 0;
	
	// PCM Ports
	private int flap_out 	= 2;
	private int flap_in 	= 3;
	private int intakeMotor = WHITE_PWM;
	private int[] DRIVE_SOLENOID = {PCM_CANID, 0, 1};
	private int[] CLIMB_SOLENOID = {PCM_CANID, 4, 5};
	
	//I2C device addresses
//		private int pixycam1_address = 0x54;
		
	EnhancedDriverStation station;
	
	TankDriveSystem tankDrive;
	GearArm gearArm; 	// Gear Arm
	UsbCamera camera; 	// Vision Camera setup

	boolean drive_mode     = true; 	// If false, in climb mode
	int auto_mode_position = 0; 	// details in autoInit()
	int auto_mode_setting  = 0; 	// details in autoCenter()
	int auto_step;
//	private int encoder_count_per_inch = 440 / 24; // UNTESTED; NOT USED
//	private int length_of_robot_inches = 3 * 12; // UNTESTED; NOT USED
	
	
	// NOTE: Climber system is built into Tank Drive.
	
	/**
	 * This function is run when the robot is first started up and should be used for any initialization of code.
	 */
	@Override
	public void robotInit() {
		if(isCompetition) {
			MOTOR_DIRECTION = COMPETITION_BOT_MOTOR_DIRECTION;
		} else {
			MOTOR_DIRECTION = PRACTICE_BOT_MOTOR_DIRECTION;
		}
		
		Logger.log("Initializing the robot...");
		station 	= new EnhancedDriverStation(leftStickPort, rightStickPort, OPERATOR_PORT);
		tankDrive 	= new TankDriveSystem(this, MOTOR_DIRECTION, leftDrivePorts, rightDrivePorts, DRIVE_SOLENOID, CLIMB_SOLENOID);
		gearArm = new GearArm(gearArmTalonCANID, flap_out, flap_in,intakeMotor);
//		setupHDCamera(1920, 1080, 10);
	}
	
	/**
	 * Setup for HD camera for driver and operator.
	 * @param xRes: x-axis resolution
	 * @param yRes: y-axis resolution
	 * @param frameRate: picture refresh rate
	 */
	private void setupHDCamera(int xRes, int yRes, int frameRate) {
		Logger.log("Setting up HD Camera");
		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(xRes, yRes);
		camera.setFPS(frameRate);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		// TODO: need to implement chooser with drive.
		
		auto_step = 1;
		
		tankDrive.resetEncoders();
		Logger.log("-----------------Auto Init---------------");
		tankDrive.printEncoders();
		Logger.log("CHECK HERE");
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
//		gearArm.flapClose();
		gearArm.flapOff();
		String msg = String.format("************* Auto Periodic: step # %d", auto_step);
		Logger.log(msg);
		Logger.log("Something a);sdlkfja;ksldjf");
//		clearBaseline();
//		autoLeftPeg();
//		autoRightPeg();
		validateRotation();
//		validateBackward();
		
	}
	
	/**
	 * Clears the baseline or gear on center peg.
	 */
	private void clearBaseline() {
		Logger.log("Clearing baseline");
		switch(auto_step) {
		case 1: goToBaseline(); break;
		}
	}
	
	private void validateBackward() {
		Logger.log("Clearing baseline");
		switch(auto_step) {
		case 1: tankDrive.autoMoveTo(-750); break;
		}
	}
	
	private void validateRotation() {
		Logger.log("Validating rotation");
		switch(auto_step) {
		case 1: tankDrive.autoMoveTo(200, -200); break;
		}
	}
	
	private void goToBaseline() { autoMoveTo(750); }
	private void rotateForSide(boolean isClockwise) {
		int direction;
		if(isClockwise) {
			direction = 1;
		} else {
			direction = -1;
		}
		rotate(direction * 200);
	}
	
	private void autoLeftPeg() {
		Logger.log("Left Peg Auto");
		autoLeftRightPeg(true);
	}
	
	private void autoRightPeg() {
		Logger.log("Right Peg Auto");
		autoLeftRightPeg(false);
	}
	
	private void autoLeftRightPeg(boolean isLeft) {
		switch(auto_step) {
		case 1: autoMoveTo(200); break;
		case 2: rotateForSide(isLeft); break;
		case 3: autoMoveTo(200); break;
		}
	}
	
	private void autoMoveTo(int steps) { tankDrive.autoMoveTo(steps); }
	
	/**
	 * Auto rotate robot. Positive integer is clockwise orientation.
	 * 
	 * @param amount: 100 is about 30 degrees
	 */
	private void rotate(int amount) {
		tankDrive.autoMoveTo(amount, -amount);
	}

	
	@Override
	public void teleopInit() { tankDrive.resetEncoders(); }

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		if (drive_mode) { // can only shift to climb mode, cannot shift back at this time.
			teleopDrive(); // Can either do driving/setting gears
			shiftToClimb();
		} else {
			teleopClimb();
			shiftToDrive();
		}
	}
	
	
	/**
	 * Checks to see if should shift to climb mode, and do so if controller pushes correct buttons to shift to climb.
	 */
	private void shiftToClimb() {
		if(drive_mode && station.shiftToClimber()) {
			if (drive_mode)
				Logger.log("Shifting to Climb");
			tankDrive.shiftToClimb();
			drive_mode = false;
		}
	}
	
	private void shiftToDrive() {
		if(!drive_mode && station.shiftToDrive()) {
			tankDrive.shiftToDrive();
			drive_mode = true;
		}
	}

	
	/**
	 * Do the things that should be done when in Teleop and in drive mode.
	 */
	private void teleopDrive() {
		Logger.log("Teleop Drive");
		tankDrive.drive(station); // push implementation to tankDrive System
		teleopGearArm();
	}
	
	private void teleopGearArm() {
		Logger.log("Teleop gear arm control");
		gearArm.control(station);
	}
	
	/**
	 * Do the things that should be done when in Teleop and not in drive mode (aka climbing mode).
	 */
	private void teleopClimb() {
		Logger.log("Teleop Climb");
		tankDrive.climb(station); // push implementation to tankDrive System
	}
}