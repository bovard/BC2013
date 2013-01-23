package team122.behavior.hq;

import battlecode.common.GameActionException;
import team122.behavior.Behavior;
import team122.robot.HQ;

public class HQSpawnVsNukeBotMinerPickax extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	
	public HQSpawnVsNukeBotMinerPickax(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
	}
	
	int count = 0;
	@Override
	public void run() throws GameActionException {
		//Perhaps swarmers?
		robot.spawnSwarmer();
	}

	@Override
	public boolean pre() throws GameActionException {
		
		return robot.rc.isActive() && robot.vsNukeBotAndMinerPickax;
	}
}
