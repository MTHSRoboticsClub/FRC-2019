package frc.StateMachine;

import frc.ChillySwerve.ChillySwerve;
import frc.NetworkComm.InputOutputComm;
import frc.Systems.FreezyPath;
import frc.Systems.NavXSensor;

public class FollowPathAction extends Action {

  private String name;
  private int pathToFollow;
  private boolean fwdPolarity;

  public FollowPathAction(int pathToFollow, boolean polarity) {
    this.name = "<Follow Path Action>";
    this.pathToFollow = pathToFollow;
    this.fwdPolarity = polarity;

    ChillySwerve.initialize();
    NavXSensor.initialize();
    InputOutputComm.initialize();
    FreezyPath.initialize();
    FreezyPath.reset(pathToFollow);
  }

  public FollowPathAction(String name, int pathToFollow, boolean polarity) {
    this.name = name;
    this.pathToFollow = pathToFollow;
    this.fwdPolarity = polarity;

    ChillySwerve.initialize();
    NavXSensor.initialize();
    InputOutputComm.initialize();
    FreezyPath.initialize();
    FreezyPath.reset(pathToFollow);
  }

  // action entry
  public void initialize() {
    // do some drivey initialization

    ChillySwerve.autoInit(true, 0.0, false);

		// set up path and followers
		FreezyPath.reset(pathToFollow);

		// start the pathfinder thread
    FreezyPath.start(fwdPolarity);

    super.initialize();
  }

  // called periodically
  public void process() {

    // no action needed (Path Following is self-running)

    super.process();
  }

  // state cleanup and exit
  public void cleanup() {
    // do some drivey cleanup

    FreezyPath.stop();
    ChillySwerve.autoStop();

    // cleanup base class
    super.cleanup();
  }
}
