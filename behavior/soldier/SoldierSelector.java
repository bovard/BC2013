package team122.behavior.soldier;

import battlecode.common.GameActionException;
import team122.behavior.Behavior;
import team122.behavior.Decision;
import team122.behavior.Node;
import team122.communication.Communicator;
import team122.robot.Soldier;

public class SoldierSelector extends Decision {
	public Soldier robot;

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
			int data = robot.com.receive(Communicator.CHANNEL_NEW_SOLDIER_MODE, SOLDIER_SWARMER);
			
			robot.initialMode = data % 10;
			robot.initialData = data - robot.initialMode;
			robot.isNew = false;
			
			Behavior behavior = (Behavior)children.get(robot.initialMode);
			behavior.setInitialData(robot.initialData);
			
			return behavior;
		}		

		return children.get(robot.initialMode);
	}

	@Override
	public boolean pre() {
		return true;
	}
	
	/**
	 * Builds encampment data.  NO way to screw this up :)
	 * @param encampmentType
	 * @param encampmentToTry
	 * @return
	 */
	public static int setEncamperData(int encampmentType, int encampmentToTry) {
		return encampmentToTry * 100 + encampmentType + SOLDIER_ENCAMPER;
	}

	public static final int SOLDIER_MINER = 0;
	public static final int SOLDIER_SWARMER = 1;
	public static final int SOLDIER_ENCAMPER = 2;
	
	public static final int GENERATOR_ENCAMPER = 10;
}