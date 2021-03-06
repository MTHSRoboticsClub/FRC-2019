package frc.Systems;

import java.util.ArrayList;

import frc.Utility.HardwareIDs;
import frc.NetworkComm.InputOutputComm;

import edu.wpi.first.wpilibj.Notifier;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.modifiers.TankModifier;

public class FreezyPath {
	
	private static boolean initialized = false;
	
	public static final int STRAIGHT_PATH = 0;
	public static final int SWERVE_RIGHT_AND_CENTER = 1;
	public static final int SWERVE_LEFT_AND_CENTER = 2;
	public static final int SWERVE_RIGHT_TURN_LEFT = 3;
	public static final int SWERVE_LEFT_TURN_RIGHT = 4;

	private static int m_pathToFollow = STRAIGHT_PATH;

	// Time Step:           0.02 Seconds (50 Hz)
	// Max Velocity:        6.0 ft/s
	// Max Acceleration:    2.0 ft/s/s
	// Max Jerk:            60 ft/s/s/s

	private static final double WHEELBASE_WIDTH_FT = 29.5/12;
	private static final double PERIOD_SEC = 0.02;
	private static final double MAX_VEL = 6.0;
	private static final double MAX_ACCEL = 2.0;
	private static final double MAX_JERK = 60.0;
	
	private static final double kP = 1.0;
	private static final double kI = 0.0;
	private static final double kD = 0.0;
	private static final double kV = 1.0 / MAX_VEL;
	private static final double kA = 0.0;
	
	private static ArrayList<Trajectory> trajectoryList;
	private static EncoderFollower leftEncoderFollower, rightEncoderFollower;

	private static Notifier m_notifier;
	
	private static Runnable m_handler = new Runnable() {
		public void run() {
			FreezyPath.update();
		}
	};

	// All path distances in ft

	// path 1 - drive straight 10 ft
	private static Waypoint[] path1 = new Waypoint[] {
		new Waypoint(0, 0, Pathfinder.d2r(0)),
		new Waypoint(5.0, 0.0, Pathfinder.d2r(0)),
		new Waypoint(10.0, 0.0, Pathfinder.d2r(0))
	};

	// path 2 - swerve to the right and back to center
	private static Waypoint[] path2 = new Waypoint[] {
		new Waypoint(0, 0, Pathfinder.d2r(0)),
		new Waypoint(5.0, -2.0, Pathfinder.d2r(0)),
		new Waypoint(10.0, 0.0, Pathfinder.d2r(0))
	};

	// path 3 - swerve to the left and back to center
	private static Waypoint[] path3 = new Waypoint[] {
		new Waypoint(0, 0, Pathfinder.d2r(0)),
		new Waypoint(5.0, 2.0, Pathfinder.d2r(0)),
		new Waypoint(10.0, 0.0, Pathfinder.d2r(0))
	};

	// path 4 - swerve to the right and turn left
	private static Waypoint[] path4 = new Waypoint[] {
		new Waypoint(0, 0, Pathfinder.d2r(0)),
		new Waypoint(2.0, -2.0, Pathfinder.d2r(0)),
		new Waypoint(4.0, 0.0, Pathfinder.d2r(90))
	};

	// path 5 - swerve to the left and turn right
	private static Waypoint[] path5 = new Waypoint[] {
		new Waypoint(0, 0, Pathfinder.d2r(0)),
		new Waypoint(2.0, 2.0, Pathfinder.d2r(0)),
		new Waypoint(4.0, 0.0, Pathfinder.d2r(-90))
	};

	// path 6 - swerve to the right and u-turn left
	private static Waypoint[] path6 = new Waypoint[] {
		new Waypoint(0, 0, Pathfinder.d2r(0)),
		new Waypoint(5.0, 4.0, Pathfinder.d2r(0)),
		new Waypoint(10.0, 2.0, Pathfinder.d2r(-90)),
		new Waypoint(7.5, 0.0, Pathfinder.d2r(-180))
	};

	// path 7 -  swerve to the left and u-turn right
	private static Waypoint[] path7 = new Waypoint[] {
		new Waypoint(0, 0, Pathfinder.d2r(0)),
		new Waypoint(5.0, 4.0, Pathfinder.d2r(0)),
		new Waypoint(10.0, 2.0, Pathfinder.d2r(90)),
		new Waypoint(7.5, 0.0, Pathfinder.d2r(180))
	};

	/*
	// path 8 - drive in a big circle and level out straight
	private static Waypoint[] path8 = new Waypoint[] {
		new Waypoint(0, 0, Pathfinder.d2r(0)),
		new Waypoint(5.0, -5.0, Pathfinder.d2r(0)),
		new Waypoint(10.0, 0, Pathfinder.d2r(90.0)),
		new Waypoint(5.0, 5.0, Pathfinder.d2r(180.0)),
		new Waypoint(0, 0, Pathfinder.d2r(270.0)),
		new Waypoint(10.0, 0, Pathfinder.d2r(360.0))
	};
	*/

	// trajectory creation method
	private static Trajectory setupPathfinder_Path(Waypoint[] points) {
						
		// Create the Trajectory Configuration
		//
		// Arguments:
		// Fit Method:          HERMITE_CUBIC or HERMITE_QUINTIC
		// Sample Count:        SAMPLES_HIGH (100 000)
		//		                SAMPLES_LOW  (10 000)
		//		                SAMPLES_FAST (1 000)
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, 
										Trajectory.Config.SAMPLES_HIGH, 
										PERIOD_SEC, MAX_VEL, MAX_ACCEL, MAX_JERK);

		// Generate the trajectory
		Trajectory trajectory = Pathfinder.generate(points, config);

    	/*
		for (int i = 0; i < trajectory.length(); i++) {
		    Trajectory.Segment seg = trajectory.get(i);
		    
		    System.out.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
		        seg.dt, seg.x, seg.y, seg.position, seg.velocity, 
		            seg.acceleration, seg.jerk, seg.heading);
		}	
		*/
		
		// save the trajectory to a file
		//File outFile = new File("chillOut_Path.csv");
		//Pathfinder.writeToCSV(outFile, trajectory);
		
		return trajectory;
	}

	private static void setupFollowers(Trajectory trajectory) {
    	TankModifier tankModifier = new TankModifier(trajectory).modify(WHEELBASE_WIDTH_FT);
    	
    	leftEncoderFollower = new EncoderFollower(tankModifier.getLeftTrajectory());
    	rightEncoderFollower = new EncoderFollower(tankModifier.getRightTrajectory());		

    	leftEncoderFollower.configureEncoder(0, (int)HardwareIDs.ENCODER_PULSES_PER_REV, (HardwareIDs.WHEEL_DIAMETER_INCHES/12));
    	rightEncoderFollower.configureEncoder(0, (int)HardwareIDs.ENCODER_PULSES_PER_REV, (HardwareIDs.WHEEL_DIAMETER_INCHES/12));
    	
    	leftEncoderFollower.configurePIDVA(kP, kI, kD, kV, kA);
    	rightEncoderFollower.configurePIDVA(kP, kI, kD, kV, kA);
	}
	
	public static void reset(int pathToFollow) {
		
		if (!initialized)
			initialize();
		
		m_pathToFollow = pathToFollow;
		setupFollowers(trajectoryList.get(m_pathToFollow));
	}
	
	public static void initialize() {
		
		if (initialized)
			return;
			
		// load trajectory list
		trajectoryList = new ArrayList<Trajectory>();
		trajectoryList.add(setupPathfinder_Path(path1));
		trajectoryList.add(setupPathfinder_Path(path2));
		trajectoryList.add(setupPathfinder_Path(path3));
		trajectoryList.add(setupPathfinder_Path(path4));
		trajectoryList.add(setupPathfinder_Path(path5));

		// create notifier
		m_notifier = new Notifier(m_handler);
				
		initialized = true;
	}
	
	public static void start(boolean motorPolarity) {

		DriveAssembly.setMotorForward(motorPolarity);

		m_notifier.startPeriodic(PERIOD_SEC);
	}

	public static void stop() {
		m_notifier.stop();
	}
	
	public static boolean isFinished()
	{
		return (leftEncoderFollower.isFinished() && rightEncoderFollower.isFinished());
	}
	
	public static void update() 
	{
		// retrieve encoder values from DriveAssembly
		int encoderLeft = DriveAssembly.getLeftEncoder();
		int encoderRight = DriveAssembly.getRightEncoder();
		
		// retrieve gyro value (yaw angle in degrees)
		double gyroValueDeg = NavXSensor.getAngle();
		
		// invert yaw angle sign for converting NavX yaw to Pathfinder (cartesian) yaw
		gyroValueDeg *= -1.0;
		
		double left = leftEncoderFollower.calculate(encoderLeft);
		double right = rightEncoderFollower.calculate(encoderRight);
		
        double desired_heading = Pathfinder.r2d(leftEncoderFollower.getHeading()); // Should also be in degrees

        double angleDifference = Pathfinder.boundHalfDegrees(desired_heading - gyroValueDeg);
        double turn = 0.8 * (-1.0 / 80.0) * angleDifference;

		// debug out
		InputOutputComm.putDouble(InputOutputComm.LogTable.kMainLog,"Auto/FreezyPath_Left_ft",left);
		InputOutputComm.putDouble(InputOutputComm.LogTable.kMainLog,"Auto/FreezyPath_Right_ft",right);
		InputOutputComm.putDouble(InputOutputComm.LogTable.kMainLog,"Auto/Desired_Heading_deg",desired_heading);
		InputOutputComm.putDouble(InputOutputComm.LogTable.kMainLog,"Auto/Actual_Heading_deg",gyroValueDeg);

        // send updated command to DriveAssembly
        DriveAssembly.drive(left + turn, right - turn);			
	}
}
