package team122.navigation;

import team122.RobotInformation;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public abstract class MoveWithDefuseMode extends NavigationMode {
	public boolean defuseEnemyMine = false;
	public MapLocation enemyMineLocation = null;

	public MoveWithDefuseMode(RobotController rc, RobotInformation info) {
		super(rc, info);
		// TODO Auto-generated constructor stub
	}

	/**
	 * moves with defusing mines.
	 * @param dir
	 * @return if the robot is defusing or not.
	 * @throws GameActionException
	 */
	public boolean moveWithDefusing(Direction dir) throws GameActionException {
		boolean defusing = false;
		MapLocation nextLoc = rc.getLocation().add(dir);
		Team team = rc.senseMine(nextLoc);
		
		if (defuseEnemyMine) {
			if (enemyMineLocation.equals(rc.getLocation())) {
				
				//TODO: This needs to have a refactor for better information organization.
				//We need to have a robot info class that gets around to removing sensing
				//Enemy HQ / Our HQ
				rc.move(enemyMineLocation.directionTo(rc.senseHQLocation()));
			} else {
				defuseEnemyMine = false;
				rc.defuseMine(enemyMineLocation);
			}
		} else if (team != null && team == Team.NEUTRAL) {
			rc.defuseMine(nextLoc);
			defusing = true;
		} else {
			
			//We need to refactor this canMove because it performs this 2x for the logic above.
			if (rc.canMove(dir)) {
				rc.move(dir);
				

				Team possibleEnemy = rc.senseMine(rc.getLocation());
				if (possibleEnemy == info.enemyTeam) {
					defuseEnemyMine = true;
					enemyMineLocation = rc.getLocation();
				}
			}
		}
		
		return defusing;
	}
}
