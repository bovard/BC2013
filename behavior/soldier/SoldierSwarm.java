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
		
		int round = Clock.getRoundNum();
		int modifier = round/400 + 1;
		if (Clock.getRoundNum() % (modifier*150) >= (modifier*125)) {
			robot.move.destination = robot.info.enemyHq;
		}
		robot.move.move();
	}
	
	@Override
	public boolean pre() {
		return !robot.enemyInMelee;
	}

}
