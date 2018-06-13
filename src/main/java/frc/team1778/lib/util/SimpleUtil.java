package frc.team1778.lib.util;

public class SimpleUtil {

  public static double limit(double v, double maxMagnitude) {
    return limit(v, -maxMagnitude, maxMagnitude);
  }

  public static double limit(double v, double min, double max) {
    return Math.min(max, Math.max(min, v));
  }

  public static double limit(boolean limitHigh, double input, double limit) {
    if (limitHigh)
      if (input > limit) return limit;
      else return input;
    else if (input < limit) return limit;
    else return input;
  }

  public static double handleDeadband(double val, double deadband) {
    return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
  }
}
