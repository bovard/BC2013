package team122.behavior.artillery;

import team122.behavior.Behavior;
import team122.robot.Artillery;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public class ArtilleryIdle extends Behavior {
	
	protected Artillery robot;

	public ArtilleryIdle(Artillery robot) {
		super();
		this.robot = robot;
	}
	
	@Override
	public void run() throws GameActionException {
		
		if (robot.canShoot && robot.rc.getLocation().distanceSquaredTo(robot.info.enemyHq) < RobotType.ARTILLERY.attackRadiusMaxSquared) {
			robot.rc.attackSquare(robot.info.enemyHq);
			return;
		}
				
	}

	@Override
	public boolean pre() {
		return !robot.canShoot || !robot.enemyNearby;
	}

}
