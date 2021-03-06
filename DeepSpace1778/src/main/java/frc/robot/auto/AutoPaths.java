package frc.robot.auto;

import frc.lib.pathing.Path;
import frc.lib.pathing.PathSegment;
import frc.robot.Constants;

/**
 * Stores reusable paths for use during the autonomous period. For example, a path that starts on
 * the left side of the HAB platform and goes to the near side of the left rocket can be used in
 * multiple auto modes, and such should be in this class.
 *
 * @author FRC 1778 Chill Out
 */
public class AutoPaths {
  public static final Path START_RIGHT_TO_RIGHT_ROCKET_NEAR_SIDE =
      new Path(
          -90,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          0.0,
          new PathSegment.Line(36, 0),
          new PathSegment.ArcedTranslation(36, 36, 0),
          new PathSegment.Line(84, 331.23),
          new PathSegment.Line(12, 331.23));

  public static final Path START_LEFT_TO_LEFT_ROCKET_NEAR_SIDE =
      START_RIGHT_TO_RIGHT_ROCKET_NEAR_SIDE.getFlipped();

  public static final Path RIGHT_ROCKET_NEAR_SIDE_TO_RIGHT_FEEDER_STATION =
      new Path(
          180,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          331.23,
          new PathSegment.Line(12, 331.23),
          new PathSegment.Line(120, 180),
          new PathSegment.Line(12, 180));

  public static final Path LEFT_ROCKET_NEAR_SIDE_TO_LEFT_FEEDER_STATION =
      RIGHT_ROCKET_NEAR_SIDE_TO_RIGHT_FEEDER_STATION.getFlipped();

  public static final Path RIGHT_FEEDER_STATION_TO_RIGHT_ROCKET_NEAR_SIDE =
      new Path(
          0,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          180.0,
          new PathSegment.Line(12, 180),
          new PathSegment.Line(120, 331.23));

  public static final Path LEFT_FEEDER_STATION_TO_LEFT_ROCKET_NEAR_SIDE =
      RIGHT_FEEDER_STATION_TO_RIGHT_ROCKET_NEAR_SIDE.getFlipped();

  public static final Path RIGHT_FEEDER_STATION_TO_RIGHT_ROCKET_FAR_SIDE =
      new Path(
          0,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          180.0,
          new PathSegment.Line(96, 210),
          new PathSegment.ArcedTranslation(36, 72, 210),
          new PathSegment.ArcedTranslation(36, 72, 150).getFlipped(),
          new PathSegment.ArcedTranslation(60, 60, 150).getFlipped());

  public static final Path LEFT_FEEDER_STATION_TO_LEFT_ROCKET_FAR_SIDE =
      RIGHT_FEEDER_STATION_TO_RIGHT_ROCKET_FAR_SIDE.getFlipped();

  public static final Path START_RIGHT_TO_RIGHT_CARGO_BAY_NEAR =
      new Path(
          0,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          0.0,
          new PathSegment.Line(48, 0.0),
          new PathSegment.ArcedTranslation(24, 48, 0.0).getFlipped(),
          new PathSegment.ArcedTranslation(24, 48, 0.0),
          new PathSegment.Line(96, 90.0),
          new PathSegment.Line(24, 90.0));

  public static final Path START_LEFT_TO_LEFT_CARGO_BAY_NEAR =
      START_RIGHT_TO_RIGHT_CARGO_BAY_NEAR.getFlipped();

  public static final Path START_CENTER_TO_RIGHT_FRONT_CARGO_BAY =
      new Path(
          0,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          0.0,
          new PathSegment.Line(48, 0.0),
          new PathSegment.ArcedTranslation(22, 48, 0.0).getFlipped(),
          new PathSegment.ArcedTranslation(22, 48, 0.0),
          new PathSegment.Line(36, 0.0));

  public static final Path START_CENTER_TO_LEFT_FRONT_CARGO_BAY =
      START_CENTER_TO_RIGHT_FRONT_CARGO_BAY.getFlipped();

  public static final Path RIGHT_FRONT_CARGO_BAY_TO_RIGHT_DEPOT =
      new Path(
          180,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          0.0,
          new PathSegment.ArcedTranslation(48, 48, 90.0),
          new PathSegment.ArcedTranslation(48, 48, 225.0).getFlipped(),
          new PathSegment.Line(60, 135.0));

  public static final Path LEFT_FRONT_CARGO_BAY_TO_LEFT_DEPOT =
      RIGHT_FRONT_CARGO_BAY_TO_RIGHT_DEPOT.getFlipped();

  public static final Path RIGHT_DEPOT_TO_ROCKET_CARGO =
      new Path(
          0,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          135.0,
          new PathSegment.Line(120, -90.0),
          new PathSegment.Line(48, -90.0));

  public static final Path LEFT_DEPOT_TO_ROCKET_CARGO = RIGHT_DEPOT_TO_ROCKET_CARGO.getFlipped();

  public static final Path RIGHT_CARGO_BAY_NEAR_TO_RIGHT_FEEDER_STATION =
      new Path(
          -90,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          90.0,
          new PathSegment.ArcedTranslation(24, 24, -90).getFlipped(),
          new PathSegment.Line(120, 180),
          new PathSegment.ArcedTranslation(24, 48, 180),
          new PathSegment.ArcedTranslation(24, 48, 180).getFlipped(),
          new PathSegment.Line(48, 180));

  public static final Path LEFT_CARGO_BAY_NEAR_TO_LEFT_FEEDER_STATION =
      RIGHT_CARGO_BAY_NEAR_TO_RIGHT_FEEDER_STATION.getFlipped();

  public static final Path RIGHT_FEEDER_STATION_TO_RIGHT_CARGO_BAY_NEAR =
      new Path(
          0,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          180.0,
          new PathSegment.Line(24, 180),
          new PathSegment.ArcedTranslation(36, 48, 180),
          new PathSegment.ArcedTranslation(36, 48, 180).getFlipped(),
          new PathSegment.Line(120, 90),
          new PathSegment.ArcedTranslation(24, 24, 90));

  public static final Path LEFT_FEEDER_STATION_TO_LEFT_CARGO_BAY_NEAR =
      RIGHT_FEEDER_STATION_TO_RIGHT_CARGO_BAY_NEAR.getFlipped();

  public static final Path RIGHT_FEEDER_STATION_TO_RIGHT_CARGO_BAY_CENTER =
      new Path(
          0,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          180.0,
          new PathSegment.Line(48, 180),
          new PathSegment.ArcedTranslation(36, 48, 180),
          new PathSegment.ArcedTranslation(36, 48, 180).getFlipped(),
          new PathSegment.Line(120, 90),
          new PathSegment.ArcedTranslation(24, 24, 90));

  public static final Path LEFT_FEEDER_STATION_TO_LEFT_CARGO_BAY_CENTER =
      RIGHT_FEEDER_STATION_TO_RIGHT_CARGO_BAY_CENTER.getFlipped();

  public static final Path RIGHT_FEEDER_STATION_TO_RIGHT_CARGO_BAY_FAR =
      new Path(
          0,
          Constants.SWERVE_MAX_ACCELERATION,
          Constants.SWERVE_MAX_VELOCITY,
          180.0,
          new PathSegment.Line(60, 180),
          new PathSegment.ArcedTranslation(36, 48, 180),
          new PathSegment.ArcedTranslation(36, 48, 180).getFlipped(),
          new PathSegment.Line(120, 90),
          new PathSegment.ArcedTranslation(24, 24, 90));

  public static final Path LEFT_FEEDER_STATION_TO_LEFT_CARGO_BAY_FAR =
      RIGHT_FEEDER_STATION_TO_RIGHT_CARGO_BAY_FAR.getFlipped();
}
