
package team122;
import battlecode.common.*;
import java.util.Random;

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
	protected Random rand;
	protected int mode;
	protected MapLocation destination;
	protected boolean has_destination = false;
	protected Direction direction = Direction.NORTH;

	//used in bug movement algorithm
	private boolean tracking = false;
	private Direction lastTargetDirection;
	private boolean trackingRight;


	/**
	 * Creates a NavigationSystem that attempts pathfinding for mobile robots
	 * Note: currently this class only requires that a robot can move, not that it has
	 * any sensors
	 * @param control the movementController
	 */
	public NavigationSystem(RobotController robotControl) {
		this.robotControl = robotControl;

		//added to make things a bit more random!
		rand = new Random(Clock.getRoundNum() * 1000);
	}



	/**
	 * Creates a navSystem with a destination already in mind
	 * @param control the MovementController
	 * @param destination the MapLocation to be the destination
	 */
	public NavigationSystem(RobotController robotControl, MapLocation dest) {
		this.robotControl = robotControl;
		this.destination = dest;
		has_destination = true;
		robotControl.setIndicatorString(2, "Dest: "+dest.toString());
	}

	/**
	 * note: this doesn't do much be the overridden version (units with blink) will
	 * @return if all movement means are active
	 */
	public boolean isActive() {
		return robotControl.isActive();
	}

	/**
	 * checks to see if the robot can move in the specified direction
	 * @param direction direction to move in
	 * @return if the bot can move in that direction
	 */
	public boolean canMove(Direction direction) {
		return robotControl.canMove(direction);
	}

	/**
	 * Sets to moveControl to move forward
	 * Note: the checks here are just as a failsafe, we'll remove them once we're sure that
	 * we are using this correctly
	 * @return if the move control was set to move forward
	 */
	public boolean setMoveForward() {
		if(robotControl.canMove(direction)) {
			try {
				robotControl.move(direction);
				return true;
			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
		}
		System.out.println("WARNING: Bad call to NavSys.setMoveForward (can't move that way!)");
		return false;
	}

	/**
	 * Stops the robot tracking and gives it a new destination
	 * @param new_dest the new MapLocation to try and move to
	 */
	public void setDestination(MapLocation new_dest) {
		destination = new_dest;
		has_destination = true;
		tracking = false;
		robotControl.setIndicatorString(2, "Dest: "+destination.toString());
	}

	/**
	 * Returns the map location that the robot is currently trying to move toward
	 * @return the MapLocation destination
	 */
	public MapLocation getDestination() {
		return destination;
	}

	/**
	 * Allows a choice of what navigation mode to use (A*, bug, flock, etc...)
	 * Note: currently only bug is implemented
	 * @param mode The navigation mode taken from NavigationMode.java
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * Checks to see if the robot is currently at it's destination
	 * @return if the robot is at its destination MapLocation
	 */
	public boolean isAtDestination() {
		return robotControl.getLocation().equals(destination);
	}

}