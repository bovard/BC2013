package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;
import battlecode.common.GameActionException;

public class HQCalculate extends Behavior {
	
	protected HQ robot;
	public boolean calculating;
	protected HQSelector parent;
	

	public HQCalculate(HQ robot) {
		super();
		this.robot = robot;
		calculating = true;
	}
	
	@Override
	public void start() { 
		
	}
	
	@Override
	public void run() throws GameActionException {
		//Spawns a soldier since its an "active" option so we can
		//use our time best.
		// -- SPAWNS a soldier intentionally to help make the calculation time problem better--
		//Will likely finish before next rc is active.
		
		if (robot.rc.isActive()) {
			robot.spawn(SoldierSelector.SOLDIER_MINER);
		}
		
		//Calculates information about the map, strategy, ect. ect.
		robot.calculateStrategyPoints();
		robot.rush = true;
		calculating = false;
	}

	@Override
	public boolean pre() {
		return calculating;
	}

}