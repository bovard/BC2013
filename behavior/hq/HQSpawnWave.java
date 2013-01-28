package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;
import team122.utils.GameStrategy;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;

public class HQSpawnWave extends Behavior {
	
	protected HQ robot;
	protected HQSelector parent;
	protected boolean init;

	public HQSpawnWave(HQ robot) {
		super();
		this.robot = robot;
		init = false;
	}
	
	/**
	 * When we first start we need to calculate the encamper spots.
	 */
	@Override
	public void start() throws GameActionException { 
		if (!init) {
			init = true;
			robot.calculateEncamperSpots();
		}
	}
	
	int supplier = 0;
	int artillery = 0;
	int mining = 0;
	
	@Override
	public void run() throws GameActionException {
		
		if (robot.rc.isActive()) {
			
		} // end is active
		System.out.println("Spawning!");
	}

	@Override
	public boolean pre() {
		return robot.state.inSpawnWave && !robot.enemyResearchedNuke;
	}
}