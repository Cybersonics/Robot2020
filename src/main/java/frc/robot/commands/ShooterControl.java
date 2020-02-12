/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import frc.robot.subsystems.Shooter;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.Robot;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * An example command that uses an example subsystem.
 */
public class ShooterControl extends CommandBase {
  //@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  public double ShooterSpeed = 0.9;
  public double Pivot = 0.9;

  Shooter ShooterOne;

  

  /**
   * constructor method
   *
   * @param subsystem The subsystem used by this command.
   */
  public ShooterControl() {
    
    // Creating new Shooter object
    ShooterOne = new Shooter();
    
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (Robot.xBoxController.getAButton()) 
    {
      ShooterSpeed = 0.9;
      ShooterOne.SetShooterRate(ShooterSpeed);
      ShooterOne.ShooterRun();
    }
    else if (Robot.xBoxController.getBButton()) 
    {
      ShooterOne.PivotUp();
    }
    // x is to Pivot Down
    else if (Robot.xBoxController.getXButton()) 
    {
      ShooterOne.PivotDown();
    }

    ShooterOne.PivotShutDown();

    if (!Robot.xBoxController.getAButton()){ //epic gamming moment!
      ShooterSpeed = 0; // f's in the chat for the shooter speed
      ShooterOne.SetShooterRate(ShooterSpeed); // begone ShooterSpeed
      ShooterOne.ShooterRun(); // wow u r such noob Shooterspeed uwu
    }


  //Curreently the Pivot is Manual, but later it can be done with a sensor
  }



  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {

  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
