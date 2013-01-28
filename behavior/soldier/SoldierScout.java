package team122.behavior.soldier;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team122.behavior.Behavior;
import team122.behavior.IComBehavior;
import team122.communication.Communicator;
import team122.navigation.SoldierMove;
import team122.robot.HQ;
import team122.robot.Soldier;

public class SoldierScout  
		extends Behavior {
	
	public Soldier robot;
	public boolean init = false;
	
	public SoldierScout(Soldier robot) {
		this.robot = robot;
	}
	
	/**
	 * Sets the spawning point.
	 */
	@Override
	public void start() { 
		
		//sets the starting spawn point.
		if (!init) {
			robot.incChannel = Communicator.CHANNEL_SOLDIER_COUNT;
			robot.move.destination = SoldierSelector.GetInitialRallyPoint(robot.info);
			init = true;
		} 
	}

	@Override
	public void run() throws GameActionException {

		if (Clock.getRoundNum() % HQ.HQ_COMMUNICATION_ROUND == 0 && robot.com.shouldAttack()) {
			robot.move.destination = robot.info.enemyHq;
		}
		
		robot.move.move();
	}
	
	@Override
	public boolean pre() {
		boolean goHome = false;
		if (robot.enemyAtTheGates) {
			goHome = robot.currentLoc.distanceSquaredTo(robot.info.hq) - robot.currentLoc.distanceSquaredTo(robot.info.enemyHq) < 0;
		}
		
		return !robot.enemyInMelee && !robot.isNukeArmed && !goHome;
	}
	

}
