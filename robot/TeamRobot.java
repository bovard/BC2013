package team122.robot;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import team122.RobotInformation;
import team122.communication.Communicator;
import team122.trees.Tree;

/**
 * This is the Robot Class, you'll need to extend it
 * 
 * Every Robot should have a 
 * 
 * @author bovard.tiberi
 *
 */
public abstract class TeamRobot {
	
	protected Tree tree;
	public RobotController rc;
	public RobotInformation info;
	public Communicator com;
	
	public TeamRobot(RobotController rc, RobotInformation info) {
		this.rc = rc;
		this.info = info;
		com = new Communicator(rc);
	}
	
	/**
	 * This will check the environment around the robot, check for messages
	 * check to see if enemies are near. 
	 * 
	 */
	public abstract void environmentCheck() throws GameActionException;
	
	/**
	 * Called at the end of a robots turn, can load things...
	 */
	public void load() throws GameActionException {};
	
	/**
	 * We should never return from this 
	 */
	public void run() {
		tree.run();
	}
}
