package team122.behavior.lib;

import battlecode.common.GameActionException;
import team122.communication.Communicator;
import team122.robot.Soldier;

public class SoldierSelector extends Decision {
	public Soldier robot;
	public int initialMode;

	public SoldierSelector(Soldier soldier) {
		this.robot = soldier;
		children.add(new SoldierDefenseMiner(this.robot));
		children.add(new SoldierSwarm(this.robot));
		children.add(new SoldierEncamper(this.robot));
		children.get(SOLDIER_MINER).parent = this;
		children.get(SOLDIER_SWARMER).parent = this;
		children.get(SOLDIER_ENCAMPER).parent = this;
	}
	
	@Override
	public Node select() throws GameActionException {
		// TODO: when we have more than one child the decision code should be in here
		
		if (robot.isNew) {
			initialMode = robot.com.receive(Communicator.CHANNEL_NEW_SOLDIER_MODE, SOLDIER_SWARMER);
			robot.isNew = false;
			return children.get(initialMode);
		}		

		return children.get(initialMode);
	}

	@Override
	public boolean pre() {
		return true;
	}

	public static final int SOLDIER_MINER = 0;
	public static final int SOLDIER_SWARMER = 1;
	public static final int SOLDIER_ENCAMPER = 2;
}
