package team122.behavior.soldier;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import team122.behavior.Behavior;
import team122.behavior.IComBehavior;
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
		
		//TODO: We need to turn this into an actual smart soldier.
		
		if (!robot.navSystem.navMode.hasDestination && !robot.navSystem.navMode.atDestination) {
			robot.navSystem.setInitialSwarmRallyPoint();
		}
		
		if (Clock.getRoundNum() % 400 == 0) {
			robot.navSystem.navMode.setDestination(robot.info.enemyHq);
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
		return !robot.enemyInMelee;
	}

}
