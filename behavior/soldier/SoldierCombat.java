package team122.behavior.soldier;

import team122.behavior.Behavior;
import team122.communication.Communicator;
import team122.robot.Soldier;
import battlecode.common.GameActionException;

public class SoldierCombat 
		extends Behavior {
	protected Soldier robot;
	
	
	public SoldierCombat(Soldier robot) {
		super();
		this.robot = robot;

	}
	
	@Override
	public void start() {
		robot.incChannel = Communicator.CHANNEL_SOLDIER_COUNT;
	}

	@Override
	public void run() throws GameActionException {
		if (!robot.rc.isActive())
			return;
		robot.mCalc.move(robot.meleeObjects, robot.currentLoc);
	}

	@Override
	public boolean pre() {
		return robot.enemyInMelee;
	}

}
