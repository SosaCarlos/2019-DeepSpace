package org.usfirst.frc.team7327.robot.commands;

import org.usfirst.frc.team7327.robot.Robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class SwerveDrive extends Command {

	public SwerveDrive() { requires(Robot.drivetrain); }

	
	public static XboxController P1 = Robot.oi.Controller0, P2 = Robot.oi.Controller1; 
	int DriveSetting, ElevSetting = 0; 
	protected void initialize() { DriveSetting = 0; ElevSetting = 0; 
		DoubleSolenoid.clearAllPCMStickyFaults(0); 
	}

	double degreesL, magnitudeL, degreesR, magnitudeR, degreesL2, magnitudeL2, magnitudeR2, setDegree =  0; 
	int heightB0 = 0, heightB1 = 11000, heightB2 = 26000, heightB3 = 37000, heightH2 = 17033, heightH3 = 30973; 
	//int heightB1 = 19893; 

	double throttle = .3, throottle = 0, ballThrottle = 0; 

	int supportMode = 0; 

	DoubleSolenoid.Value Flex = DoubleSolenoid.Value.kOff; 

	protected void execute(){
		
		NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
		NetworkTableEntry tx = table.getEntry("tx");
		NetworkTableEntry ty = table.getEntry("ty");
		NetworkTableEntry ta = table.getEntry("ta");
		double x = tx.getDouble(0.0);
		double y = ty.getDouble(0.0);
		double area = ta.getDouble(0.0);
		
		SmartDashboard.putNumber("NavAngle: ", Robot.NavAngle()); 
		SmartDashboard.putNumber("LimelightX", x);
		SmartDashboard.putNumber("LimelightY", y);
		SmartDashboard.putNumber("LimelightArea", area);
		SmartDashboard.putNumber("NWab", Robot.drivetrain.getAbeNW()); 
		SmartDashboard.putNumber("NEab", Robot.drivetrain.getAbeNE()); 
		SmartDashboard.putNumber("SWab", Robot.drivetrain.getAbeSW()); 
		SmartDashboard.putNumber("SEab", Robot.drivetrain.getAbeSE()); 

		if(Robot.oi.StartButton(P1)) { Robot.nav.reset(); }
		if(Robot.oi.StartButton(P2)) { Robot.drivetrain.ResetElevator(); }

		if(Robot.oi.BButton(P2)){ Flex = DoubleSolenoid.Value.kForward; } //Flex
		else if(Robot.oi.AButton(P2)){ Flex = DoubleSolenoid.Value.kReverse; } //Release
		else { Flex = DoubleSolenoid.Value.kOff; }
		Robot.drivetrain.setRawBicep(Flex); 
		
		if(Robot.oi.RightBumperDown(P2)) { throottle = .6; }
		else if(Robot.oi.RightBumperDown(P1)) {throottle = .6; }
		else if(Robot.oi.LeftBumperDown(P2)) { throottle = -.6;}
		else if(Robot.oi.LeftBumperDown(P1)) { throottle = -6;}
		else { throottle = 0; }
		Robot.drivetrain.setRawIntake(throottle);

		magnitudeR2 = Math.sqrt(Math.pow(Robot.oi.RightStickX(P2), 2) + Math.pow(Robot.oi.RightStickY(P2), 2));
		if(magnitudeR2 > .3) { ballThrottle = .75*-Robot.oi.RightStickY(P2); }
		else if(Robot.oi.RightBumperDown(P2)) { ballThrottle = -.5; }
		else if(Robot.oi.RightBumperDown(P1)) { ballThrottle = -.5; }
		else if(Robot.oi.LeftBumperDown(P1))  { ballThrottle =  .5; }
		else{ ballThrottle = 0; }
		Robot.drivetrain.setRawBallIn(ballThrottle); 
		
		if(Robot.oi.Dpad(P2) >= 0 || Robot.oi.Dpad(P1) >= 0 || Robot.oi.YButtonDown(P2) || Robot.oi.XButtonDown(P2)) { 
            if     (Robot.oi.DpadDown(P1) || Robot.oi.DpadDown(P2) )  { ElevSetting = 1; Robot.drivetrain.ElevOn(true); }
            else if(Robot.oi.DpadRight(P1)|| Robot.oi.DpadRight(P2))  { ElevSetting = 2; Robot.drivetrain.ElevOn(true); }
            else if(Robot.oi.DpadUp(P1)   || Robot.oi.DpadUp(P2)   )  { ElevSetting = 3; Robot.drivetrain.ElevOn(true); }
            else if(Robot.oi.DpadLeft(P1) || Robot.oi.DpadLeft(P2) )  { ElevSetting = 4; Robot.drivetrain.ElevOn(true); } 
            else if(Robot.oi.YButtonDown(P2)){ ElevSetting = 5; Robot.drivetrain.ElevOn(true); }
            else if(Robot.oi.XButtonDown(P2)){ ElevSetting = 6; Robot.drivetrain.ElevOn(true); }
        }   else{ ElevSetting = 0; Robot.drivetrain.ElevOn(false); }

		switch(ElevSetting) {
		case 0: 
			Robot.drivetrain.setRawElevator(throttle*(Robot.oi.LeftTrigger(P2) - Robot.oi.RightTrigger(P2)));
			break; 
		case 1: Robot.drivetrain.setElevatorPosition(heightB0); break; 
		case 2: Robot.drivetrain.setElevatorPosition(heightB1); break; 
		case 3: Robot.drivetrain.setElevatorPosition(heightB2); break;
		case 4: Robot.drivetrain.setElevatorPosition(heightB3); break; 
		case 6: Robot.drivetrain.setElevatorPosition(heightH2); break; 
		case 7: Robot.drivetrain.setElevatorPosition(heightH3); break; 
		}

		degreesL = Math.toDegrees(Math.atan2(Robot.oi.LeftStickY(P1),  Robot.oi.LeftStickX(P1))) + 90;
		magnitudeL = Math.sqrt(Math.pow(Robot.oi.LeftStickX(P1), 2) + Math.pow(Robot.oi.LeftStickY(P1), 2));
		degreesR = Math.toDegrees(Math.atan2(Robot.oi.RightStickY(P1),  Robot.oi.RightStickX(P1))) + 90;
		magnitudeR = Math.sqrt(Math.pow(Robot.oi.RightStickX(P1), 2) + Math.pow(Robot.oi.RightStickY(P1), 2));
		if(magnitudeL > .5) setDegree = 180-degreesL;
		
		degreesL2 = Math.toDegrees(Math.atan2(Robot.oi.LeftStickY(P2),  Robot.oi.LeftStickX(P2))) + 90;
        magnitudeL2 = Math.sqrt(Math.pow(Robot.oi.LeftStickX(P2), 2) + Math.pow(Robot.oi.LeftStickY(P2), 2));
        if(magnitudeL2 > .5){ supportMode = Robot.oi.RoundDegrees(degreesL2); }
		else{ supportMode = -1; }
		System.out.println(degreesL2); 

		switch(DriveSetting) {
		case 0: //Precision Mode 
			Robot.drivetrain.setAllDegrees(setDegree+Robot.NavAngle());
			Robot.drivetrain.setAllSpeed(-Robot.oi.RightTrigger(P1)+Robot.oi.LeftTrigger(P1));
			if(magnitudeR > .5) { DriveSetting = 1; Robot.drivetrain.turning.setOn(true); }
			if(Robot.oi.LSClick(P1)){ DriveSetting = 2; }
			if(Robot.oi.RSClick(P1)){ DriveSetting = 3; }
			break; 
		case 1: 
			Robot.drivetrain.setEachDegree(225, 315, 135, 45);
			if(Robot.oi.RoundDegrees(degreesR) == supportMode) { degreesR = supportMode; }
			Robot.drivetrain.turning.setYaw(degreesR);
			if(magnitudeR <= .5) { DriveSetting = 0; Robot.drivetrain.turning.setOn(false); }
			if(Robot.oi.LSClick(P1)){ DriveSetting = 2; Robot.drivetrain.turning.setOn(false); }
			if(Robot.oi.RSClick(P1)){ DriveSetting = 3; Robot.drivetrain.turning.setOn(false); }
			break; 
		case 2: 
			Robot.drivetrain.setAllDegrees(Robot.NavAngle());
			Robot.drivetrain.setAllSpeed(-Robot.oi.RightTrigger(P1)+Robot.oi.LeftTrigger(P1));
			if(magnitudeL > .5) { DriveSetting = 0; }
			if(magnitudeR > .5) { DriveSetting = 1; Robot.drivetrain.turning.setOn(true); }
			if(Robot.oi.RSClick(P1)){ DriveSetting = 3; }
			break;
		case 3: 
			Robot.drivetrain.setAllDegrees(Robot.NavAngle());
			Robot.drivetrain.setAllSpeed(-Robot.oi.RightTrigger(P1)+Robot.oi.LeftTrigger(P1));
			if(magnitudeL > .5) { DriveSetting = 0; }
			if(magnitudeR > .5) { DriveSetting = 1; Robot.drivetrain.turning.setOn(true); }
			if(Robot.oi.LSClick(P1)){ DriveSetting = 2; }
			break;
		}
	}
	
	protected boolean isFinished() { return false; }
	protected void interrupted() { end(); }
}