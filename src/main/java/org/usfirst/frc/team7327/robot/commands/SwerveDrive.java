package org.usfirst.frc.team7327.robot.commands;


import org.usfirst.frc.team7327.robot.Robot;

import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class SwerveDrive extends Command {

	public SwerveDrive() {
		requires(Robot.drivetrain); 
	}

	int setting = 0; 
	public static XboxController Player1 = Robot.oi.Controller0; 
	public static XboxController Player2 = Robot.oi.Controller1; 
	protected void initialize() { 
		setting = 0; 
	}

	double degreesL, magnitudeL, degreesR, magnitudeR, setDegree =  0; 
	double throttle = .5; 


	public void robotInit(){
	}
	
	DigitalInput forwardLimitSwitch = new DigitalInput(0);
	DigitalInput reverseLimitSwitch = new DigitalInput(1);

    public void teleopPeriodic()
    {
		
    }


	int heightH0 = -1888; 
	int heightH1 = -1888;
	int heightH2 = -18921;
	int heightH3 = -32861; 
	
	int heightB0 = -2000; 
	int heightB1 = -11445;
	int heightB2 = -26029;
	int heightB3 = -37000; 
	public static boolean flag= true;
	public static boolean kill=false; 
	boolean Dpressed = false; 

	double throottle = 0; //2/12

	protected void execute(){
		
		System.out.println("output is: " + forwardLimitSwitch.get());
		
		// Intake 2/12
		if(Robot.oi.getRightBumperDown(Player2) == true) { 
			throottle = .85; 
		}
		else if(Robot.oi.getLeftBumperDown(Player2) == true) { 
			throottle = -.85;
		}
		else { throottle = 0; }

		Robot.drivetrain.setRawIntake(throottle);

		if(flag) { Robot.drivetrain.setRawElevator(throttle*(-Robot.oi.getLeftTrigger(Player2) + Robot.oi.getRightTrigger(Player2))); }

		if((Robot.oi.getBButton(Player2) || Robot.oi.getAButton(Player2) && flag)) {
			Robot.gotoPosition(heightH1);
			flag = false; 
		}
		else if(Robot.oi.getYButton(Player2)&& flag) {
			Robot.gotoPosition(heightH2);
			flag = false; 
		}
		else if(Robot.oi.getXButton(Player2) && flag) {
			Robot.gotoPosition(heightH3);
			flag = false; 
		}
	 
		if(Robot.oi.Dpad(Player2) >= 0) { Dpressed = true; }else{ Dpressed = false; }
		
		if(((Robot.oi.Dpad(Player2) >= 0 && Robot.oi.Dpad(Player2) < 45) || (Robot.oi.Dpad(Player2) >= 315 && Robot.oi.Dpad(Player2) < 360) )&& flag) { 
			Robot.gotoPosition(heightB2); flag = false; }
		else if((Robot.oi.Dpad(Player2) >= 45 && Robot.oi.Dpad(Player2) < 135)&&flag) { Robot.gotoPosition(heightB1);  flag = false; }
		else if((Robot.oi.Dpad(Player2) >= 135 && Robot.oi.Dpad(Player2) < 225)&&flag) { Robot.gotoPosition(heightB0); flag = false; }
		else if((Robot.oi.Dpad(Player2) >= 225 && Robot.oi.Dpad(Player2) < 315)&&flag) { Robot.gotoPosition(heightB3); flag = false; }

		if(Robot.oi.getBackButton(Player2)){ kill = true; }
		
		
		SmartDashboard.putNumber("NavAngle: ", Robot.NavAngle()); 
		
		degreesL = Math.toDegrees(Math.atan2(Robot.oi.getLeftStickY(Player1),  Robot.oi.getLeftStickX(Player1))) + 90;
		magnitudeL = Math.sqrt(Math.pow(Robot.oi.getLeftStickX(Player1), 2) + Math.pow(Robot.oi.getLeftStickY(Player1), 2));

		degreesR = Math.toDegrees(Math.atan2(Robot.oi.getRightStickY(Player1),  Robot.oi.getRightStickX(Player1))) + 90;
		magnitudeR = Math.sqrt(Math.pow(Robot.oi.getRightStickX(Player1), 2) + Math.pow(Robot.oi.getRightStickY(Player1), 2));
		
		NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
		NetworkTableEntry tx = table.getEntry("tx");
		NetworkTableEntry ty = table.getEntry("ty");
		NetworkTableEntry ta = table.getEntry("ta");

		//read values periodically
		double x = tx.getDouble(0.0);
		double y = ty.getDouble(0.0);
		double area = ta.getDouble(0.0);

		//post to smart dashboard periodically
		SmartDashboard.putNumber("LimelightX", x);
		SmartDashboard.putNumber("LimelightY", y);
		SmartDashboard.putNumber("LimelightArea", area);



		SmartDashboard.putNumber("NWab", Robot.drivetrain.getAbeNW()); 
		SmartDashboard.putNumber("NEab", Robot.drivetrain.getAbeNE()); 
		SmartDashboard.putNumber("SWab", Robot.drivetrain.getAbeSW()); 
		SmartDashboard.putNumber("SEab", Robot.drivetrain.getAbeSE()); 

		if(magnitudeL > .5) setDegree = 180-degreesL;
		
		if(Robot.oi.getStartButton(Player1)) Robot.nav.reset();

		Robot.drivetrain.setRawElevator(throttle*(Robot.oi.getLeftTrigger(Player2) - Robot.oi.getRightTrigger(Player2)));
		
		switch(setting) {
		case 0: //Precision Mode 
			Robot.drivetrain.setAllDegrees(setDegree+Robot.NavAngle());
			Robot.drivetrain.setAllSpeed(-Robot.oi.getRightTrigger(Player1)+Robot.oi.getLeftTrigger(Player1));
			if(magnitudeR > .5) { setting = 1; Robot.drivetrain.turning.setOn(true); }
			break; 
		case 1: 
			Robot.drivetrain.setEachDegree(225, 315, 135, 45);
			Robot.drivetrain.turning.setYaw(degreesR);
			if(magnitudeR <= .5) { setting = 0; Robot.drivetrain.turning.setOn(false); }
			break; 
		case 2: 
			break; 
		}

		
	}
	
	protected boolean isFinished() {

		return false;
	}

	protected void interrupted() {
		end();
	}
}