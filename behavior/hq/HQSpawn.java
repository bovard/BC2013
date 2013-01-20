package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierEncamper;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;

public class HQSpawn extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;

	public HQSpawn(HQ robot) {
		super();
		this.robot = robot;
		utils = robot.hqUtils;
	}
	
	@Override
	public void run() throws GameActionException {
		
		//Miners are super required.
		if (utils.requireMiner(robot.defensive)) {
			robot.spawn(SoldierSelector.SOLDIER_MINER);
			return;
		}
		
		//Generators and suppliers are needed.
		if (utils.canSpawnSoldier() && utils.requireSoldier(robot.defensive, robot.econ)) {
			robot.spawn(SoldierSelector.SOLDIER_SWARMER);
		}
		
		if (utils.requireGenerator()) {
			robot.spawn(SoldierSelector.GENERATOR_ENCAMPER + SoldierSelector.SOLDIER_ENCAMPER);
		}
	}

	@Override
	public boolean pre() {
		return robot.rc.isActive();
	}

	public static final int DEFENSIVE_MINER_SPAWN = 25;
}
