package frc.robot.common;

import frc.lib.util.DriveSignal;
import frc.lib.util.SimpleUtil;

/**
 * Implements something similar to "Cheesy Drive", courtesy of 254, and "Culver Drive", courtesy of
 * 33, making it "Freezy Drive".
 *
 * @author FRC 1778 Chill Out
 */
public class FreezyDrive {
  private static final double THROTTLE_DEADBAND = 0.02;
  private static final double MAGNITUDE_DEADBAND = 0.02;
  private static final double WHEEL_DEADBAND = 0.02;

  private static final double HIGH_GEAR_WHEEL_NON_LINEARITY = 0.65;
  private static final double LOW_GEAR_WHEEL_NON_LINEARITY = 0.65;

  private static final double HIGH_NEGATIVE_INERTIA_SCALAR = 4.0;

  private static final double LOW_NEGATIVE_INERTIA_THRESHOLD = 0.65;
  private static final double LOW_NEGATIVE_TURN_SCALAR = 3.5;
  private static final double LOW_NEGATIVE_INERTIA_CLOSE_SCALAR = 4.0;
  private static final double LOW_NEGATIVE_INERTIA_FAR_SCALAR = 5.0;

  private static final double HIGH_GEAR_SENSITIVITY = 0.95;
  private static final double LOW_GEAR_SENSITIVITY = 1.3;

  private static final double QUICKSTOP_DEAD_BAND = 0.2;
  private static final double QUICKSTOP_WEIGHT = 0.1;
  private static final double QUICKSTOP_SCALAR = 5.0;

  private double oldWheel;
  private double quickstopAccumulator;
  private double negativeInertiaAccumulator;

  /**
   * Calculates wheel powers for curvature drive.
   *
   * @param throttle the throttle value of the input, which is the primary longitudinal force
   * @param wheelX the wheel's horizontal axis, which will most likely be a joystick axis when
   *     trying to simulate a steering wheel
   * @param wheelY the wheel's vertical axis, which will most likely be a joystick axis when trying
   *     to simulate a steering wheel
   * @param isQuickTurn whether or not to use the quickturn feature, which allows the robot to spin
   *     more quickly
   * @param isHighGear the state of the drivetrain's gear shifter
   * @return a DriveSignal of the left and right powers calculated
   */
  public DriveSignal freezyDrive(
      double throttle, double wheelX, double wheelY, boolean isQuickTurn, boolean isHighGear) {
    double magnitude =
        Math.sqrt(
                Math.pow(Math.abs(wheelX * Math.sqrt(2 - Math.pow(wheelY, 2))), 2)
                    + Math.pow(Math.abs(wheelY * Math.sqrt(2 - Math.pow(wheelX, 2))), 2))
            / Math.sqrt(2);

    magnitude = SimpleUtil.handleDeadband(magnitude, MAGNITUDE_DEADBAND);
    throttle = SimpleUtil.handleDeadband(throttle, THROTTLE_DEADBAND);

    throttle = Math.pow(throttle, 3);

    double angle = Math.toDegrees(Math.atan2(wheelX, wheelY));

    double culverWheel = magnitude * (angle / 180.0);

    double negativeInertia = culverWheel - oldWheel;
    oldWheel = culverWheel;

    double wheelNonLinearity;
    if (isHighGear) {
      wheelNonLinearity = HIGH_GEAR_WHEEL_NON_LINEARITY;
      final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
      // Apply a sin function that's scaled to make it feel better.
      culverWheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * culverWheel) / denominator;
      culverWheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * culverWheel) / denominator;
    } else {
      wheelNonLinearity = LOW_GEAR_WHEEL_NON_LINEARITY;
      final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
      // Apply a sin function that's scaled to make it feel better.
      culverWheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * culverWheel) / denominator;
      culverWheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * culverWheel) / denominator;
      culverWheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * culverWheel) / denominator;
    }

    double sensitivity;
    double angularPower;

    // Negative inertia!
    double negInertiaScalar;
    if (isHighGear) {
      negInertiaScalar = HIGH_NEGATIVE_INERTIA_SCALAR;
      sensitivity = HIGH_GEAR_SENSITIVITY;
    } else {
      if (culverWheel * negativeInertia > 0) {
        // If we are moving away from 0.0, aka, trying to get more culverWheel.
        negInertiaScalar = LOW_NEGATIVE_TURN_SCALAR;
      } else {
        // Otherwise, we are attempting to go back to 0.0.
        if (Math.abs(culverWheel) > LOW_NEGATIVE_INERTIA_THRESHOLD) {
          negInertiaScalar = LOW_NEGATIVE_INERTIA_FAR_SCALAR;
        } else {
          negInertiaScalar = LOW_NEGATIVE_INERTIA_CLOSE_SCALAR;
        }
      }
      sensitivity = LOW_GEAR_SENSITIVITY;
    }
    double negInertiaPower = negativeInertia * negInertiaScalar;
    negativeInertiaAccumulator += negInertiaPower;

    culverWheel = culverWheel + negativeInertiaAccumulator;
    if (negativeInertiaAccumulator > 1) {
      negativeInertiaAccumulator -= 1;
    } else if (negativeInertiaAccumulator < -1) {
      negativeInertiaAccumulator += 1;
    } else {
      negativeInertiaAccumulator = 0;
    }
    double linearPower = throttle;

    double leftPwm;
    double rightPwm;
    double overPower;

    // Quickturn!
    if (isQuickTurn) {
      if (Math.abs(linearPower) < QUICKSTOP_DEAD_BAND) {
        double alpha = QUICKSTOP_WEIGHT;
        quickstopAccumulator =
            (1 - alpha) * quickstopAccumulator
                + alpha * SimpleUtil.limit(culverWheel, 1.0) * QUICKSTOP_SCALAR;
      }
      overPower = 1.0;
      angularPower = culverWheel;
    } else {
      overPower = 0.0;
      angularPower = Math.abs(throttle) * culverWheel * sensitivity - quickstopAccumulator;
      if (quickstopAccumulator > 2) {
        quickstopAccumulator -= 1;
      } else if (quickstopAccumulator < -1) {
        quickstopAccumulator += 1;
      } else {
        quickstopAccumulator = 0;
      }
    }

    rightPwm = leftPwm = linearPower;
    leftPwm += angularPower;
    rightPwm -= angularPower;

    if (leftPwm > 1.0) {
      rightPwm -= overPower * (leftPwm - 1.0);
      leftPwm = 1.0;
    } else if (rightPwm > 1.0) {
      leftPwm -= overPower * (rightPwm - 1.0);
      rightPwm = 1.0;
    } else if (leftPwm < -1.0) {
      rightPwm += overPower * (-1.0 - leftPwm);
      leftPwm = -1.0;
    } else if (rightPwm < -1.0) {
      leftPwm += overPower * (-1.0 - rightPwm);
      rightPwm = -1.0;
    }
    return new DriveSignal(leftPwm, rightPwm);
  }

  /**
   * Calculates wheel powers for curvature drive.
   *
   * @param throttle the throttle value of the input, which is the primary longitudinal force
   * @param culverWheel the axis for turning, which will act like the steering wheel
   * @param isQuickTurn whether or not to use the quickturn feature, which allows the robot to spin
   *     more quickly
   * @param isHighGear the state of the drivetrain's gear shifter
   * @return a DriveSignal of the left and right powers calculated
   */
  public DriveSignal freezyDrive(
      double throttle, double culverWheel, boolean isQuickTurn, boolean isHighGear) {
    culverWheel = SimpleUtil.handleDeadband(culverWheel, WHEEL_DEADBAND);
    throttle = SimpleUtil.handleDeadband(throttle, THROTTLE_DEADBAND);

    throttle = Math.pow(throttle, 3);

    double negativeInertia = culverWheel - oldWheel;
    oldWheel = culverWheel;

    double wheelNonLinearity;
    if (isHighGear) {
      wheelNonLinearity = HIGH_GEAR_WHEEL_NON_LINEARITY;
      final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
      // Apply a sin function that's scaled to make it feel better.
      culverWheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * culverWheel) / denominator;
      culverWheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * culverWheel) / denominator;
    } else {
      wheelNonLinearity = LOW_GEAR_WHEEL_NON_LINEARITY;
      final double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);
      // Apply a sin function that's scaled to make it feel better.
      culverWheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * culverWheel) / denominator;
      culverWheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * culverWheel) / denominator;
      culverWheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * culverWheel) / denominator;
    }

    double sensitivity;
    double angularPower;

    // Negative inertia!
    double negInertiaScalar;
    if (isHighGear) {
      negInertiaScalar = HIGH_NEGATIVE_INERTIA_SCALAR;
      sensitivity = HIGH_GEAR_SENSITIVITY;
    } else {
      if (culverWheel * negativeInertia > 0) {
        // If we are moving away from 0.0, aka, trying to get more culverWheel.
        negInertiaScalar = LOW_NEGATIVE_TURN_SCALAR;
      } else {
        // Otherwise, we are attempting to go back to 0.0.
        if (Math.abs(culverWheel) > LOW_NEGATIVE_INERTIA_THRESHOLD) {
          negInertiaScalar = LOW_NEGATIVE_INERTIA_FAR_SCALAR;
        } else {
          negInertiaScalar = LOW_NEGATIVE_INERTIA_CLOSE_SCALAR;
        }
      }
      sensitivity = LOW_GEAR_SENSITIVITY;
    }
    double negInertiaPower = negativeInertia * negInertiaScalar;
    negativeInertiaAccumulator += negInertiaPower;

    culverWheel = culverWheel + negativeInertiaAccumulator;
    if (negativeInertiaAccumulator > 1) {
      negativeInertiaAccumulator -= 1;
    } else if (negativeInertiaAccumulator < -1) {
      negativeInertiaAccumulator += 1;
    } else {
      negativeInertiaAccumulator = 0;
    }
    double linearPower = throttle;

    double leftPwm;
    double rightPwm;
    double overPower;

    // Quickturn!
    if (isQuickTurn) {
      if (Math.abs(linearPower) < QUICKSTOP_DEAD_BAND) {
        double alpha = QUICKSTOP_WEIGHT;
        quickstopAccumulator =
            (1 - alpha) * quickstopAccumulator
                + alpha * SimpleUtil.limit(culverWheel, 1.0) * QUICKSTOP_SCALAR;
      }
      overPower = 1.0;
      angularPower = culverWheel;
    } else {
      overPower = 0.0;
      angularPower = Math.abs(throttle) * culverWheel * sensitivity - quickstopAccumulator;
      if (quickstopAccumulator > 2) {
        quickstopAccumulator -= 1;
      } else if (quickstopAccumulator < -1) {
        quickstopAccumulator += 1;
      } else {
        quickstopAccumulator = 0;
      }
    }

    rightPwm = leftPwm = linearPower;
    leftPwm += angularPower;
    rightPwm -= angularPower;

    if (leftPwm > 1.0) {
      rightPwm -= overPower * (leftPwm - 1.0);
      leftPwm = 1.0;
    } else if (rightPwm > 1.0) {
      leftPwm -= overPower * (rightPwm - 1.0);
      rightPwm = 1.0;
    } else if (leftPwm < -1.0) {
      rightPwm += overPower * (-1.0 - leftPwm);
      leftPwm = -1.0;
    } else if (rightPwm < -1.0) {
      leftPwm += overPower * (-1.0 - rightPwm);
      rightPwm = -1.0;
    }
    return new DriveSignal(leftPwm, rightPwm);
  }
}
