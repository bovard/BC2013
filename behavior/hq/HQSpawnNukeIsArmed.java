package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.robot.HQ;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;

public class HQSpawnNukeIsArmed extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;

	public HQSpawnNukeIsArmed(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
	}
	
	@Override
	public void run() throws GameActionException {
		
		if (!robot.rc.hasUpgrade(Upgrade.DEFUSION)) {
			robot.rc.researchUpgrade(Upgrade.DEFUSION);
		} else {
			
			robot.spawnSwarmer();
		}
		
		
		//Nothign to do.  DO not over commit.
		return;
	}

	@Override
	public boolean pre() {
		return robot.rc.isActive() && robot.enemyResearchedNuke;
	}
	
	public static final int MINER_COUNT = 1;
	public static final int ROBOT_LOWER_SOLDIER_COUNT = 10;
	public static final int ROBOT_UPPER_SOLDIER_COUNT = 40;
	public static final int ROBOT_UPPER_DEFUSION_SOLDIER_COUNT = 60;
	public static final int ROBOT_SUPPLIER_COUNT = 3;
	public static final int ROBOT_GENERATOR_COUNT = 3;
	public static final int ROBOT_ENCAMPER_COUNT = 1;
}
