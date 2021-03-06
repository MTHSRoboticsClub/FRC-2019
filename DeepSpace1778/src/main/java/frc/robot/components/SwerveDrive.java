package frc.robot.components;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import frc.lib.driver.NavX;
import frc.lib.util.ModuleSignal;
import frc.robot.Constants;
import frc.robot.Ports;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all calculations and control over the four (4) swerve modules associated with the swerve
 * drive. The swerve drive also has one (1) NavX IMU connected to the subsystem.
 *
 * @author FRC 1778 Chill Out
 */
public class SwerveDrive extends Subsystem {

  private static SwerveDrive instance;

  private static boolean initialized;

  public SwerveModule leftFront;
  public SwerveModule rightFront;
  public SwerveModule leftBack;
  public SwerveModule rightBack;

  private NavX navX;

  private NetworkTableEntry leftFrontTurnAngleEntry;
  private NetworkTableEntry leftFrontDrivePowerEntry;

  private NetworkTableEntry rightFrontTurnAngleEntry;
  private NetworkTableEntry rightFrontDrivePowerEntry;

  private NetworkTableEntry leftBackTurnAngleEntry;
  private NetworkTableEntry leftBackDrivePowerEntry;

  private NetworkTableEntry rightBackTurnAngleEntry;
  private NetworkTableEntry rightBackDrivePowerEntry;

  private NetworkTableEntry bagMotorCurrentEntry;
  private NetworkTableEntry cimMotorCurrentEntry;

  private boolean shuffleboardInitialized;

  public static SwerveDrive getInstance() {
    return getInstance(true);
  }

  public static SwerveDrive getInstance(boolean hardware) {
    if (!initialized) {
      initialized = true;
      instance = new SwerveDrive(hardware);
    }

    return instance;
  }

  private SwerveDrive(boolean hardware) {
    if (hardware) {
      leftFront =
          new SwerveModule(
              Ports.LEFT_FRONT_ID, Ports.LEFT_FRONT_TURN_ID, Constants.LEFT_FRONT_ANGLE_OFFSET);
      rightFront =
          new SwerveModule(
              Ports.RIGHT_FRONT_ID, Ports.RIGHT_FRONT_TURN_ID, Constants.RIGHT_FRONT_ANGLE_OFFSET);
      leftBack =
          new SwerveModule(
              Ports.LEFT_BACK_ID, Ports.LEFT_BACK_TURN_ID, Constants.LEFT_BACK_ANGLE_OFFSET);
      rightBack =
          new SwerveModule(
              Ports.RIGHT_BACK_ID, Ports.RIGHT_BACK_TURN_ID, Constants.RIGHT_BACK_ANGLE_OFFSET);

      navX = new NavX(Ports.NAVX_SPI);
    }
  }

  @Override
  public void sendTelemetry(boolean debug) {
    if (shuffleboardInitialized) {
      leftFrontTurnAngleEntry.setDouble(leftFront.getCurrentAngle());
      rightFrontTurnAngleEntry.setDouble(rightFront.getCurrentAngle());
      leftBackTurnAngleEntry.setDouble(leftBack.getCurrentAngle());
      rightBackTurnAngleEntry.setDouble(rightBack.getCurrentAngle());

      if (debug) {
        leftFrontDrivePowerEntry.setDouble(leftFront.getDriveDistanceInches());
        rightFrontDrivePowerEntry.setDouble(rightFront.getDriveDistanceInches());
        leftBackDrivePowerEntry.setDouble(leftBack.getDriveDistanceInches());
        rightBackDrivePowerEntry.setDouble(rightBack.getDriveDistanceInches());
        bagMotorCurrentEntry.setDouble(
            +leftFront.getTurnMotor().getOutputCurrent()
                + rightFront.getTurnMotor().getOutputCurrent()
                + leftBack.getTurnMotor().getOutputCurrent()
                + rightBack.getTurnMotor().getOutputCurrent());
        cimMotorCurrentEntry.setDouble(
            leftFront.getDriveMotor().getOutputCurrent()
                + rightFront.getDriveMotor().getOutputCurrent()
                + leftBack.getDriveMotor().getOutputCurrent()
                + rightBack.getDriveMotor().getOutputCurrent());
      }
    } else {
      leftFrontTurnAngleEntry =
          Constants.debugTab
              .add("Left Front Angle", 0)
              .withWidget(BuiltInWidgets.kTextView)
              .withPosition(0, 0)
              .withSize(1, 1)
              .getEntry();
      rightFrontTurnAngleEntry =
          Constants.debugTab
              .add("Right Front Angle", 0)
              .withWidget(BuiltInWidgets.kTextView)
              .withPosition(1, 0)
              .withSize(1, 1)
              .getEntry();
      leftBackTurnAngleEntry =
          Constants.debugTab
              .add("Left Back Angle", 0)
              .withWidget(BuiltInWidgets.kTextView)
              .withPosition(2, 0)
              .withSize(1, 1)
              .getEntry();
      rightBackTurnAngleEntry =
          Constants.debugTab
              .add("Right Back Angle", 0)
              .withWidget(BuiltInWidgets.kTextView)
              .withPosition(3, 0)
              .withSize(1, 1)
              .getEntry();

      if (debug) {
        leftFrontDrivePowerEntry =
            Constants.debugTab
                .add("Left Front", 0)
                .withWidget(BuiltInWidgets.kTextView)
                .withPosition(0, 1)
                .withSize(1, 1)
                .getEntry();
        rightFrontDrivePowerEntry =
            Constants.debugTab
                .add("Right Front", 0)
                .withWidget(BuiltInWidgets.kTextView)
                .withPosition(1, 1)
                .withSize(1, 1)
                .getEntry();
        leftBackDrivePowerEntry =
            Constants.debugTab
                .add("Left Back", 0)
                .withWidget(BuiltInWidgets.kTextView)
                .withPosition(2, 1)
                .withSize(1, 1)
                .getEntry();
        rightBackDrivePowerEntry =
            Constants.debugTab
                .add("Right Back", 0)
                .withWidget(BuiltInWidgets.kTextView)
                .withPosition(3, 1)
                .withSize(1, 1)
                .getEntry();

        bagMotorCurrentEntry =
            Constants.debugTab
                .add("BAG Current", 0)
                .withWidget(BuiltInWidgets.kGraph)
                .withPosition(4, 0)
                .withSize(3, 3)
                .getEntry();
        cimMotorCurrentEntry =
            Constants.debugTab
                .add("CIM Current", 0)
                .withWidget(BuiltInWidgets.kGraph)
                .withPosition(7, 0)
                .withSize(3, 3)
                .getEntry();
      }
      shuffleboardInitialized = true;
    }
  }

  @Override
  public void resetEncoders() {
    leftFront.resetDriveEncoder();
    rightFront.resetDriveEncoder();
    leftBack.resetDriveEncoder();
    rightBack.resetDriveEncoder();
  }

  @Override
  public void zeroSensors() {
    navX.zeroYaw();
  }

  public NavX getNavX() {
    return navX;
  }

  public SwerveModule getLeftFrontModule() {
    return leftFront;
  }

  public SwerveModule getRightFrontModule() {
    return rightFront;
  }

  public SwerveModule getLeftBackModule() {
    return leftBack;
  }

  public SwerveModule getRightBackModule() {
    return rightBack;
  }

  public ArrayList<ModuleSignal> calculateModuleSignals(
      double forward, double strafe, double rotation) {
    ArrayList<ModuleSignal> signals = new ArrayList<ModuleSignal>(4);

    double a = strafe + (rotation * (Constants.VEHICLE_WHEELBASE / Constants.VEHICLE_DIAGONAL));
    double b = strafe - (rotation * (Constants.VEHICLE_WHEELBASE / Constants.VEHICLE_DIAGONAL));
    double c = forward + (rotation * (Constants.VEHICLE_TRACKWIDTH / Constants.VEHICLE_DIAGONAL));
    double d = forward - (rotation * (Constants.VEHICLE_TRACKWIDTH / Constants.VEHICLE_DIAGONAL));

    double leftFrontPower = Math.sqrt((a * a) + (d * d));
    double rightFrontPower = Math.sqrt((a * a) + (c * c));
    double leftBackPower = Math.sqrt((b * b) + (d * d));
    double rightBackPower = Math.sqrt((b * b) + (c * c));

    double max =
        Math.max(
            leftFrontPower, Math.max(rightFrontPower, Math.max(leftBackPower, rightBackPower)));

    if (max > 1) {
      leftFrontPower /= max;
      rightFrontPower /= max;
      leftBackPower /= max;
      rightBackPower /= max;
    }

    double leftFrontAngle = Math.atan2(a, d) * 180 / Math.PI;
    double rightFrontAngle = Math.atan2(a, c) * 180 / Math.PI;
    double leftBackAngle = Math.atan2(b, d) * 180 / Math.PI;
    double rightBackAngle = Math.atan2(b, c) * 180 / Math.PI;

    signals.add(new ModuleSignal(leftFrontPower, leftFrontAngle));
    signals.add(new ModuleSignal(rightFrontPower, rightFrontAngle));
    signals.add(new ModuleSignal(leftBackPower, leftBackAngle));
    signals.add(new ModuleSignal(rightBackPower, rightBackAngle));

    return signals;
  }

  public void setSignals(List<ModuleSignal> signals) {
    setTurnSignals(signals);
    setDriveSignals(signals);
  }

  public void setTurnSignals(List<ModuleSignal> signals) {
    setAllTurnPowers(0.5);

    leftFront.setTargetAngle(signals.get(0).getAngle());
    rightFront.setTargetAngle(signals.get(1).getAngle());
    leftBack.setTargetAngle(signals.get(2).getAngle());
    rightBack.setTargetAngle(signals.get(3).getAngle());
  }

  public void setDriveSignals(List<ModuleSignal> signals) {
    leftFront.setDrivePower(signals.get(0).getDrivePower());
    rightFront.setDrivePower(signals.get(1).getDrivePower());
    leftBack.setDrivePower(signals.get(2).getDrivePower());
    rightBack.setDrivePower(signals.get(3).getDrivePower());
  }

  public void setAllToAngle(double angle) {
    leftFront.setTargetAngle(angle);
    rightFront.setTargetAngle(angle);
    leftBack.setTargetAngle(angle);
    rightBack.setTargetAngle(angle);
  }

  public void setTargetDistances(
      double leftFrontDistance,
      double rightFrontDistance,
      double leftBackDistance,
      double rightBackDistance) {
    leftFront.setTargetDistance(leftFrontDistance / SwerveModule.INCHES_PER_ENCODER_PULSE);
    rightFront.setTargetDistance(rightFrontDistance / SwerveModule.INCHES_PER_ENCODER_PULSE);
    leftBack.setTargetDistance(leftBackDistance / SwerveModule.INCHES_PER_ENCODER_PULSE);
    rightBack.setTargetDistance(rightBackDistance / SwerveModule.INCHES_PER_ENCODER_PULSE);
  }

  public void stop() {
    setAllDrivePowers(0, 0, 0, 0);
  }

  private void setAllDrivePowers(
      double leftFrontPower, double rightFrontPower, double leftBackPower, double rightBackPower) {
    leftFront.setDrivePower(leftFrontPower);
    rightFront.setDrivePower(rightFrontPower);
    leftBack.setDrivePower(leftBackPower);
    rightBack.setDrivePower(rightBackPower);
  }

  public void setAllTurnPowers(double power) {
    leftFront.setTurnPower(power);
    rightFront.setTurnPower(power);
    leftBack.setTurnPower(power);
    rightBack.setTurnPower(power);
  }
}
