
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
	protected Random rand = new Random();
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
		rand.setSeed(Clock.getRoundNum());
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
	 * Sets to moveControl to move backward
	 * Note: the checks here are just as a failsafe, we'll remove them once we're sure that
	 * we are using this correctly
	 * @return if the move control was set to move backward
	 */
	public boolean setMoveBackward() {
		if(robotControl.canMove(direction.opposite())) {
			robotControl.move(direction.opposite());
			return true;
		}
		System.out.println("WARNING: Bad call to NavSys.setMoveBackward (can't move there!)");
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

	/**
	 * setNextMove is called to choose and execute the next for a bot.
	 * @return if the moveController was 'set' (given a command)
	 */
	public boolean setNextMove() {
		if(has_destination && !robotControl.isActive()) {
			try {
				return bug();  
			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
		}
		//if the moveController is active or we don't have a destination return false
		System.out.println("WARNING: Bad call to NavSys.setNextMove");
		return false;
	}


	/**
	 * bug runs the bug algorithm. Currently the bug chooses a random direction to trace
	 * and can remember the original direction it needed to go to get the target
	 * TODO: add a break for when we've been tracing too long
	 * TODO: be smarter about what direction to choose to trace
	 * Note: currently the bug will fall off of convex curves but will hug concave
	 * @return if the moveController was set
	 */
	protected boolean bug() {
		Direction currentDirection = direction;
		if (robotControl.getLocation().equals(destination)) {
			//System.out.println("DESTINATION REACHED!!");
			has_destination = false;
			robotControl.setIndicatorString(2, "No Dest");
			System.out.println("WARNING: Bad call to NavSys.setNextMove ->bug (no dest or dest reached)");
			return false;
		}
		//if we're currently tracking
		else if (tracking) {
			//System.out.println("Tracking");

			//check to see if we can move in the direction we were last blocked in and we're
			//off the obstacle
			if (robotControl.canMove(lastTargetDirection) && robotControl.canMove(lastTargetDirection.rotateLeft())
					&& robotControl.canMove(lastTargetDirection.rotateRight())) {
				//System.out.println("Done Tracking!");
				tracking = false;
				direction = lastTargetDirection;
				return true;
			}
			else {
				//System.out.println("Continuing to Track... moving");
				if (trackingRight)
					if (robotControl.canMove(currentDirection.rotateLeft())) {
						direction = direction.rotateLeft();
						return true;
					}
					else if (robotControl.canMove(currentDirection)) {
						robotControl.move(direction);
						return true;
					}
					else {
						direction = direction.rotateRight();
						return true;
					}
				else if (!trackingRight)
					if (robotControl.canMove(currentDirection.rotateRight())) {
						direction = direction.rotateRight();
						return true;
					}
					else if (robotControl.canMove(currentDirection)) {
						robotControl.move(direction);
						return true;
					}
					else {
						direction = direction.rotateLeft();
						return true;
					}
			}
		}


		else if (!tracking) {
			//System.out.println("Not tracking... moving");
			lastTargetDirection = robotControl.getLocation().directionTo(destination);
			//if you can move toward the target and you're facing that way move foward
			if (robotControl.canMove(lastTargetDirection) && lastTargetDirection == currentDirection) {
				robotControl.move(direction);
				return true;
			}
			//if you can move toward the target but you aren't facing the right way, rotate
			else if (robotControl.canMove(lastTargetDirection)) {
				direction = lastTargetDirection;
				return true;
			}
			//otherwise if you can't move toward the target you need to start tracking!
			else {
				//System.out.println("Need to start tracking!");
				tracking = true;
				//choose a direction to track in (by making it random we can avoid (some) loops
				//TODO: Change this to favor the direction that would require the least turning
				//to continue in (so when hitting an object at an angle they would continue

				//if we can rotate slightly left and/or right
				if (robotControl.canMove(direction.rotateRight())
						|| robotControl.canMove(direction.rotateLeft()) && rand.nextInt(10) < 8)
				{
					//if canMove right && (random or can't move Left)
					if (robotControl.canMove(direction.rotateRight())
							&& (rand.nextBoolean() || !robotControl.canMove(direction.rotateLeft()))) {
						trackingRight = true;
					}
					else {
						trackingRight = false;
					}
				}
				else {
					trackingRight = rand.nextBoolean();
				}
				//TODO: do we need to make this pass-by-value?
				Direction toMove = lastTargetDirection;
				//a count prevents the robot from turning in circles forever
				int count = 8;
				while(!robotControl.canMove(toMove) && count > 0) {
					if (trackingRight)
						toMove = toMove.rotateRight();
					else
						toMove = toMove.rotateLeft();
					count--;
				}
				//System.out.println("Changing to Direction "+toMove.name()+" and count="+count);
				direction = toMove;
				return true;
			}
		}
		System.out.println("WARNING: Bad call to NavSys.setNextMove -> bug (unknown)");
		return false;
	}

}