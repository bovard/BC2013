package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;
import team122.utils.GameStrategy;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;

public class HQSpawnStart extends Behavior {
	
	protected HQ robot;
	protected HQSelector parent;
	protected boolean init;

	public HQSpawnStart(HQ robot) {
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
	
	@SuppressWarnings("unused")
	@Override
	public void run() throws GameActionException {
		
		if (robot.rc.isActive()) {
			if (Clock.getRoundNum() < GameStrategy.START_STRATEGY_ATTACK_ROUND) {
				
				//We always spawn a supplier first.
				if (supplier == 0 && (robot.greedySupplier() || robot.spawnSupplier())) {
					supplier++;
				
				//Now we spawn soldiers to a certain point.
				} else {
	
					//We spawn swarmers until calculated or round 100
					robot.spawnScout();
				}
			} else {
					
				//The alternative option.
				if (GameStrategy.START_STRATEGY_DEFUSION) {
					
					if (robot.rc.hasUpgrade(Upgrade.DEFUSION)) {
						
						robot.state.inStart = false;
						robot.state.inSpawnWave = true;
					} else {
						robot.rc.researchUpgrade(Upgrade.DEFUSION);
					}
				} else {

					if (mining == 0 && GameStrategy.START_STRATEGY_MINER) {
						robot.spawnMiner();
						mining++;
						
					} else if (artillery == 0 && robot.spawnArtillery()) {
						artillery++;
					
					//Now we spawn soldiers to a certain point.
					} else {
						
						//We spawn swarmers until calculated or round 100
						robot.spawnBackdoor();
						
						robot.state.inStart = false;
						robot.state.inSpawnWave = true;
					}
				}
			} // end else above round 140
		} // end is active

		if (Clock.getRoundNum() == GameStrategy.START_STRATEGY_ATTACK_ROUND || Clock.getRoundNum() == GameStrategy.START_STRATEGY_ATTACK_ROUND + 3) {
			
			robot.attack();
		}
	}

	@Override
	public boolean pre() {
		return robot.state.inStart;
	}
}