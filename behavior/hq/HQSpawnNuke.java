package team122.behavior.hq;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;
import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;

public class HQSpawnNuke extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	
	public HQSpawnNuke(HQ robot) {
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
		if (robot.encampmentSorter.finishBaseCalculation) {
			System.out.println("CALCULATED!!!!");
			if (count % 3 == 0) {
				robot.spawnGenerator();
			} else if (count % 3 == 1) {
				robot.spawnSupplier();
			} else {
				robot.spawnArtillery();
			}
		} else {
			System.out.println("CLACULATING");
			robot.encampmentSorter.calculate();
		}
		return;
		
	}

	@Override
	public boolean pre() throws GameActionException {
		
		return robot.rc.isActive() && robot.nuke;
	}
	
	
	public static final int MINER_COUNT = 3;
	public static final int ROBOT_LOWER_SOLDIER_COUNT = 10;
	public static final int ROBOT_UPPER_SOLDIER_COUNT = 40;
	public static final int ROBOT_UPPER_DEFUSION_SOLDIER_COUNT = 60;

}
