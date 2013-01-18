package team122.navigation;

import team122.RobotInformation;
import battlecode.common.Direction;
import battlecode.common.RobotController;

public class BugMode extends NavigationMode {

	public BugMode(RobotController rc, RobotInformation info) {
		super(rc, info);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void move() {
		// TODO Auto-generated method stub
		
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
//	    try {
//	      Direction currentDirection = direction;
//	      if (robotControl.getLocation().equals(destination)) {
//	        //System.out.println("DESTINATION REACHED!!");
//	        has_destination = false;
//	        robotControl.setIndicatorString(2, "No Dest");
//	        System.out.println("WARNING: Bad call to NavSys.setNextMove ->bug (no dest or dest reached)");
//	        return false;
//	      }
//	      //if we're currently tracking
//	      else if (tracking) {
//	        //System.out.println("Tracking");
//
//	        //check to see if we can move in the direction we were last blocked in and we're
//	        //off the obstacle
//	        if (robotControl.canMove(lastTargetDirection) && robotControl.canMove(lastTargetDirection.rotateLeft())
//	                && robotControl.canMove(lastTargetDirection.rotateRight())) {
//	          //System.out.println("Done Tracking!");
//	          tracking = false;
//	          direction = lastTargetDirection;
//	          return true;
//	        }
//	        else {
//	          //System.out.println("Continuing to Track... moving");
//	          if (trackingRight)
//	            if (robotControl.canMove(currentDirection.rotateLeft())) {
//	              direction = direction.rotateLeft();
//	              return true;
//	            }
//	            else if (robotControl.canMove(currentDirection)) {
//	              robotControl.move(direction);
//	              return true;
//	            }
//	            else {
//	              direction = direction.rotateRight();
//	              return true;
//	            }
//	          else if (!trackingRight)
//	            if (robotControl.canMove(currentDirection.rotateRight())) {
//	              direction = direction.rotateRight();
//	              return true;
//	            }
//	            else if (robotControl.canMove(currentDirection)) {
//	              robotControl.move(direction);
//	              return true;
//	            }
//	            else {
//	              direction = direction.rotateLeft();
//	              return true;
//	            }
//	        }
//	      }
//
//
//	      else if (!tracking) {
//	        //System.out.println("Not tracking... moving");
//	        lastTargetDirection = robotControl.getLocation().directionTo(destination);
//	        //if you can move toward the target and you're facing that way move foward
//	        if (robotControl.canMove(lastTargetDirection) && lastTargetDirection == currentDirection) {
//	          robotControl.move(direction);
//	          return true;
//	        }
//	        //if you can move toward the target but you aren't facing the right way, rotate
//	        else if (robotControl.canMove(lastTargetDirection)) {
//	          direction = lastTargetDirection;
//	          return true;
//	        }
//	        //otherwise if you can't move toward the target you need to start tracking!
//	        else {
//	          //System.out.println("Need to start tracking!");
//	          tracking = true;
//	          //choose a direction to track in (by making it random we can avoid (some) loops
//	          //TODO: Change this to favor the direction that would require the least turning
//	          //to continue in (so when hitting an object at an angle they would continue
//
//	          //if we can rotate slightly left and/or right
//	          if (robotControl.canMove(direction.rotateRight())
//	                  || robotControl.canMove(direction.rotateLeft()) && rand.nextInt(10) < 8)
//	          {
//	            //if canMove right && (random or can't move Left)
//	            if (robotControl.canMove(direction.rotateRight())
//	                    && (rand.nextBoolean() || !robotControl.canMove(direction.rotateLeft()))) {
//	              trackingRight = true;
//	            }
//	            else {
//	              trackingRight = false;
//	            }
//	          }
//	          else {
//	            trackingRight = rand.nextBoolean();
//	          }
//	          //TODO: do we need to make this pass-by-value?
//	          Direction toMove = lastTargetDirection;
//	          //a count prevents the robot from turning in circles forever
//	          int count = 8;
//	          while(!robotControl.canMove(toMove) && count > 0) {
//	            if (trackingRight)
//	              toMove = toMove.rotateRight();
//	            else
//	              toMove = toMove.rotateLeft();
//	            count--;
//	          }
//	          //System.out.println("Changing to Direction "+toMove.name()+" and count="+count);
//	          direction = toMove;
//	          return true;
//	        }
//	      }
//	    } catch (Exception e) {
//	      System.out.println("caught exception:");
//	      e.printStackTrace();
//	    }
//	    System.out.println("WARNING: Bad call to NavSys.setNextMove -> bug (unknown)");
	    return false;
	  }

}
