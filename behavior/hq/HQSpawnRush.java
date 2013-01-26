package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;

public class HQSpawnRush extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	protected Upgrade[] upgrades;

	public HQSpawnRush(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
		upgrades = new Upgrade[5];
		upgrades[0] = Upgrade.FUSION;
		upgrades[1] = Upgrade.DEFUSION;
		upgrades[2] = Upgrade.VISION;
		upgrades[3] = Upgrade.PICKAXE;
		upgrades[4] = Upgrade.NUKE;
	}
	
	private int genSpawn = 0;
	private int supSpawn = 0;
	private int artSpawn = 0;
	
	@Override
	public void run() throws GameActionException {
		
		int round = Clock.getRoundNum();
		
		if (utils.supplierCount == 0) {
			robot.spawnSupplier();
		}
		
		if (robot.rc.isActive() && robot.powerThisRound > 75) {
			if (round < 50 && robot.info.enemyHqDistance > 1000) {
				
				if (supSpawn < 2 && robot.spawnSupplier()) {
					supSpawn++;
				}
			} else if (robot.powerThisRound > 200 && robot.spawnSupplier()) {
				
			} else if (!robot.powerPositive && robot.spawnGenerator()) {
				
			} else if (round/500 + 1 > artSpawn && robot.spawnArtillery()) {
				artSpawn++;
			}
			if (robot.rc.isActive()){
				robot.spawnSwarmer();
			}
		} 
		if (robot.rc.isActive() && robot.powerThisRound < 70 && robot.spawnGenerator()) {
			
		} else if (robot.rc.isActive() && !robot.powerPositive && robot.spawnGenerator()) {
			
		}
		
		// if we are active, research something!
		if (robot.rc.isActive()) {
			for (int i = 0; i < upgrades.length; i++) {
				if (!robot.rc.hasUpgrade(upgrades[i])) {
					robot.rc.researchUpgrade(upgrades[i]);
					break;
				}
			}
		}
		
		
		//Nothign to do.  DO not over commit.
		return;
	}

	@Override
	public boolean pre() {
		return robot.rc.isActive() && robot.rush && !robot.enemyResearchedNuke;
	}
	
	public static final int MINER_COUNT = 1;
	public static final int ROBOT_LOWER_SOLDIER_COUNT = 10;
	public static final int ROBOT_UPPER_SOLDIER_COUNT = 40;
	public static final int ROBOT_UPPER_DEFUSION_SOLDIER_COUNT = 60;
	public static final int ROBOT_SUPPLIER_COUNT = 3;
	public static final int ROBOT_GENERATOR_COUNT = 3;
	public static final int ROBOT_ENCAMPER_COUNT = 1;
}
