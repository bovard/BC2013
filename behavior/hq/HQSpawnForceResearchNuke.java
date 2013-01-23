package team122.behavior.hq;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;
import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;

public class HQSpawnForceResearchNuke extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	
	public HQSpawnForceResearchNuke(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
	}

	private final int NUKE_TIME = 325;
	
	@Override
	public void run() throws GameActionException {
		robot.rc.researchUpgrade(Upgrade.NUKE);
		return;
		
	}

	@Override
	public boolean pre() throws GameActionException {
		
		return robot.rc.isActive() && robot.forceNukeRush;
	}
	
	
	public static final int MINER_COUNT = 1;
	public static final int ROBOT_LOWER_SOLDIER_COUNT = 10;
	public static final int ROBOT_UPPER_SOLDIER_COUNT = 40;
	public static final int ROBOT_UPPER_DEFUSION_SOLDIER_COUNT = 60;

}
