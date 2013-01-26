package team122.behavior.soldier;

import team122.behavior.Behavior;
import team122.behavior.IComBehavior;
import team122.communication.Communicator;
import team122.robot.Soldier;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;

public class SoldierCombat 
		extends Behavior
		implements IComBehavior {
	protected Soldier robot;
	
	
	public SoldierCombat(Soldier robot) {
		super();
		this.robot = robot;

	}

	/**
	 * echos the com behavior to the communicator.
	 */
	@Override
	public void comBehavior() throws GameActionException {
		robot.com.increment(Communicator.CHANNEL_SOLDIER_COUNT, Clock.getRoundNum() + 1);
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
