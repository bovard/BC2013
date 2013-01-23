package team122.behavior.hq;

import battlecode.common.GameActionException;
import battlecode.common.Upgrade;
import team122.behavior.Behavior;
import team122.robot.HQ;

public class HQSpawnVsNukeBot extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	
	public HQSpawnVsNukeBot(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
	}
	
	int count = 0;
	@Override
	public void run() throws GameActionException {
		//Perhaps swarmers?
		if (!robot.rc.hasUpgrade(Upgrade.DEFUSION)) {
			robot.rc.researchUpgrade(Upgrade.DEFUSION);
		} else {
			robot.spawnSwarmer();
		}
	}

	@Override
	public boolean pre() throws GameActionException {
		
		return robot.rc.isActive() && robot.vsNukeBot;
	}

}
