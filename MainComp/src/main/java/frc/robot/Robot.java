package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.lib.util.DebugLog;
import frc.robot.auto.AutoModeBase;
import frc.robot.auto.AutoModeExecutor;
import frc.robot.components.SwerveDrive;
import java.util.Optional;

/**
 * This is the main hub for all other classes. Each of the overrided methods are synced with FMS and
 * driverstation packets. Periodic methods loop throughverything inside of them and then return to
 * the top. Init methods are run once, and then pass on to their respective periodic method.
 *
 * @author FRC 1778 Chill Out
 */
public class Robot extends TimedRobot {
  private AutoFieldState autoFieldState = AutoFieldState.getInstance();
  private AutoModeSelector autoModeSelector = new AutoModeSelector();
  private AutoModeExecutor autoModeExecutor;

  private SwerveDrive swerve = SwerveDrive.getinstance();
  private Controls controls = Controls.getInstance();

  public Robot() {
    DebugLog.logRobotConstruction();
  }

  @Override
  public void robotInit() {
    try {
      DebugLog.logRobotInit();

      sendTelemetry();
      autoModeSelector.updateModeCreator();
      autoFieldState.setSides(DriverStation.getInstance().getGameSpecificMessage());
    } catch (Throwable t) {
      DebugLog.logThrowableCrash(t);
    }
  }

  @Override
  public void disabledInit() {
    try {
      DebugLog.logDisabledInit();

      if (autoModeExecutor != null) {
        autoModeExecutor.stop();
      }

      autoModeSelector.reset();
      autoModeSelector.updateModeCreator();
      autoModeExecutor = new AutoModeExecutor();
    } catch (Throwable t) {
      DebugLog.logThrowableCrash(t);
    }
  }

  @Override
  public void autonomousInit() {
    try {
      DebugLog.logAutoInit();

      autoModeExecutor.start();
      DebugLog.logNote(
          String.format(
              "Auto mode %s selected and running.",
              autoModeExecutor.getAutoMode().getClass().getSimpleName()));
    } catch (Throwable t) {
      DebugLog.logThrowableCrash(t);
    }
  }

  @Override
  public void teleopInit() {
    try {
      DebugLog.logTeleopInit();

      swerve.zeroSensors();

      if (autoModeExecutor != null) {
        autoModeExecutor.stop();
      }
    } catch (Throwable t) {
      DebugLog.logThrowableCrash(t);
    }
  }

  @Override
  public void testInit() {
    try {
      DebugLog.logTestInit();
    } catch (Throwable t) {
      DebugLog.logThrowableCrash(t);
    }
  }

  @Override
  public void disabledPeriodic() {
    try {
      autoFieldState.setSides(DriverStation.getInstance().getGameSpecificMessage());
      autoModeSelector.updateModeCreator();

      if (autoFieldState.isValid()) {
        Optional<AutoModeBase> autoMode = autoModeSelector.getAutoMode(autoFieldState);
        if (autoMode.isPresent() && autoMode.get() != autoModeExecutor.getAutoMode()) {
          autoModeExecutor.setAutoMode(autoMode.get());
        }
      }
    } catch (Throwable t) {
      DebugLog.logThrowableCrash(t);
    }
  }

  @Override
  public void autonomousPeriodic() {
    try {
    } catch (Throwable t) {
      DebugLog.logThrowableCrash(t);
    }
  }

  @Override
  public void teleopPeriodic() {
    try {
      boolean slowMode = controls.getSlowMode();

      if (controls.getFieldCentricToggle()) {
        double angle = Math.toRadians(swerve.getNavX().getAngle());

        double forward = (slowMode ? 0.6 : 1.0) * controls.getTranslationY();
        double strafe = (slowMode ? 0.6 : 1.0) * controls.getTranslationX();

        double temp = (forward * Math.cos(angle)) + (strafe * Math.sin(angle));
        strafe = (-forward * Math.sin(angle)) + (strafe * Math.cos(angle));
        forward = temp;

        if (controls.getTranslationX() != 0 | controls.getTranslationY() != 0) {
            swerve.setSignals(
            swerve.calculateModuleSignals(
                (slowMode ? 0.6 : 1.0) * forward,
                (slowMode ? 0.6 : 1.0) * strafe,
                (slowMode ? 0.6 : 1.0) * controls.getRotation()));
        }
      } else {
        swerve.setSignals(
            swerve.calculateModuleSignals(
                (slowMode ? 0.6 : 1.0) * controls.getTranslationY(),
                (slowMode ? 0.6 : 1.0) * controls.getTranslationX(),
                (slowMode ? 0.6 : 1.0) * controls.getRotation()));
      }
    } catch (Throwable t) {
      DebugLog.logThrowableCrash(t);
    }
  }

  @Override
  public void testPeriodic() {
    try {
      swerve.setAllToAngle(0);
      System.out.println("leftFront angle: " + swerve.leftFront.getAbsAngle());
      System.out.println("rightFront angle: " + swerve.rightFront.getAbsAngle());
      System.out.println("leftBack angle: " + swerve.leftBack.getAbsAngle());
      System.out.println("rightBack angle: " + swerve.rightBack.getAbsAngle());
    } catch (Throwable t) {
      DebugLog.logThrowableCrash(t);
    }
  }

  private void sendTelemetry() {
    swerve.sendTelemetry();
  }
}
