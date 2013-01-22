package team122.behavior.hq;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;
import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;

public class HQSpawnDarkHorse extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	
	public HQSpawnDarkHorse(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
	}

	private final int NUKE_TIME = 225;
	
	int count = 0;
	@Override
	public void run() throws GameActionException {
//		if (Clock.getRoundNum() < NUKE_TIME) {
//			if (utils.minerCount < MINER_COUNT) {
//				robot.spawn(SoldierSelector.SOLDIER_MINER);
//			} else if (utils.artilleryCount < 5 && utils.encamperCount < 5 && robot.spawnArtillery()) {
//			} else if (utils.generatorCount < 2 && utils.encamperCount < 2 && robot.spawnGenerator()) {
//			} else if (utils.supplierCount < 2 && utils.encamperCount < 3 && robot.spawnSupplier()) {
//				robot.spawnSupplier();
//			} else if (!robot.rc.hasUpgrade(Upgrade.PICKAXE)) {
//				robot.rc.researchUpgrade(Upgrade.PICKAXE);
//			} else if (robot.rc.getTeamPower() > 50 && Clock.getRoundNum() < NUKE_TIME) { // more gen more soldiers.
//				robot.spawn(SoldierSelector.SOLDIER_NUKE);
//			}
//		} else {
//			if (utils.nukeDefenderCount < 10) {
//				robot.spawn(SoldierSelector.SOLDIER_NUKE);
//			} else {
//				robot.rc.researchUpgrade(Upgrade.NUKE);
//			}
//		}

		if (count < 30) {
			robot.spawnDarkHorse();
			count++;
		} else {
			robot.rc.researchUpgrade(Upgrade.NUKE);
		}
		
		return;
		
	}

	@Override
	public boolean pre() throws GameActionException {
		
		return robot.rc.isActive() && robot.darkHorse;
	}

}
