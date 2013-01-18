package team122.navigation;

import team122.RobotInformation;
import battlecode.common.*;

public class CityBlockMode extends MoveWithDefuseMode {

	public CityBlockMode(RobotController rc, RobotInformation info) {
		super(rc, info);
	}

	/**
	 * @throws GameActionException
	 * @see NavigationMode
	 */
	@Override
	public void move() throws GameActionException {
		if (!hasDestination) {
			return;
		}
		if (atDestination) {
			return;
		}

		MapLocation currentLoc;
		Direction dirToGo;
		Team team;

		// Goes through the limit and processes the city block
		destinationTries++;
		currentLoc = rc.getLocation();
		dirToGo = currentLoc.directionTo(destination);

		if (dirToGo == Direction.OMNI || dirToGo == Direction.NONE) {
			hasDestination = false;
			atDestination = true;
			return;
		}

		if (rc.isActive()) {
			if (rc.canMove(dirToGo)) {
				if (moveWithDefusing(dirToGo)) {
					return;
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

				// We need to perform a simplified bug algorithm. We add in some
				// intentional
				// error to be able to hopefully get around this obstacle.
				Direction dir = Direction.values()[(int) (Math.random() * 8)];
				moveWithDefusing(dir);
			}
		}

	} // run with limit.
}
