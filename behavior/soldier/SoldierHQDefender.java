package team122.behavior.soldier;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team122.behavior.Behavior;
import team122.communication.Communicator;
import team122.navigation.SoldierMove;
import team122.robot.Soldier;

public class SoldierHQDefender  
		extends Behavior {
	
	public Soldier robot;
	public SoldierMove move;
	
	public SoldierHQDefender(Soldier robot) {
		this.robot = robot;
		move = new SoldierMove(robot);
		move.destination = robot.info.hq;
	}
	
	@Override
	public void start() { 
		robot.incChannel = Communicator.CHANNEL_SOLDIER_COUNT;
	}

	@Override
	public void run() throws GameActionException {
		
		//TODO: We need to turn this into an actual smart soldier.
		move.move();
	}
	
	@Override
	public boolean pre() {
		return !robot.enemyInMelee && !robot.isNukeArmed && robot.enemyAtTheGates;
	}
}
