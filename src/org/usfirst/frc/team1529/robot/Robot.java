package org.usfirst.frc.team1529.robot;

import java.util.Date;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Team Name: 1529 CyberCards
 * Year: 2017 Season
 * Game: FIRST Steamworks
 * What do it do(Auto)? Pass baseline, put a gear on the peg, & look for more gears
 * What do it do(Teleop)? Collect gears, put gears on pegs, & climb
 * Special, cool stuff: Pixycam to find gears & pegs, Visioncam for drivers to see, LED's, Encoders, & Pneumatics
 */
public class Robot extends IterativeRobot {
	private boolean isCompetition = true;
	
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
	/*Practice bot setup**********
	 * 0/1 => drive solenoid
	 * 2/3 => flap
	 * 4/5 => climb solenoid
	 */
	private int flap_out 	= 4;
	private int flap_in 	= 5;
	private int intakeMotor = WHITE_PWM;
	private int[] DRIVE_SOLENOID = {PCM_CANID, 0, 1};
	private int[] CLIMB_SOLENOID = {PCM_CANID, 2, 3};
		
	EnhancedDriverStation station;
	
	TankDriveSystem tankDrive;
	GearArm gearArm; 	// Gear Arm
	UsbCamera camera; 	// Vision Camera setup
	SendableChooser<String> autoChooser;
	Sendable timeWarning;

	boolean drive_mode     = true; 	// If false, in climb mode
	int auto_mode_position = 0; 	// details in autoInit()
	int auto_mode_setting  = 0; 	// details in autoCenter()
	int auto_step;
	int auto_dummy_counter;
	String auto_choice;
	long auto_counter;
	long delay_counter;
	
	
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
		
		drive_mode = true;
		
		Logger.log("Initializing the robot...");
		station 	= new EnhancedDriverStation(leftStickPort, rightStickPort, OPERATOR_PORT);
		tankDrive 	= new TankDriveSystem(this, MOTOR_DIRECTION, leftDrivePorts, rightDrivePorts, DRIVE_SOLENOID, CLIMB_SOLENOID);
		gearArm 	= new GearArm(gearArmTalonCANID, flap_out, flap_in,intakeMotor);
		
		setupAutoChooser();
		initializeTimer();
//		setupHDCamera(96, 54, 60);
//		sendWarning();
	}
	
	private String getMatchTime() {
		return String.format("Match time: %f", Timer.getMatchTime());
	}
	
	private void initializeTimer() {
		auto_counter = 0;
	}
	
//	private void setupChoosers() {
//		setupAutoChooser();
////		setupDriverChooser(); // TODO: select driver
////		setupRobotChooser();  // Competition or practice bot.
//	}
	
	private void setupAutoChooser() {
		System.out.println("Setting up chooser");
		autoChooser = new SendableChooser<String>();
//		autoChooser.addDefault("Clear Baseline", "baseline");
//		autoChooser.addObject("Center of Airship", "center");
//		autoChooser.addObject("Left of Airship", "left");
//		autoChooser.addObject("Right of Airship", "right");
		autoChooser.addDefault("Dummy Straight", "dummy");
		autoChooser.addObject("Streak Upfield", "streak_upfield");
		autoChooser.addObject("Test Dummy Center Peg", "test_center");
		SmartDashboard.putData("Autonomous:", autoChooser);
	}
	
	/**
	 * Setup for HD camera for driver and operator.
	 * @param xRes: x-axis resolution
	 * @param yRes: y-axis resolution
	 * @param frameRate: picture refresh rate
	 */
	private void setupHDCamera(int xRes, int yRes, int frameRate) {
		Logger.log("Setting up HD Camera");
		camera = CameraServer.getInstance().startAutomaticCapture(0);
		camera.setResolution(xRes, yRes);
		camera.setFPS(frameRate);
	}


	@Override
	public void autonomousInit() {
		initializeAutoVariables();
		
		tankDrive.resetEncoders();
		Logger.log("-----------------Auto Init---------------");
		tankDrive.printEncoders();
	}
	
	private void initializeAutoVariables() {
		Logger.log("Initializing Auto Variables");
		auto_choice = autoChooser.getSelected().toString();
		auto_step = 1;
		auto_dummy_counter = 0;
		delay_counter = 0;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		gearArm.flapOff();
		auto_counter++;
//		String msg = String.format("************* Auto Periodic: step # %d", auto_step);
//		Logger.log(msg);
		
		runChoice();
	}
	
	private void runChoice() {
//		String msg = String.format("AutoChoice: %s", auto_choice);
//		Logger.log(msg);
		switch(auto_choice) {
		case "baseline": clearBaseline(); break;
		case "left": autoLeftPeg(); break;
		case "right": autoRightPeg(); break;
		case "dummy": autoDummy(); break;
		case "center": autoCenter(); break;
		case "streak_upfield": autoStreakUpfield(); break;
		case "test_center": autoTestCenter(); break;
		}
	}
	
	private void autoTestCenter() {
		switch(auto_step) {
		case 1: autoDummy(93); break;
		case 2: delayRobot(10); break;
		case 3: releaseFlap(); break;
		}
	}
	
	private void delayRobot(long time) {
		if(delay_counter < time) {
			delay_counter++;
		} else {
			auto_step++;
		}
	}
	
	private void releaseFlap() {
		gearArm.openFlap();
	}
	
	private void autoStreakUpfield() {
		switch(auto_step) {
		case 1: waitThenStreakUpfield(); break;
		}
	}
	
	private void waitThenStreakUpfield() {
		double direction = -1.0;
//		double delta = Timer.getMatchTime();
		double delta = auto_counter / 50.0;
		String msg = String.format("Going Upfield: Match Time:: %f", delta);
		Logger.log(msg);
		double start = 13.0;
		if(delta < 2.0) {
			tankDrive.setSpeed(direction * 0.4);
		} else if(delta < start) {
			tankDrive.setSpeed(0.0);
		}else if(delta < 14.0) {
			tankDrive.setSpeed(direction * 0.35);
		} else if(delta < 14.5) {
			Logger.log("B");
			tankDrive.setSpeed(direction * 0.7);
		} else if(delta < 15.0) {
			Logger.log("C");
			tankDrive.setSpeed(direction * 1.0);
		} else {
			Logger.log("STOP");
			tankDrive.setSpeed(0.0);
			tankDrive.shiftToClimb();
			drive_mode = false;
			auto_step++;
		}
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
	
	private void autoCenter() {
		switch(auto_step) {
		case 1: tankDrive.autoMoveStraightTo(760);
//		case 2: autoOpenFlap();
//		case 3: autoLowerFlap();
		}
	}
	
	private void autoOpenFlap() {
		auto_step++;
	}
	
	private void autoLowerFlap() {
		
		auto_step++;
	}
	
	private void autoDummy(int stopper) {
		Logger.log("Trying to Run Dummy");
		String msg = String.format("Dummy Counter: %d", auto_dummy_counter);
		Logger.log(msg);
		if(auto_dummy_counter < stopper){
			Logger.log("Dummy Auto Periodic");
			
			tankDrive.leftDrive.setSpeed(-0.3);
			tankDrive.rightDrive.setSpeed(-0.27);
			
		} else if(auto_dummy_counter > stopper) {
			tankDrive.leftDrive.setSpeed(0.0);
			tankDrive.rightDrive.setSpeed(0.0);
			auto_step++;
		}
		
		auto_dummy_counter++;
	}
	
	private void autoDummy(){
		int stopper = 100;
		Logger.log("Trying to Run Dummy");
		String msg = String.format("Dummy Counter: %d", auto_dummy_counter);
		Logger.log(msg);
		if(auto_dummy_counter < stopper){
			Logger.log("Dummy Auto Periodic");
			
			tankDrive.leftDrive.setSpeed(-0.3);
			tankDrive.rightDrive.setSpeed(-0.3);
			
		} else if(auto_dummy_counter > stopper) {
			tankDrive.leftDrive.setSpeed(0.0);
			tankDrive.rightDrive.setSpeed(0.0);
			auto_step++;
		}
		
		auto_dummy_counter++;
	}
	
	private void goToBaseline() { autoMoveTo(750, 775); }
	
	private void autoLeftPeg() {
		Logger.log("Left Peg Auto");
		autoLeftRightPeg(true);
	}
	
	private void autoRightPeg() {
		Logger.log("Right Peg Auto");
		autoLeftRightPeg(false);
	}
	
	private void autoLeftRightPeg(boolean isLeft) {
		int outer = 1107;
		int inner = 925;
		int left, right;
		if(isLeft) {
			left = outer;
			right = inner;
		} else {
			left = inner;
			right = outer;
		}
		switch(auto_step) {
		case 1: autoMoveTo(left, right); break;
		case 2: autoMoveTo(300); break;
		}
	}
	
	private void autoMoveTo(int steps) { tankDrive.autoMoveTo(steps); }
	private void autoMoveTo(int leftSteps, int rightSteps) { tankDrive.autoMoveTo(leftSteps, rightSteps); }
	
	@Override
	public void teleopInit() { 
		tankDrive.resetEncoders();
	}

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
		
//		timeWarning();
//		sendWarning();
		
		Logger.log(tankDrive.encoderToStr());
	}
	
	private void timeWarning() {
		double warning = 60.0 * 3.0 - 20.0;
		if(Timer.getMatchTime() > warning){
			sendWarning();
		}
	}
	
	private void sendWarning() {
		SmartDashboard.putString("matchTime", getMatchTime());
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
//		Logger.log("Teleop Drive");
		tankDrive.drive(station); // push implementation to tankDrive System
		teleopGearArm();
	}
	
	private void teleopGearArm() {
//		Logger.log("Teleop gear arm control");
		gearArm.control(station);
	}
	
	/**
	 * Do the things that should be done when in Teleop and not in drive mode (aka climbing mode).
	 */
	private void teleopClimb() {
//		Logger.log("Teleop Climb");
		tankDrive.climb(station); // push implementation to tankDrive System
	}
}