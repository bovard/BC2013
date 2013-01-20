
package team122.navigation;
import battlecode.common.*;

import java.util.HashMap;
import team122.RobotInformation;
import team122.robot.TeamRobot;

/**
 * The NavigationSystem takes care of selecting a move for the robot
 * The NavigationSystem needs to be given a MovementController to issue commands to, it
 * will check to see if the MovementController is idle before issuing any commands
 *
 * Note: this class shouldn't be doing any checking to see if it actually can set the
 * movementcontroller, but it still does. it will print a warning if it can't
 *
 * Note: this class should only be used when trying to navigate somewhere. Fleeing, combat
 * movement
 *
 * @see SensorNavigationSystem
 * @see JumpNavigationSystem
 * @author bovard
 */
public class NavigationSystem {


    public RobotController rc;
    public RobotInformation info;

    //class variables
    protected int mode;
    public NavigationMode navMode;
	public HashMap<MapLocation, Boolean> alliedEncampments;


    /**
     * Creates a NavigationSystem that attempts pathfinding for mobile robots
     * Note: currently this class only requires that a robot can move, not that it has
     * any sensors
     * @param control the movementController
     */
    public NavigationSystem(RobotController rc, RobotInformation info) {
    	this.rc = rc;
    	this.info = info;
    	
    	//added to make things a bit more random!
    	setNavigationMode(NavigationMode.CITY_BLOCK);
		alliedEncampments = new HashMap<MapLocation, Boolean>();
    }
  
    /**
     * sets the navigation mode
     * @param mode
     */
    public void setNavigationMode(int mode) {
    	switch (mode) {
	  		case NavigationMode.ASTAR_MODE:
	  			navMode = new AStarMode(rc, info);
	  			break;
		  	case NavigationMode.CITY_BLOCK:
		  		navMode = new CityBlockMode(rc, info);
		  		break;
    	}
    }
	
	/**
	 * Sets the rally point that is in the direction of the enemy castle.
	 */
	public void setInitialSwarmRallyPoint() {
		Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		
		MapLocation rallyPoint = rc.senseHQLocation()
				.add(dir).add(dir).add(dir).add(dir).add(dir).add(dir);
		navMode.setDestination(rallyPoint);
	}
	
	/**
	 * Sets destination to enemy hq
	 */
	public void setToEnemyHQ() {
		navMode.setDestination(rc.senseEnemyHQLocation());
	}
	
	public static MapLocation BoundToBoard(TeamRobot robot, MapLocation loc) {
		if (robot.rc.senseTerrainTile(loc) == TerrainTile.OFF_MAP) {
			int newX = loc.x, newY = loc.y;
			
			if (newX < 0) {
				newX = 0;
			} else if (newX >= robot.info.width) {
				newX = robot.info.width - 1;
			}
			
			if (newY < 0) {
				newY = 0;
			} else if (newY >= robot.info.height) {
				newY = robot.info.height - 1;
			}
			
			return new MapLocation(newX, newY);
		}
		return loc;
	}
}