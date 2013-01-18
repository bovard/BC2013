package team122.behavior.lib;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import team122.communication.Communicator;
import team122.robot.Soldier;

public class SoldierSelector extends Decision {
	public Soldier robot;
	public Communicator com;

	public SoldierSelector(Soldier soldier) {
		this.robot = soldier;
		children.add(new SoldierDefenseMiner(this.robot));
		children.add(new SoldierSwarm(this.robot));
		children.add(new SoldierCombat(this.robot));
		children.get(SOLDIER_MINER).parent = this;
		children.get(SOLDIER_SWARMER).parent = this;
		children.get(COMBAT).parent = this;
		com = new Communicator(soldier.rc, soldier.info);
	}
	
	@Override
	public Node select() throws GameActionException {
		// TODO: when we have more than one child the decision code should be in here
		if(robot.enemyInMelee) {
			return children.get(COMBAT);
		}
		
		int type = SOLDIER_SWARMER;
		if(Clock.getRoundNum() < 30) {
			type = com.receive(Communicator.CHANNEL_COMMUNICATE_SOLDIER_MODE, SOLDIER_SWARMER);	
		}
		
		return children.get(type);
	}

	@Override
	public boolean pre() {
		return true;
	}

	public static final int SOLDIER_MINER = 0;
	public static final int SOLDIER_SWARMER = 1;
	public static final int COMBAT = 2;
}
