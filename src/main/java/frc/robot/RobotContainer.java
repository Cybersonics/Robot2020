/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.ClimberCommand;
import frc.robot.commands.IndexerCommand;
import frc.robot.commands.IntakeCommand;
import frc.robot.commands.Location;
import frc.robot.commands.MechIntakeCommand;
import frc.robot.commands.ShooterControl;
import frc.robot.commands.SnapPivotCommand;
import frc.robot.commands.AdjustPivotCommand;
import frc.robot.commands.TeleopDrive;
import frc.robot.commands.auton.AutonRoutines;
import frc.robot.commands.auton.AutonSelector;
import frc.robot.commands.auton.PathWeaverAuton;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Launcher;
import frc.robot.subsystems.MechIntake;
import frc.robot.subsystems.Vision;
// import net.bancino.robotics.libio.command.RunnableCommand;
import net.bancino.robotics.swerveio.gyro.NavXGyro;

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...

  /*
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private final ExampleCommand m_autoCommand = new ExampleCommand(m_exampleSubsystem);
  */
  
  private final Intake _intake = new Intake();
  private final MechIntake _mechIntake = new MechIntake();
  private final Indexer _indexer = new Indexer();
  private final Launcher _launcher = new Launcher();
  private final Vision _vision = new Vision();
  public static DriveTrain _drive;
  private final NavXGyro gyro = new NavXGyro(SPI.Port.kMXP);
  
  private final IntakeCommand _intakeCommand = new IntakeCommand(_intake);
  private final IndexerCommand _indexerCommand = new IndexerCommand(_indexer);
  private final ShooterControl _shooterCommand = new ShooterControl(_launcher, _vision);
  private final ClimberCommand extend = new ClimberCommand();

  private final AutonRoutines routines;
  private final AutonSelector autonSelector;

  public static Joystick leftJoy;
  public static Joystick rightJoy;
  public XboxController xboxController;

  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    
    CameraServer.getInstance().startAutomaticCapture();
    leftJoy = new Joystick(Constants.LEFT_JOYSTICK);
    rightJoy = new Joystick(Constants.RIGHT_JOYSTICK);
    xboxController = new XboxController(Constants.XBOX_CONTROLLER);

    /* Construct our subsystems here if they throw exceptions. */
    _drive = DriveTrain.getInstance(gyro);
    routines = new AutonRoutines(_drive, _launcher, _vision, _intake, _indexer);
    autonSelector = new AutonSelector(routines);
    
    configureCommands();
    configureButtonBindings();
  }

  private void configureCommands() {
    /* The drivetrain uses three axes: forward, strafe, and angular velocity, in that order. */
    TeleopDrive swerveDriveTeleop = new TeleopDrive(_drive, leftJoy, Joystick.AxisType.kY, Joystick.AxisType.kX, rightJoy, Joystick.AxisType.kX);
    _drive.setDefaultCommand(swerveDriveTeleop);
    
    _indexer.setDefaultCommand(new IndexerCommand(
      _indexer,
      () -> deadband(0.3, xboxController.getY(Hand.kRight))
    ));

    _intake.setDefaultCommand(new IntakeCommand(
      _intake, 
      () -> deadband(0.3, xboxController.getY(Hand.kLeft))
    ));

    _mechIntake.setDefaultCommand(new MechIntakeCommand (
      _mechIntake,
      () -> deadband(0.3, xboxController.getX(Hand.kLeft))
    ));
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    // new JoystickButton(leftJoy, 7)
    //   .whenPressed(new RunnableCommand(() -> {
    //     _drive.getGyro().zero();
    //   }, _drive)
    // );

    new JoystickButton(rightJoy, 3).whenPressed(() -> extend.extend());
    new JoystickButton(rightJoy, 3).whenPressed(new SnapPivotCommand(_launcher, Location.LiftOpen));
    new JoystickButton(rightJoy, 3).whenReleased(() -> extend.stop());
    new JoystickButton(rightJoy, 2).whenPressed(() -> extend.retract());
    new JoystickButton(rightJoy, 2).whenPressed(new SnapPivotCommand(_launcher, Location.LiftLock));
    new JoystickButton(rightJoy, 2).whenReleased(() -> extend.stop());

    new JoystickButton(xboxController, 1).whenPressed(new SnapPivotCommand(_launcher, Location.Trench));
    new JoystickButton(xboxController, 4).whenPressed(new SnapPivotCommand(_launcher, Location.Auton));
    new JoystickButton(xboxController, 2).whenPressed(new AdjustPivotCommand(_launcher, 15));
    new JoystickButton(xboxController, 3).whenPressed(new AdjustPivotCommand(_launcher, -15));

    new JoystickButton(xboxController, 5).whenPressed(() -> _vision.turnOnLightRings());
    new JoystickButton(xboxController, 7).whenPressed(() -> _vision.turnOffLightRings());
    
    new JoystickButton(xboxController, 6).whenPressed(() -> _shooterCommand.fire());
    new JoystickButton(xboxController, 6).whenReleased(() -> _shooterCommand.stop());
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // This command will run in autonomous

    // return autonSelector.getCommand();


    try {
      return new PathWeaverAuton(_drive, "paths/output/" + "Forward" + ".wpilib.json");
    } catch (java.io.IOException e) {
      e.printStackTrace();
      DriverStation.reportError("Could not load pathweaver swerve drive.", true);
      return null;
    }
  }

  /**
   * Calculate a deadband
   * 
   * @param raw The input on the joystick to mod
   * @return The result of the mod.
   */
  private double deadband(double deadband, double raw) {
    /* This will be our result */
    double mod;
    /* Compute the deadband mod */
    if (raw < 0.0d) {
      if (raw <= -deadband) {
        mod = (raw + deadband) / (1 - deadband);
      } else {
        mod = 0.0d;
      }
    } else {
      if (raw >= deadband) {
        mod = (raw - deadband) / (1 - deadband);
      } else {
        mod = 0.0d;
      }
    }
    /* Return the result. */
    return mod;
  }

}
