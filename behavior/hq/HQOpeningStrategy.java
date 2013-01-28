package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;
import battlecode.common.Clock;
import battlecode.common.GameActionException;

public class HQOpeningStrategy extends Behavior {
	
	protected HQ robot;
	protected HQSelector parent;
	protected boolean init;

	public HQOpeningStrategy(HQ robot) {
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
	
	@Override
	public void run() throws GameActionException {
		
		if (robot.rc.isActive()) {
			
			//We always spawn a supplier first.
			if (supplier == 0 && robot.greedySupplier()) {
				supplier++;
			
			//Now we spawn soldiers to a certain point.
			} else {

				//We spawn swarmers until calculated or round 100
				robot.spawnSwarmer();
			}
		}
	}

	@Override
	public boolean pre() {
		return (!robot.encampmentSorter.sorted || Clock.getRoundNum() < HQSelector.OPENING_STRATEGY_MINIMUM_ROUND_COUNT) && !robot.enemyResearchedNuke;
	}

}