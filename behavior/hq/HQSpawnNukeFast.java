package team122.behavior.hq;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;
import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;

public class HQSpawnNukeFast extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	
	public HQSpawnNukeFast(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
	}

	private final int NUKE_TIME = 325;
	
	@Override
	public void run() throws GameActionException {
		if (Clock.getRoundNum() < 25) {
			if (utils.minerCount < MINER_COUNT) {
				robot.spawn(SoldierSelector.SOLDIER_MINER);
			} else if (!robot.rc.hasUpgrade(Upgrade.PICKAXE)) {
				robot.rc.researchUpgrade(Upgrade.PICKAXE);
			} else if (robot.rc.getTeamPower() > 50 && Clock.getRoundNum() < NUKE_TIME) { // more gen more soldiers.
				robot.spawn(SoldierSelector.SOLDIER_NUKE);
			}
		} else {
			robot.rc.researchUpgrade(Upgrade.NUKE);
		}
		return;
		
	}

	@Override
	public boolean pre() throws GameActionException {
		
		return robot.rc.isActive() && robot.nuke;
	}
	
	
	public static final int MINER_COUNT = 1;
	public static final int ROBOT_LOWER_SOLDIER_COUNT = 10;
	public static final int ROBOT_UPPER_SOLDIER_COUNT = 40;
	public static final int ROBOT_UPPER_DEFUSION_SOLDIER_COUNT = 60;

}
