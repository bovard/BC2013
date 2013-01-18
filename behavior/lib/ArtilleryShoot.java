package team122.behavior.lib;

import team122.robot.Artillery;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.RobotType;

public class ArtilleryShoot extends Behavior{
	
	protected Artillery robot;
	
	public ArtilleryShoot(Artillery robot) {
		super();
		this.robot = robot;
	}

	@Override
	public void run() throws GameActionException {
		if (robot.rc.isActive()) {
			GameObject[] robots = robot.rc.senseNearbyGameObjects(GameObject.class, RobotType.ARTILLERY.attackRadiusMaxSquared, robot.info.enemyTeam);
			
			if (robots.length > 0) {
				robot.rc.attackSquare(robot.rc.senseLocationOf(robots[0]));
			}
		} // end is active.
		
	}

	@Override
	public boolean pre() {
		return robot.canShoot && robot.enemyNearby;
	}

}
