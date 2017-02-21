package org.usfirst.frc.team1529.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
	/* Team Name: 1529 CyberCards
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
	private int[] leftDrivePorts   = {GRAY_PWM, PURPLE_PWM, GREEN_PWM};
	private int[] rightDrivePorts  = {RED_PWM, ORANGE_PWM, YELLOW_PWM};
	private int   intakeMotor; // TODO: initialize
	
	// DIO Ports
//	private int[] leftDriveEncoderPortAB 	= {0, 1};
//	private int[] rightDriveEncoderPortAB 	= {2, 3};
//	private int[] gearArmEncoderPortAB 		= {4, 5};

	// CAN ID
	private int PDP_CANID			= 0;
	private int PCM_CANID			= 1;
	private int gearArmTalonCANID 	= 2;
	
	// PCM Ports
	// TODO: figure this out: 0-7
	private int flap_out 	= 100;
	private int flap_in 	= 100;
	
	//I2C device addresses
//	private int pixycam1_address = 0x54;
	
	EnhancedDriverStation station;
	
	TankDriveSystem tankDrive;
	GearArm gearArm; 	// Gear Arm
	UsbCamera camera; 	// Vision Camera setup

	boolean drive_mode     = true; 	// If false, in climb mode
	int auto_mode_position = 0; 	// details in autoInit()
	int auto_mode_setting  = 0; 	// details in autoCenter()
	
	// NOTE: Climber system is built into Tank Drive.
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization of code.
	 */
	@Override
	public void robotInit() {
		Logger.log("Initializing the robot...");
		station 	= new EnhancedDriverStation(leftStickPort, rightStickPort);
		tankDrive 	= new TankDriveSystem(leftDrivePorts, rightDrivePorts);
		/* GearArm: Picks up and places gears.
		 * Inputs:
		 * 1. talonCANID: 		0 // some setup will probablly be needed
		 * 2. flapPCMID1: 		4 // See Pneumatic Control Module (PCM)
		 * 3. flapPCMID2: 		5 // See PCM
		 * 4. intakeMotorPWM: 	0 // See RoboRio PWM ports
		 */
//		gearArm = new GearArm(gearArmTalonCANID, flap_out, flap_in, intakeMotor);
//		setupHDCamera(1920, 1080, 10);
		
		//PixyCam set up
//		I2CPixyPort = new I2C(I2C.Port.kOnboard, Pixy1DeviceAddress);
//		Pixy1ReadByteBuffer = new ByteBuffer;
//		Pixy1ReadByteBuffer.allocate(14);
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
		
		switch(auto_mode_position) {
			case 0: autoCenter(); break;
			case 1: autoLeft(); break;
			case 2: autoRight(); break;
		}
	}
	
	private void autoCenter() {
		/* auto_mode_setting
		 * 0: Peg on gear only
		 * 1: 2 Gear To Left
		 * 2: 2 Gear To Right
		 */
		pegCenter();
		switch(auto_mode_setting) {
		case 1: findGear(true); break;
		case 2: findGear(false); break;
		}
	}
	
	private void pegCenter() {
		// TODO implement; should start in center position and place peg on center peg
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
			// TODO actually engage pneumatic.
			drive_mode = false;
		}

		if (drive_mode)
			teleopDrive(); // Can either do driving/setting gears
		else
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

