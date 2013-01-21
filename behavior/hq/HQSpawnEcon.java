package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Upgrade;

public class HQSpawnEcon extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	protected RobotController rc;
	protected int nukeCount;

	public HQSpawnEcon(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
		this.rc = robot.rc;
		nukeCount = 0;
	}
	
	@Override
	public void run() throws GameActionException {

		if (utils.minerCount < MINER_COUNT) {
			robot.spawn(SoldierSelector.SOLDIER_MINER);
		} else if (!rc.hasUpgrade(Upgrade.PICKAXE)) {
			rc.researchUpgrade(Upgrade.PICKAXE);
		} else if (utils.soldierCount < ROBOT_LOWER_SOLDIER_COUNT + utils.generatorCount * 3) { // more gen more soldiers.
			robot.spawn(SoldierSelector.SOLDIER_DEFENDER);
		} else if (utils.shouldCreateEncampment(robot.mapInfo) && utils.encamperCount < ROBOT_ENCAMPER_COUNT) {
			System.out.println("Create Encampment");
			
			if (utils.supplierCount < ROBOT_SUPPLIER_COUNT && Clock.getRoundNum() % 2 == 0) {
				System.out.println("Create Supplier");
				robot.spawn(SoldierSelector.setEncamperData(SoldierSelector.SUPPLIER_ENCAMPER, utils.generatorCount + utils.supplierCount + utils.encamperCount));
			} else if (utils.generatorCount < ROBOT_GENERATOR_COUNT) {
				System.out.println("Create Generator");
				robot.spawn(SoldierSelector.setEncamperData(SoldierSelector.GENERATOR_ENCAMPER, utils.generatorCount + utils.supplierCount + utils.encamperCount));
			}
		} else if (!rc.hasUpgrade(Upgrade.DEFUSION)) {
			rc.researchUpgrade(Upgrade.DEFUSION);
		} else if (utils.soldierCount < ROBOT_UPPER_SOLDIER_COUNT + utils.generatorCount * 3) { // more gen more soldiers.
			robot.spawn(SoldierSelector.SOLDIER_DEFENDER);
		} else if (!rc.hasUpgrade(Upgrade.NUKE)) {
			rc.researchUpgrade(Upgrade.NUKE);
		}
		
		
		//Nothign to do.  DO not over commit.
		return;
	}

	@Override
	public boolean pre() {
		return robot.rc.isActive() && robot.econ;
	}
	
	public static final int MINER_COUNT = 2;
	public static final int ROBOT_LOWER_SOLDIER_COUNT = 8;
	public static final int ROBOT_UPPER_SOLDIER_COUNT = 80;
	public static final int ROBOT_SUPPLIER_COUNT = 15;
	public static final int ROBOT_GENERATOR_COUNT = 5;
	public static final int ROBOT_ENCAMPER_COUNT = 3;
}
