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
		extends Behavior
		implements IComBehavior {
	
	public Soldier robot;
	private SoldierMove move;
	
	public SoldierSwarm(Soldier robot) {
		this.robot = robot;
		move = new SoldierMove(robot);
	}

	@Override
	public void run() throws GameActionException {
		
		//TODO: We need to turn this into an actual smart soldier.
		
		if (move.destination == null) {
			Direction dir = robot.info.enemyDir;
			move.destination = robot.info.hq
					.add(dir).add(dir).add(dir).add(dir).add(dir).add(dir);
		}
		
		if (Clock.getRoundNum() % 400 >= 300) {
			move.destination = robot.info.enemyHq;
		}
		move.move();
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
