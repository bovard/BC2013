package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.robot.HQ;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;

public class HQSpawnMP extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	protected Upgrade[] upgrades;

	public HQSpawnMP(HQ robot) {
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
	
	private int spawn = 0;
	
	@Override
	public void run() throws GameActionException {
		
		int round = Clock.getRoundNum();

		if (robot.rc.hasUpgrade(Upgrade.FUSION)) {
			robot.rc.researchUpgrade(Upgrade.FUSION);
		} else {
			if (spawn % 10 == 0) {
				robot.spawnArtillery();
			} else if (spawn % 10 == 1) {
				robot.spawnGenerator();
			} else if (spawn % 10 == 2) {
//				robot.spawnMiner();
			} else if (spawn % 10 == 3) {
				robot.spawnSupplier();
			} else if (spawn % 10 == 4) {
//				robot.spawnSwarmer();
			}
			spawn++;
		}
		HQUtils.calculate(robot);
		
//		if (genSpawn <= artSpawn) {
//			if (rand.nextInt() % 4 == 0) {
//				robot.spawnSupplier();
//			} else {
//				robot.spawnGenerator();
//			}
//			genSpawn++;
//		} else {
//			robot.spawnArtillery();
//			artSpawn++;
//		}
		
		
		//Nothign to do.  DO not over commit.
		return;
	}

	@Override
	public boolean pre() {
		return robot.rc.isActive();
	}
}
