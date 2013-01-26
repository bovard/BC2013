package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;
import battlecode.common.GameActionException;

public class HQCalculate extends Behavior {
	
	protected HQ robot;
	protected HQSelector parent;
	protected boolean init;
	

	public HQCalculate(HQ robot) {
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
	
	@Override
	public void run() throws GameActionException {
		
		//While it calculates, it needs to make sure that it continues to spawn.
		if (robot.rc.isActive()) {
//			robot.spawnSwarmer();
		}
		robot.encampmentSorter.calculate();
	}

	@Override
	public boolean pre() {
		return !robot.encampmentSorter.finishBaseCalculation;
	}

}