package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;
import battlecode.common.GameActionException;

public class HQSpawnRush extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;

	public HQSpawnRush(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
	}
	
	@Override
	public void run() throws GameActionException {
		if (utils.minerCount < MINER_COUNT) {
			robot.spawn(SoldierSelector.SOLDIER_MINER);
		} else if (utils.soldierCount < ROBOT_LOWER_SOLDIER_COUNT + utils.generatorCount * 3) { // more gen more soldiers.
			robot.spawn(SoldierSelector.SOLDIER_SWARMER);
		} else if (utils.shouldCreateEncampment(robot.info) && utils.encamperCount < ROBOT_ENCAMPER_COUNT) {
			System.out.println("Create Encampment");
			if (utils.supplierCount < ROBOT_SUPPLIER_COUNT) {
				System.out.println("Create Supplier");
				robot.spawn(SoldierSelector.setEncamperData(SoldierSelector.SUPPLIER_ENCAMPER, utils.generatorCount + utils.supplierCount + utils.encamperCount));
			} else if (utils.generatorCount< ROBOT_SUPPLIER_COUNT) {
				System.out.println("Create Generator");
				robot.spawn(SoldierSelector.setEncamperData(SoldierSelector.GENERATOR_ENCAMPER, utils.generatorCount + utils.supplierCount + utils.encamperCount));
			}
		} else if (utils.soldierCount < ROBOT_UPPER_SOLDIER_COUNT + utils.generatorCount * 3) { // more gen more soldiers.
			robot.spawn(SoldierSelector.SOLDIER_SWARMER);
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
	public static final int ROBOT_UPPER_SOLDIER_COUNT = 30;
	public static final int ROBOT_SUPPLIER_COUNT = 3;
	public static final int ROBOT_GENERATOR_COUNT = 3;
	public static final int ROBOT_ENCAMPER_COUNT = 2;
}
