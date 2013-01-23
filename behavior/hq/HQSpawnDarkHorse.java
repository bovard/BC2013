package team122.behavior.hq;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;
import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;

public class HQSpawnDarkHorse extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	
	public HQSpawnDarkHorse(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
	}
	
	int count = 0;
	final int NUKE_HOLDOUT_TIME = 213;
	final int VISION_COUNT = 5;
	final int MAX_COUNT = 10;
	
	@Override
	public void run() throws GameActionException {

		//We always spawn 3.
		if (count < 3) {
			robot.spawnDarkHorse();
			count++;
		} else {
			
			if (robot.nukeCount < NUKE_HOLDOUT_TIME) {
				robot.rc.researchUpgrade(Upgrade.NUKE);
				robot.nukeCount++;
			} else {
				boolean sensedNuke = robot.rc.senseEnemyNukeHalfDone();
				if (!sensedNuke && count > VISION_COUNT && !robot.rc.hasUpgrade(Upgrade.VISION)) {
					robot.rc.researchUpgrade(Upgrade.VISION);
				} else if (!sensedNuke && count < MAX_COUNT && robot.spawnDarkHorse()) {
					count++;
				} else {
					robot.rc.researchUpgrade(Upgrade.NUKE);
				}
			}
		}
		
		return;
		
	}

	@Override
	public boolean pre() throws GameActionException {
		
		return robot.rc.isActive() && robot.darkHorse;
	}

}
