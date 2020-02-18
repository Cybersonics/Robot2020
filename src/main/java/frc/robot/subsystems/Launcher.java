/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Launcher extends SubsystemBase {

  // Member Variables

  private final double MAX_SHOOTER_RATE = 1.0;
  private CANSparkMax _rightMotor;
  private CANSparkMax _leftMotor;
  private TalonSRX _pivotMotor;

  // End Member Variables

  // Constructors

  public Launcher() {
    CommandScheduler.getInstance().registerSubsystem(this);
    setupRightMotor();
    setupLeftMotor();
    setupPivotMotor();
  }

  // End Constructors

  // Public Methods

  public void calculatedLaunch(double speed) {
    _rightMotor.set(speed);
    _leftMotor.set(speed);
  }

  public void launcherStart() {
    calculatedLaunch(MAX_SHOOTER_RATE);
    calculatedLaunch(MAX_SHOOTER_RATE);
  }

  public void launcherStop() {
    calculatedLaunch(0);
    calculatedLaunch(0);
  }

  public void calculatedPivot(double setPoint) {
    _pivotMotor.set(ControlMode.Position, setPoint);
  }

  public void pivotMax() {
    calculatedPivot(50);
  }

  public void pivotParalleToFloor() {
    calculatedPivot(25);
  }

  // End Public Methods

  // Private Methods

  private void setupRightMotor() {
    _rightMotor = new CANSparkMax(Constants.SHOOTER_SPARK_ONE, MotorType.kBrushless);
    _rightMotor.restoreFactoryDefaults();
  }

  private void setupLeftMotor() {
    _leftMotor = new CANSparkMax(Constants.SHOOTER_SPARK_TWO, MotorType.kBrushless);
    _leftMotor.restoreFactoryDefaults();
    _leftMotor.setInverted(true);
  }

  private void setupPivotMotor() {
    _pivotMotor = new TalonSRX(Constants.PIVOT_TALON);
    _pivotMotor.configFactoryDefault();
    _pivotMotor.configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, 0);
    _pivotMotor.setInverted(false);
    _pivotMotor.setSensorPhase(false);
    _pivotMotor.config_kP(0, 1.0, 0);
    _pivotMotor.config_kI(0, 0.02, 0);
    _pivotMotor.config_kD(0, 0, 0);
    _pivotMotor.config_IntegralZone(0, 100, 0);
    _pivotMotor.configAllowableClosedloopError(0, 5, 0);
    _pivotMotor.setNeutralMode(NeutralMode.Brake);
    _pivotMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, 0);
  }

  // End Private Methods
}
