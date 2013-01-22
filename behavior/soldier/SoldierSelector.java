package team122.behavior.soldier;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team122.behavior.Behavior;
import team122.behavior.Decision;
import team122.behavior.Node;
import team122.communication.Communicator;
import team122.robot.HQ;
import team122.robot.Soldier;

public class SoldierSelector extends Decision {
	public Soldier robot;

	public SoldierSelector(Soldier soldier) {
		this.robot = soldier;
		children.add(new SoldierDefenseMiner(this.robot));
		children.add(new SoldierSwarm(this.robot));
		children.add(new SoldierEncamper(this.robot));
		children.add(new SoldierCombat(this.robot));
		children.add(new SoldierDefender(this.robot));
		children.add(new SoldierNukeDefender(this.robot));
		children.get(SOLDIER_MINER).parent = this;
		children.get(SOLDIER_SWARMER).parent = this;
		children.get(SOLDIER_ENCAMPER).parent = this;
		children.get(SOLDIER_COMBAT).parent = this;
		children.get(SOLDIER_DEFENDER).parent = this;
		children.get(SOLDIER_NUKE).parent = this;
	}
	
	@Override
	public Node select() throws GameActionException {
		// TODO: when we have more than one child the decision code should be in here
		if(robot.enemyInMelee) {
			return children.get(SOLDIER_COMBAT);
		}
		
		if (robot.isNew) {
			int data = robot.com.receive(Communicator.CHANNEL_NEW_SOLDIER_MODE, SOLDIER_SWARMER);
			
			robot.initialMode = data % 10;
			robot.initialData = data - robot.initialMode;
			robot.isNew = false;
			
			System.out.println("Getting new robot behavior with : " + robot.initialMode + " : " + robot.initialData);
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

	public static final int SOLDIER_MINER = 0;
	public static final int SOLDIER_SWARMER = 1;
	public static final int SOLDIER_ENCAMPER = 2;
	public static final int SOLDIER_COMBAT = 3;
	public static final int SOLDIER_DEFENDER = 4;
	public static final int SOLDIER_NUKE = 5;
}
