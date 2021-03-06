/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team7327.robot.commands;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;

import org.usfirst.frc.team7327.robot.Robot;
import org.usfirst.frc.team7327.robot.Util.DriveCommand;
import org.usfirst.frc.team7327.robot.Util.ModuleLocation;

import static org.usfirst.frc.team7327.robot.Robot.kDrivetrain;
import static org.usfirst.frc.team7327.robot.Robot.oi;

public class Drive extends Command {
  
  public Drive() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.kDrivetrain);
  }

  // Called just before this Command runs the first time
  int DriveSetting, ElevSetting = 0; 
  @Override
  protected void initialize() { DriveSetting = 0; ElevSetting = 0;
    //DoubleSolenoid.clearAllPCMStickyFaults(0);  
  }

  public static XboxController P1 = Robot.oi.Controller0, P2 = Robot.oi.Controller1; 
  double Rotation = 0; 
  double testRotation; 
  double finalAngle = 0; 
  int rotAngBR = 135;   
  int rotAngBL = 45;   
  int rotAngFR = -135;   
  int rotAngFL = -45;    

  double degreesL, magnitudeL, degreesR, magnitudeR, degreesL2, magnitudeL2, magnitudeR2, setDegree =  0; 
	int heightB0 = 0, heightB1 = 11000, heightB2 = 26000, heightB3 = 37000, heightH2 = 17033, heightH3 = 30973; 
	//int heightB1 = 19893; 

  double throttle = .3, throottle = 0, ballThrottle = 0, throoottle = 1;
  boolean flag = false;
  
  DigitalInput forwardLimitSwitch = new DigitalInput(0);
	DigitalInput reverseLimitSwitch = new DigitalInput(1);


  
	//DoubleSolenoid.Value Flex = DoubleSolenoid.Value.kOff; 

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    double leftX = oi.getLeftXAxis();
    double leftY = oi.getLeftYAxis();
    double rightX = oi.getRightXAxis();
    double stickAngle = Math.toDegrees(Math.atan2(leftX, leftY));
    double leftMag = oi.getLeftMagnitude(); 
    if(leftMag < .3) { leftMag = 0; }
    if(leftMag > .3){ finalAngle = - stickAngle + Robot.NavAngle(); }
    double wheelXcos = Math.cos(finalAngle/57.2957795) * leftMag;
    double wheelYsin = Math.sin(finalAngle/57.2957795) * leftMag;

    SmartDashboard.putNumber("NAVANGLEGYRO: ", Robot.NavAngle());
    SmartDashboard.putNumber("LX: ", leftX); 
    SmartDashboard.putNumber("LY: ", leftY); 
    SmartDashboard.putNumber("stickAngle: ", stickAngle); 
    SmartDashboard.putNumber("RX: ", rightX); 
    SmartDashboard.putNumber("finalAngle: ", finalAngle); 
    System.out.println("output is: " + forwardLimitSwitch.get());
		System.out.println("output is : " + reverseLimitSwitch.get());


    double FLwheelX = wheelXcos + Math.cos(rotAngFL/57.2957795) * -rightX;
		double FLwheelY = wheelYsin + Math.sin(rotAngFL/57.2957795) * -rightX;
		double FLwheelRot = Math.atan2(FLwheelY, FLwheelX) * 57.2957795;
    double FLwheelMag = Math.hypot(FLwheelX, FLwheelY)*throoottle;
    
    double FRwheelX = wheelXcos + Math.cos(rotAngFR/57.2957795) * -rightX;
		double FRwheelY = wheelYsin + Math.sin(rotAngFR/57.2957795) * -rightX;
		double FRwheelRot = Math.atan2(FRwheelY, FRwheelX) * 57.2957795;
    double FRwheelMag = Math.hypot(FRwheelX, FRwheelY)*throoottle;
    
    double BLwheelX = wheelXcos + Math.cos(rotAngBL/57.2957795) * -rightX;
		double BLwheelY = wheelYsin + Math.sin(rotAngBL/57.2957795) * -rightX;
		double BLwheelRot = Math.atan2(BLwheelY, BLwheelX) * 57.2957795;
    double BLwheelMag = Math.hypot(BLwheelX, BLwheelY)*throoottle;
    
    double BRwheelX = wheelXcos + Math.cos(rotAngBR/57.2957795) * -rightX;
		double BRwheelY = wheelYsin + Math.sin(rotAngBR/57.2957795) * -rightX;
		double BRwheelRot = Math.atan2(BRwheelY, BRwheelX) * 57.2957795;
    double BRwheelMag = Math.hypot(BRwheelX, BRwheelY)*throoottle;

    if(Robot.oi.BackButton(P1) && !flag) { 
      flag = true;
      throoottle = 0.3;
    }

    if(Robot.oi.BackButton(P1) && flag) {
        flag = false;
        throoottle = 1;
      }
  
      
    double max = FLwheelMag;

    if(FRwheelMag > max)
      max = FRwheelMag;
    else if(BLwheelMag > max)
      max = BLwheelMag;
    else if(BRwheelMag > max)
      max = BRwheelMag;
    if(max > 1){
      FLwheelMag /= max;
      FRwheelMag /= max;
      BLwheelMag /= max;
      BRwheelMag /= max;
    }

    DriveCommand frontLeftCommand = new DriveCommand(FLwheelRot, FLwheelMag);
    DriveCommand frontRightCommand = new DriveCommand(FRwheelRot, FRwheelMag);
    DriveCommand backLeftCommand = new DriveCommand(BLwheelRot, BLwheelMag);
    DriveCommand backRightCommand = new DriveCommand(BRwheelRot, BRwheelMag);

    kDrivetrain.setModule(ModuleLocation.FRONT_LEFT, frontLeftCommand);
    kDrivetrain.setModule(ModuleLocation.FRONT_RIGHT, frontRightCommand);
    kDrivetrain.setModule(ModuleLocation.BACK_LEFT, backLeftCommand);
    kDrivetrain.setModule(ModuleLocation.BACK_RIGHT, backRightCommand);


    //7327 CODE BELOW
    SmartDashboard.putNumber("NavAngle: ", Robot.NavAngle()); 
    if(Robot.oi.StartButton(P1)) { Robot.nav.reset(); }

    
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
		SmartDashboard.putNumber("NWab", Robot.kDrivetrain.getAbeNW()); 
		SmartDashboard.putNumber("NEab", Robot.kDrivetrain.getAbeNE()); 
		SmartDashboard.putNumber("SWab", Robot.kDrivetrain.getAbeSW()); 
		SmartDashboard.putNumber("SEab", Robot.kDrivetrain.getAbeSE()); 

		if(Robot.oi.StartButton(P1)) { Robot.nav.reset(); }
		if(Robot.oi.StartButton(P2)) { Robot.kDrivetrain.ResetElevator(); }

		//if(Robot.oi.BButton(P2)){ Flex = DoubleSolenoid.Value.kForward; } //Flex
		//else if(Robot.oi.AButton(P2)){ Flex = DoubleSolenoid.Value.kReverse; } //Release
		//else { Flex = DoubleSolenoid.Value.kOff; }
		//Robot.kDrivetrain.setRawBicep(Flex); 
		
		if(Robot.oi.RightBumperDown(P2)) { throottle = .6; }
		else if(Robot.oi.RightBumperDown(P1)) {throottle = .6; }
		else if(Robot.oi.LeftBumperDown(P2)) { throottle = -.6;}
		else if(Robot.oi.LeftBumperDown(P1)) { throottle = -6;}
		else { throottle = 0; }
		Robot.kDrivetrain.setRawIntake(throottle);

		magnitudeR2 = Math.sqrt(Math.pow(Robot.oi.RightStickX(P2), 2) + Math.pow(Robot.oi.RightStickY(P2), 2));
		if(magnitudeR2 > .3) { ballThrottle = .75*-Robot.oi.RightStickY(P2); }
		else if(Robot.oi.RightBumperDown(P2)) { ballThrottle = -.5; }
		else if(Robot.oi.RightBumperDown(P1)) { ballThrottle = -.5; }
		else if(Robot.oi.LeftBumperDown(P1))  { ballThrottle =  .5; }
		else{ ballThrottle = 0; }
		Robot.kDrivetrain.setRawBallIn(ballThrottle); 
		
		if(Robot.oi.Dpad(P2) >= 0 || Robot.oi.Dpad(P1) >= 0 || Robot.oi.YButtonDown(P2) || Robot.oi.XButtonDown(P2) ) { 
      if(forwardLimitSwitch.get() || reverseLimitSwitch.get()) { Robot.kDrivetrain.setRawElevator(0);}
            else if     (Robot.oi.DpadDown(P1) || Robot.oi.DpadDown(P2) )  { ElevSetting = 1; Robot.kDrivetrain.ElevOn(true); }
            else if(Robot.oi.DpadRight(P1)|| Robot.oi.DpadRight(P2))  { ElevSetting = 2; Robot.kDrivetrain.ElevOn(true); }
            else if(Robot.oi.DpadUp(P1)   || Robot.oi.DpadUp(P2)   )  { ElevSetting = 3; Robot.kDrivetrain.ElevOn(true); }
            else if(Robot.oi.DpadLeft(P1) || Robot.oi.DpadLeft(P2) )  { ElevSetting = 4; Robot.kDrivetrain.ElevOn(true); } 
            else if(Robot.oi.YButtonDown(P2)){ ElevSetting = 5; Robot.kDrivetrain.ElevOn(true); }
            else if(Robot.oi.XButtonDown(P2)){ ElevSetting = 6; Robot.kDrivetrain.ElevOn(true); }
        }   else{ ElevSetting = 0; Robot.kDrivetrain.ElevOn(false); }

        

		switch(ElevSetting) {
		case 0: 
			Robot.kDrivetrain.setRawElevator(throttle*(Robot.oi.LeftTrigger(P2) - Robot.oi.RightTrigger(P2)));
			break; 
		case 1: Robot.kDrivetrain.setElevatorPosition(heightB0); break; 
		case 2: Robot.kDrivetrain.setElevatorPosition(heightB1); break; 
		case 3: Robot.kDrivetrain.setElevatorPosition(heightB2); break;
		case 4: Robot.kDrivetrain.setElevatorPosition(heightB3); break; 
		case 6: Robot.kDrivetrain.setElevatorPosition(heightH2); break; 
		case 7: Robot.kDrivetrain.setElevatorPosition(heightH3); break; 
		}


  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
