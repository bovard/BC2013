package team122.behavior.lib.hq;

import team122.behavior.lib.Behavior;
import team122.behavior.lib.SoldierSelector;
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
		double energon = robot.rc.getEnergon();
		
		if (robot.defensive > DEFENSIVE_MINER_SPAWN && utils.minerCount < 1) {
			
		}
	}

	@Override
	public boolean pre() {
		return robot.rc.isActive();
	}

	public static final int DEFENSIVE_MINER_SPAWN = 25;
}
