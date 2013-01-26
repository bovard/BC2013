package team122.behavior.artillery;

import team122.behavior.Behavior;
import team122.combat.ArtilleryShotCalculator;
import team122.robot.Artillery;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.RobotType;

public class ArtilleryShoot extends Behavior{
	
	protected Artillery robot;
	protected ArtilleryShotCalculator shotCalc;
	
	public ArtilleryShoot(Artillery robot) {
		super();
		this.robot = robot;
		shotCalc = new ArtilleryShotCalculator(robot);
	}

	@Override
	public void run() throws GameActionException {
		
		if (robot.rc.getLocation().distanceSquaredTo(robot.info.enemyHq) < RobotType.ARTILLERY.attackRadiusMaxSquared) {
			robot.rc.attackSquare(robot.info.enemyHq);
			return;
		}
		
		shotCalc.shoot();
	}

	@Override
	public boolean pre() {
		return robot.canShoot && robot.enemyNearby;
	}

}
