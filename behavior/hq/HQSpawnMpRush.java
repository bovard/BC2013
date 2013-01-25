package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;

public class HQSpawnMpRush extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	protected Upgrade[] upgrades;

	public HQSpawnMpRush(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
		upgrades = new Upgrade[5];
		upgrades[0] = Upgrade.PICKAXE;
		upgrades[1] = Upgrade.DEFUSION;
		upgrades[2] = Upgrade.VISION;
		upgrades[3] = Upgrade.FUSION;
		upgrades[4] = Upgrade.NUKE;
	}
	
	int roundReset = 150;
	int currentRound = 0;
	int genSpawned = 0;
	int supSpawn = 0;
	int artillerySpawn = 0;
	int swarmerSpawn = 0;
	boolean canSpawnGens = true;
	boolean canSpawnArtilleries = true;
	
	@Override
	public void run() throws GameActionException {
		
//		if (Clock.getRoundNum() / roundReset > currentRound) {
//			
//			if (genSpawned == supSpawn && canSpawnGens) {
//				if (!robot.spawnGenerator()) {
//					System.out.println("COULD NOT SPAWN GENERATOR!");
//					canSpawnGens = false;
//				}
//				genSpawned += 2;
//			} else if (supSpawn < genSpawned && canSpawnGens) {
//				if (!robot.spawnSupplier()) {
//					canSpawnGens = false;
//				}
//				supSpawn++;
//			} else if (artillerySpawn < supSpawn && canSpawnArtilleries) {
//				if (!robot.spawnArtillery()) {
//					canSpawnArtilleries = false;
//				}
//				artillerySpawn++;
//			} else {
//				currentRound++;
//			}
//		} else {
//
//			if (rand.nextInt() % 5 == 0) {
//				if (!robot.rc.hasUpgrade(Upgrade.FUSION)) {
//					robot.rc.researchUpgrade(Upgrade.FUSION);
//				}
//			} else {
//				robot.spawn(SoldierSelector.SOLDIER_SWARMER);
//			}
//		}
		if (!robot.encampmentSorter.finishBaseCalculation) {
			robot.spawnSwarmer();
		}
		if (rand.nextInt() % 2 == 0) {
			robot.spawnGenerator();
		} else {
			robot.spawnArtillery();
		}
		
		return;
	}

	@Override
	public boolean pre() {
		return robot.rc.isActive() && robot.rush;
	}
	
	public static final int MINER_COUNT = 1;
	public static final int ROBOT_LOWER_SOLDIER_COUNT = 10;
	public static final int ROBOT_UPPER_SOLDIER_COUNT = 40;
	public static final int ROBOT_UPPER_DEFUSION_SOLDIER_COUNT = 60;
	public static final int ROBOT_SUPPLIER_COUNT = 3;
	public static final int ROBOT_GENERATOR_COUNT = 3;
	public static final int ROBOT_ENCAMPER_COUNT = 1;
}
