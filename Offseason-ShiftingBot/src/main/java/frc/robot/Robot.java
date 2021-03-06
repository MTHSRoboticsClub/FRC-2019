package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import frc.lib.util.DebugLog;
import frc.robot.auto.AutoModeBase;
import frc.robot.auto.AutoModeExecutor;
import java.util.Optional;

/**
 * This is the main hub for all other classes. Each of the overrided methods are synced with FMS and
 * driverstation packets. Periodic methods loop throughverything inside of them and then return to
 * the top. Init methods are run once, and then pass on to their respective periodic method.
 *
 * @author FRC 1778 Chill Out
 */
public class Robot extends IterativeRobot {
  private AutoFieldState autoFieldState = AutoFieldState.getInstance();
  private AutoModeSelector autoModeSelector = new AutoModeSelector();
  private AutoModeExecutor autoModeExecutor;

  public Robot() {
    DebugLog.logRobotConstruction();
  }

  @Override
  public void robotInit() {
    try {
      DebugLog.logRobotInit();

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
      throw new NullPointerException();
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
      System.out.println("TeleOp");
    } catch (Throwable t) {
      DebugLog.logThrowableCrash(t);
    }
  }

  @Override
  public void testPeriodic() {
    try {
    } catch (Throwable t) {
      DebugLog.logThrowableCrash(t);
    }
  }
}
