package frc.robot.auto;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.auto.actions.Action;

/**
 * An abstract class that is the basis of the robot's autonomous routines. This is implemented in
 * auto modes (which are routines that do actions).
 */
public abstract class AutoModeBase {
  protected double updateRate = 1.0 / 20.0;
  protected boolean active = false;

  protected abstract void routine() throws AutoModeEndedException;

  /** Runs the routine. */
  public void run() {
    active = true;

    try {
      routine();
    } catch (AutoModeEndedException e) {
      DriverStation.reportError("AUTO MODE DONE!!!! ENDED EARLY!!!!", false);
      return;
    }

    done();
  }

  public void done() {
    System.out.println("Auto mode done");
  }

  public void stop() {
    active = false;
  }

  public boolean isActive() {
    return active;
  }

  /**
   * Returns true if mode is active. This will throw AutoModeEndedException if not active.
   *
   * @return true if mode is active
   */
  public boolean isActiveWithThrow() throws AutoModeEndedException {
    if (!isActive()) {
      throw new AutoModeEndedException();
    }

    return isActive();
  }

  /**
   * Runs the specified action.
   *
   * @param action the action to run
   */
  public void runAction(Action action) throws AutoModeEndedException {
    isActiveWithThrow();
    action.start();

    while (isActiveWithThrow() && !action.isFinished()) {
      action.update();
      long waitTime = (long) (updateRate * 1000.0);

      try {
        Thread.sleep(waitTime);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    action.done();
  }
}
