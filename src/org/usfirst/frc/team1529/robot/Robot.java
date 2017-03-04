package org.usfirst.frc.team1529.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
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
	// USB Ports
	private int leftStickPort 	= 0;
	private int rightStickPort 	= 1;
	
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

	private int[] leftDrivePorts   = {GRAY_PWM, PURPLE_PWM, GREEN_PWM, LEFT_A_DIO, LEFT_B_DIO};
	private int[] rightDrivePorts  = {RED_PWM, ORANGE_PWM, YELLOW_PWM, RIGHT_A_DIO, RIGHT_B_DIO};

	// CAN ID
	private int PDP_CANID			= 0;
	private int PCM_CANID			= 0;
	private int gearArmTalonCANID 	= 0;
	
	// PCM Ports
	// TODO: figure this out: 0-7
	private int flap_out 	= 100;
	private int flap_in 	= 100;
	private int[] DRIVE_SOLENOID = {PCM_CANID, 0, 1};
	private int[] CLIMB_SOLENOID = {PCM_CANID, 2, 3};
	
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
	private int encoder_count_per_inch = 440 / 24;
	private int length_of_robot_inches = 3 * 12;
	
	
	// NOTE: Climber system is built into Tank Drive.
	
	/**
	 * This function is run when the robot is first started up and should be used for any initialization of code.
	 */
	@Override
	public void robotInit() {
		Logger.log("Initializing the robot...");
		station 	= new EnhancedDriverStation(leftStickPort, rightStickPort);
		tankDrive 	= new TankDriveSystem(this, leftDrivePorts, rightDrivePorts, DRIVE_SOLENOID, CLIMB_SOLENOID);
		/* GearArm: Picks up and places gears.
		 * Inputs:
		 * 1. talonCANID: 		0 // some setup will probablly be needed
		 * 2. flapPCMID1: 		4 // See Pneumatic Control Module (PCM)
		 * 3. flapPCMID2: 		5 // See PCM
		 * 4. intakeMotorPWM: 	0 // See RoboRio PWM ports
		 */
//			gearArm = new GearArm(gearArmTalonCANID, flap_out, flap_in, intakeMotor);
		
//		setupHDCamera(1920, 1080, 10);
		
		//PixyCam set up

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
		/**
		 * Various options:
		 * -Center position, cross baseline
		 * -Center position
		 * -Center position; pick up gear left
		 * -Center position; pick up gear right
		 * -Left position (1 gear on peg only)
		 * -Right position (1 gear on peg only)
		 */
		auto_mode_position = 0; // TODO implement chooser
		auto_mode_setting = 0; 	// TODO implement chooser
		
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
		/* auto_mode_position: center, left of airship, right of airship
		 * 0: Center
		 * 1: Left
		 * 2: Right
		 * else: do nothing
		 */
		
//			switch(auto_mode_position) {
//				case 0: autoCenter(); break;
//				case 1: autoLeft(); break;
//				case 2: autoRight(); break;
//			}
		Logger.log("Auto Periodic");
		clearBaseline();
	}
	
	private void clearBaseline() {
		Logger.log("Clearing baseline");
		
		int inches_to_baseline_from_wall = 7 * 12 + 10; // 7 ft 9.25 inches
		switch(auto_step) {
		case 1: tankDrive.autoMoveTo(encoder_distance_to_location_from_wall(inches_to_baseline_from_wall)); break;
		}
	}
	
	private void firstGearOnPeg() {
		Logger.log("Placing gear");
		int inches_to_peg_from_wall = 9*12 + 10; // 9 ft 10 inches;
		switch(auto_step) {
		case 1: tankDrive.autoMoveTo(encoder_distance_to_location_from_wall(inches_to_peg_from_wall)); break;
		}
	}
	
	private int encoder_distance_to_location_from_wall(int inches) {
		return (inches - length_of_robot_inches) * encoder_count_per_inch;
	}
	
	private void autoCenter() {
		/* auto_mode_setting
		 * 0: Peg on gear only
		 * 1: 2 Gear To Left
		 * 2: 2 Gear To Right
		 */
	}
	
	private void findGear(boolean toLeft) {
		// TODO implement; after pegCenter search for Gear
	}
	
	private void autoLeft() {
		// TODO something when in position to left of airship;
	}
	
	private void autoRight() {
		// TODO something when in position to right of airship;
	}
	
	@Override
	public void teleopInit() {
		tankDrive.resetEncoders();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		// Check button to set drive_mode
		// Can only shift to climbing mode; cannot shift back
		// check button to see if should be in climb-mode
		if(station.shiftToClimber()) {
			if (drive_mode)
				Logger.log("Shifting to Climb");
			// TODO actually engage pneumatic to shift to climb mode.
			drive_mode = false;
		}

		if (drive_mode) {
			teleopDrive(); // Can either do driving/setting gears
			teleopGearArm(); // Control the gear arm and intake system
		} else
			teleopClimb(); // Or climb
	}
	
	/**
	 * Do the things that should be done when in Teleop and in drive mode.
	 */
	private void teleopDrive() {
		Logger.log("Teleop Drive");
		tankDrive.drive(station); // push implementation to tankDrive System
		// TODO implement gear arm controls here.
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

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}