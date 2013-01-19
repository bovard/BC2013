package team122.behavior.lib;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import team122.communication.Communicator;
import team122.robot.Soldier;

public class SoldierSwarm  
		extends Behavior
		implements IComBehavior {
	
	public Soldier robot;
	
	public SoldierSwarm(Soldier robot) {
		this.robot = robot;
	}

	@Override
	public void run() throws GameActionException {
		if (!robot.navSystem.navMode.hasDestination && !robot.navSystem.navMode.atDestination) {
			robot.navSystem.setInitialSwarmRallyPoint();
		}
		
		if (Clock.getRoundNum() % 400 == 0) {
			robot.navSystem.setToEnemyHQ();
		} else {
			robot.navSystem.navMode.move();
		}
	}

	@Override
	public void comBehavior() throws GameActionException {
		robot.com.increment(Communicator.CHANNEL_SOLDIER_COUNT);
	}
	
	@Override
	public boolean pre() {
		return true;
	}

}
