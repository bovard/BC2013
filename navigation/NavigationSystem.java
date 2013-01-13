
package team122.navigation;
import battlecode.common.*;

import java.util.HashMap;
import java.util.Random;

import team122.AStar;

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


    protected RobotController robotControl;

    //class variables
    protected Random rand = new Random();
    protected int mode;
    public NavigationMode navMode;
	public HashMap<MapLocation, Boolean> alliedEncampments;


    /**
     * Creates a NavigationSystem that attempts pathfinding for mobile robots
     * Note: currently this class only requires that a robot can move, not that it has
     * any sensors
     * @param control the movementController
     */
    public NavigationSystem(RobotController robotControl) {
    	this.robotControl = robotControl;

    	//added to make things a bit more random!
    	rand.setSeed(Clock.getRoundNum());
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
	  			navMode = new AStarMode(robotControl);
	  			break;
		  	case NavigationMode.CITY_BLOCK:
		  		navMode = new CityBlockMode(robotControl);
		  		break;
    	}
    }

	/**
	 * Gets the nearest unused encampment.
	 * @return
	 */
	public void setNearestEncampmentAsDestination() {
		MapLocation[] encampments = robotControl.senseAllEncampmentSquares();
		MapLocation closest = null;
		MapLocation location = robotControl.getLocation();
		int closestDistance = 0;
		int currDistance = 0;
		
		for (MapLocation encampment : encampments) {
			
			//The first one is always the closest.
			if (closest == null) {
				closest = encampment;
				closestDistance = location.distanceSquaredTo(closest);
			} else {
				
				//Next the distances need to be compared.
				currDistance = location.distanceSquaredTo(encampment);
				if (currDistance < closestDistance && 
						!alliedEncampments.containsKey(encampment)) {
					
					closestDistance = currDistance;
					closest = encampment;
				}
			}
		}
		
		navMode.setDestination(closest);
	}
}