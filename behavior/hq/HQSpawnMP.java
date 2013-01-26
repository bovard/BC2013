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
	protected int count = 0;

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
	
	private int spawned = 0;
	
	@Override
	public void run() throws GameActionException {
		
		if (spawned % 6 == 0) {
			robot.spawnMiner();
		} else if (spawned % 6 == 1) {
			robot.spawnGenerator();
		} else if (spawned % 6 == 2) {
			robot.spawnSupplier();
		} else if (spawned % 6 == 3) {
			robot.spawnArtillery();
		} else if (spawned % 6 == 4) {
			robot.spawnMiner();
		} else if (spawned % 6 == 5) {
			robot.spawnEncampmentHunter();
		}
		
		spawned++;
		
		HQUtils.calculate(robot);
		
		//Nothign to do.  DO not over commit.
		return;
	}

	@Override
	public boolean pre() {
		return robot.rc.isActive() && !robot.enemyResearchedNuke;
	}
}
