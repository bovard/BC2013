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
		upgrades[0] = Upgrade.PICKAXE;
		upgrades[1] = Upgrade.DEFUSION;
		upgrades[2] = Upgrade.VISION;
		upgrades[3] = Upgrade.FUSION;
		upgrades[4] = Upgrade.NUKE;
	}
	
	@Override
	public void run() throws GameActionException {
		if (utils.minerCount < MINER_COUNT) {
			robot.spawn(SoldierSelector.SOLDIER_MINER);
		} else if (utils.generatorCount < 1 && utils.encamperCount < 2) {
			
			//TODO: WONT WORK NO MORE
			robot.spawn(SoldierSelector.setEncamperData(SoldierSelector.GENERATOR_ENCAMPER, 0));
		} else if (utils.supplierCount < 1 && utils.encamperCount < 2) {
			
			
			//TODO: WONT WORK NO MORE -- SEE NOTE ON HQDynamic
			robot.spawn(SoldierSelector.setEncamperData(SoldierSelector.SUPPLIER_ENCAMPER, 0));
		} else if (utils.soldierCount < ROBOT_LOWER_SOLDIER_COUNT + utils.generatorCount * 3 && robot.rc.getTeamPower() > 50) { // more gen more soldiers.
			robot.spawn(SoldierSelector.SOLDIER_SWARMER);
		} else if (utils.soldierCount < ROBOT_UPPER_SOLDIER_COUNT + utils.generatorCount * 3 && robot.rc.getTeamPower() > 50) { // more gen more soldiers.
			robot.spawn(SoldierSelector.SOLDIER_SWARMER);
		} else {
			for (Upgrade u : upgrades) {
				if (!robot.rc.hasUpgrade(u)) {
					robot.rc.researchUpgrade(u);
					break;
				}
			}
		}
		//Nothign to do.  DO not over commit.
		return;
	}

	@Override
	public boolean pre() {
		return robot.rc.isActive();
	}
	
	public static final int MINER_COUNT = 1;
	public static final int ROBOT_LOWER_SOLDIER_COUNT = 10;
	public static final int ROBOT_UPPER_SOLDIER_COUNT = 40;
	public static final int ROBOT_UPPER_DEFUSION_SOLDIER_COUNT = 60;
	public static final int ROBOT_SUPPLIER_COUNT = 3;
	public static final int ROBOT_GENERATOR_COUNT = 3;
	public static final int ROBOT_ENCAMPER_COUNT = 1;
}
