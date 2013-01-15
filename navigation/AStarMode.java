package team122.navigation;

import team122.RobotInformation;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class AStarMode extends NavigationMode {

	/**
	 * creates a new a star
	 * @param rc
	 */
	public AStarMode(RobotController rc, RobotInformation info) {
		super(rc, info);
	}
	
	/**
	 * @see NavigationMode
	 */
	public void move() { }
	
	/**
	 * sets the destination. if its possible.
	 */
	@Override
	public boolean setDestination(MapLocation location) {
		super.setDestination(location);
		
		return true;
	}
}
