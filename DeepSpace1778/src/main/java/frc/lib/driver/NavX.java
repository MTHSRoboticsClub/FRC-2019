package frc.lib.driver;

import com.kauailabs.navx.AHRSProtocol.AHRSUpdateBase;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.ITimestampedDataSubscriber;
import edu.wpi.first.wpilibj.SPI;

/**
 * Driver for a NavX IMU. This wraps the basic functions of the AHRS class, simplifying lower level
 * calls to the NavX itself.
 *
 * @author FRC 254 The Cheesy Poofs
 */
public class NavX {

  private class Callback implements ITimestampedDataSubscriber {

    @Override
    public void timestampedDataReceived(
        long systemTimestamp, long sensorTimestamp, AHRSUpdateBase update, Object context) {
      synchronized (NavX.this) {
        // This handles the fact that the sensor is inverted from our coordinate
        // conventions.
        if (lastSensorTimestampMs != invalidTimestamp && lastSensorTimestampMs < sensorTimestamp) {
          yawRateDegreesPerSecond =
              1000.0
                  * (-yawDegrees - update.yaw)
                  / (double) (sensorTimestamp - lastSensorTimestampMs);
        }
        lastSensorTimestampMs = sensorTimestamp;
        yawDegrees = -update.yaw;
      }
    }
  }

  private AHRS ahrs;

  private double angleAdjustment;
  private double yawDegrees;
  private double yawRateDegreesPerSecond;
  private final long invalidTimestamp = -1;
  private long lastSensorTimestampMs;

  public NavX(SPI.Port spiPortId) {
    ahrs = new AHRS(spiPortId, (byte) 200);
    resetState();
    ahrs.registerCallback(new Callback(), null);
  }

  public synchronized void reset() {
    ahrs.reset();
    resetState();
  }

  public synchronized void zeroYaw() {
    ahrs.zeroYaw();
    resetState();
  }

  private void resetState() {
    lastSensorTimestampMs = invalidTimestamp;
    yawDegrees = 0.0;
    yawRateDegreesPerSecond = 0.0;
  }

  public synchronized void setAngleAdjustment(double adjustment) {
    angleAdjustment = adjustment;
  }

  public double getAngle() {
    return -ahrs.getAngle();
  }

  private synchronized double getRawYawDegrees() {
    return yawDegrees;
  }

  public double getYaw() {
    return angleAdjustment + getRawYawDegrees();
  }

  public double getYawRateDegreesPerSec() {
    return yawRateDegreesPerSecond;
  }

  public double getYawRateRadiansPerSec() {
    return 180.0 / Math.PI * getYawRateDegreesPerSec();
  }

  public double getRawAccelX() {
    return ahrs.getRawAccelX();
  }
}
