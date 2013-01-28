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
	protected boolean attackReady = true;
	protected int startRound;

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
			if (robot.hqUtils.powerTotalFromLastRound < GameStrategy.WAVE_POWER_THRESHHOLD) {
				attackReady = false;
			}
			startRound = Clock.getRoundNum();
		}
	}
	
	int supplier = 0;
	int artillery = 0;
	int mining = 0;
	
	@Override
	public void run() throws GameActionException {
		
		if (!attackReady) {
			if (robot.hqUtils.powerTotalFromLastRound > GameStrategy.WAVE_POWER_THRESHHOLD + 5) {
				attackReady = true;
			}
		}
		
		if (attackReady && Clock.getRoundNum() % 3 == 0 && robot.hqUtils.powerTotalFromLastRound < GameStrategy.WAVE_POWER_THRESHHOLD) {
			robot.attack();
			robot.spawnBackdoor();
		}
		
		
		if (robot.rc.isActive()) {
			if (supplier < 1 && robot.spawnSupplier()) {
				supplier++;
			} else if (mining < 1) {
				robot.spawnMiner();
				mining++;
			} else {
				robot.spawnScout();
			}
			
			
		} // end is active
	}

	@Override
	public boolean pre() {
		return robot.state.inSpawnWave && !robot.enemyResearchedNuke;
	}
}