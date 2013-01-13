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
	public void runWithLimit(int limit) {
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
					try {
						MapLocation nextLoc = rc.getLocation().add(dirToGo);
						team = rc.senseMine(nextLoc);
						
						System.out.println("Sensing Mine: " + team);
						
						if (team != null && team == Team.NEUTRAL) {
							System.out.println("Defusing Mine");
							rc.defuseMine(nextLoc);
							break;
						} else {
							rc.move(dirToGo);
						}
						
						if (currentLoc.equals(destination)) {
							hasDestination = false;
							atDestination = true;
							return;
						}
					} catch (GameActionException e) {
						e.printStackTrace();
					}
				} else if (currentLoc.distanceSquaredTo(destination) == 1) {
					hasDestination = false;
					atDestination = true;
				}
			}
		}
		
	} // run with limit.
}
