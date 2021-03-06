package frc.StateMachine;

import edu.wpi.first.wpilibj.RobotController;
import java.util.prefs.Preferences;

// event triggered when timer gets to a certain predetermined angle
public class TimeEvent extends Event {

  private String name;
  private double durationSec;
  private long startTimeUs;

  public TimeEvent() {
    this.name = "<Timer Event>";
    this.durationSec = 0.0;
  }

  public TimeEvent(double durationSec) {
    this.name = "<Timer Event>";
    this.durationSec = durationSec;
  }

  // overloaded initialize method
  public void initialize() {
    // System.out.println("TimeEvent initialized!");
    startTimeUs = RobotController.getFPGATime();

    super.initialize();
  }

  // overloaded trigger method
  public boolean isTriggered() {
    long currentTimeUs = RobotController.getFPGATime();
    double delta = (currentTimeUs - startTimeUs) / 1e6;
    // System.out.println("delta = " + delta + " duration = " + durationSec);

    if (delta > durationSec) {
      System.out.println("TimeEvent triggered!");
      return true;
    }

    return false;
  }

  public void persistWrite(int counter, Preferences prefs) {

    // create node for event
    Preferences eventPrefs = prefs.node(counter + "_" + this.name);

    // store event details
    eventPrefs.put("class", this.getClass().toString());
    eventPrefs.putDouble("durationSec", this.durationSec);
  }
}
