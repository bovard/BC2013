package team122.navigation;

import battlecode.common.*;

public class CityBlockMode extends NavigationMode {

	public CityBlockMode(RobotController rc) {
		super(rc);
	}

	@Override
	public boolean run() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @throws GameActionException 
	 * @see NavigationMode
	 */
	@Override
	public void runWithLimit(int limit) throws GameActionException {
		if (!hasDestination) {
			return;
		}
		if (atDestination) {
			return;
		}
		
		MapLocation currentLoc;
		Direction dirToGo;
		Team team;
		
		//Goes through the limit and processes the city block
		for (;limit > 0;limit--) {
			currentLoc = rc.getLocation();
			dirToGo = currentLoc.directionTo(destination);
			
			if (dirToGo == Direction.OMNI || dirToGo == Direction.NONE) {
				hasDestination = false;
				atDestination = true;
				return;
			}
			
			if (rc.isActive()) {
				if (rc.canMove(dirToGo)) {
					if (_moveWithDefusing(dirToGo)) {
						break;
					}
					
					if (currentLoc.equals(destination)) {
						hasDestination = false;
						atDestination = true;
						return;
					}
				} else if (currentLoc.distanceSquaredTo(destination) == 1) {
					hasDestination = false;
					atDestination = true;
					return;
				} else {
					
					//We need to perform a simplified bug algorithm.  We add in some intentional
					//error to be able to hopefully get around this obstacle.
					Direction dir = Direction.values()[(int)(Math.random() * 8)];
					if (_moveWithDefusing(dir)) {
						break;
					}
				}
			}
		}
		
	} // run with limit.
	
	/**
	 * moves with defusing mines.
	 * @param dir
	 * @return if the robot is defusing or not.
	 * @throws GameActionException
	 */
	private boolean _moveWithDefusing(Direction dir) 
		throws GameActionException {
		boolean defusing = false;
		MapLocation nextLoc = rc.getLocation().add(dir);
		Team team = rc.senseMine(nextLoc);
		
		if (team != null && team == Team.NEUTRAL) {
			rc.defuseMine(nextLoc);
			defusing = true;
		} else {
			
			//We need to refactor this canMove because it performs this 2x for the logic above.
			if (rc.canMove(dir)) {
				rc.move(dir);
			}
		}
		
		return defusing;
	}
}
