package team122.behavior.soldier;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team122.behavior.Behavior;
import team122.behavior.IComBehavior;
import team122.communication.Communicator;
import team122.navigation.SoldierMove;
import team122.robot.Soldier;

public class SoldierSwarm  
		extends Behavior {
	
	public Soldier robot;
	
	public SoldierSwarm(Soldier robot) {
		this.robot = robot;
	}
	
	@Override
	public void start() { 
		robot.incChannel = Communicator.CHANNEL_SOLDIER_COUNT;
	}

	@Override
	public void run() throws GameActionException {
		
		//TODO: We need to turn this into an actual smart soldier.
		if (robot.move.destination == null) {
			Direction dir = robot.info.enemyDir;
			robot.move.destination = robot.info.hq
					.add(dir).add(dir).add(dir).add(dir).add(dir).add(dir).add(dir).add(dir);
		}
		
//		int modifier = Clock.getRoundNum() / 400 + 1;
//		if (Clock.getRoundNum() % (modifier * 150) == 0 || 
//				(modifier > 400 && Clock.getRoundNum() % 400 == 0)) {
		if (Clock.getRoundNum() % 200 == 0) {
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
