package team122.behavior.lib;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import team122.robot.Soldier;

public class SoldierSwarm extends Behavior{
	
	public Soldier robot;
	
	public SoldierSwarm(Soldier robot) {
		this.robot = robot;
	}

	@Override
	public void run() throws GameActionException {
		if (!robot.navMode.hasDestination && !robot.navMode.atDestination) {
			robot.navSystem.setInitialSwarmRallyPoint();
		}
		
		if (Clock.getRoundNum() % 100 == 0) {
			robot.navSystem.setToEnemyHQ();
		} else {
			robot.navMode.move();
		}
	}

	@Override
	public boolean pre() {
		return !robot.enemyInMelee;
	}

}
