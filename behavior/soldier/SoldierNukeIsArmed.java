package team122.behavior.soldier;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import team122.behavior.Behavior;
import team122.communication.Communicator;
import team122.robot.Soldier;

public class SoldierNukeIsArmed  
		extends Behavior {
	
	public Soldier robot;
	private boolean init = false;
	
	public SoldierNukeIsArmed(Soldier robot) {
		this.robot = robot;
	}
	
	@Override
	public void start() { 
		if (!init) {
			init = true;
			robot.incChannel = Communicator.CHANNEL_SOLDIER_COUNT;
			robot.move.destination = SoldierSelector.GetInitialRallyPoint(robot.info);
		}
		
	}

	@Override
	public void run() throws GameActionException {

		if (Clock.getRoundNum() % 75 == 0) {
			robot.move.destination = robot.info.enemyHq;
		}
		
		robot.move.move();
	}
	
	@Override
	public boolean pre() {
		return !robot.enemyInMelee;
	}

}