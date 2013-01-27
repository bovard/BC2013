package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.robot.HQ;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Team;
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
	
	private int spawned = 1;
	
	@Override
	public void run() throws GameActionException {
		
		if (robot.info.myTeam == Team.A) {

			if (spawned % 10 == 0) {
				robot.spawnGenerator();
			}
		} else {
			
			if (spawned % 500 == 0) {
				robot.spawnBackdoor();
			} else if (spawned % 100 == 0) {
				robot.spawnEncampmentHunter(0);
			} else if (spawned % 25 == 0) {
				robot.spawnGenerator();
			}
		}

		spawned++;		
		
		//Nothign to do.  DO not over commit.
		return;
	}

	@Override
	public boolean pre() {
		return robot.rc.isActive() && !robot.enemyResearchedNuke;
	}
}
