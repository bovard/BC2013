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
	
	@Override
	public void run() throws GameActionException {
		
		double energy = robot.rc.getEnergon();
		int round = Clock.getRoundNum();
		
		if (energy > 50) {
			if (round < 50 && robot.info.enemyHqDistance > 300) {
				System.out.println("spawning Generator");
				if (robot.spawnGenerator()) {
					System.out.println("reallyG");
					robot.rc.yield();
				}
				
				System.out.println("spawning Supplier");
				if (robot.spawnSupplier()) {
					System.out.println("ReallyS");
				}
			} else if (energy > 200 && robot.spawnSupplier()) {
				
			} else if (energy < 100 || !robot.energyPositive && robot.spawnGenerator()) {
				
			} else {
				robot.spawnSwarmer();
			}
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
