package team122.navigation;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class AStarMode extends NavigationMode {

	/**
	 * creates a new a star
	 * @param rc
	 */
	public AStarMode(RobotController rc) {
		super(rc);
	}

	/**
	 * Runs the navigation algorithm to the destination.
	 */
	public boolean run() {	
		return false;
	}
	
	/**
	 * @see NavigationMode
	 */
	public void runWithLimit(int limit) { }
	
	/**
	 * sets the destination. if its possible.
	 */
	@Override
	public boolean setDestination(MapLocation location) {
		super.setDestination(location);
		
		
		
		return true;
	}
}
