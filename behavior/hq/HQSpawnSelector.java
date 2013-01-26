package team122.behavior.hq;

import team122.behavior.Decision;
import team122.behavior.Node;
import team122.robot.HQ;
import battlecode.common.GameActionException;

public class HQSpawnSelector extends Decision {
	
	protected HQ robot;

	public HQSpawnSelector(HQ robot) {
		super();
		this.robot = robot;

		this.children.add(new HQSpawnRush(robot));
		this.children.add(new HQSpawnForceResearchNuke(robot));
		this.children.add(new HQSpawnNukeIsArmed(robot));
		this.children.add(new HQSpawnMP(robot));
		this.children.get(HQ_SPAWN_RUSH).parent = this;
		this.children.get(HQ_SPAWN_FORCE_RESEARCH_NUKE).parent = this;
		this.children.get(HQ_SPAWN_NUKE_IS_ARMED).parent = this;
		this.children.get(HQ_SPAWN_MP_RUSH).parent = this;
	}

	@Override
	public Node select() throws GameActionException {
		
		//------------------------------------------------------------------------
		// 		NUKE STRATEGIES
		//------------------------------------------------------------------------
		if (robot.forceNukeRush) {
			
			return this.children.get(HQ_SPAWN_FORCE_RESEARCH_NUKE);
		} else if (robot.enemyResearchedNuke) {
			
			return this.children.get(HQ_SPAWN_NUKE_IS_ARMED);
			

		//------------------------------------------------------------------------
		// 		GENERAL STRATEGIES
		//------------------------------------------------------------------------
//		} else if (robot.rush) {
//			return this.children.get(HQ_SPAWN_RUSH);


		//------------------------------------------------------------------------
		// 		DEFAULT STRATEGIES
		//------------------------------------------------------------------------
		} else {
			//defaults to rush.
			robot.rush = true;
			return this.children.get(HQ_SPAWN_MP_RUSH);
		}

	}
	
	@Override
	public boolean pre() {
		return robot.rc.isActive();
	}

	public static final int HQ_SPAWN_RUSH = 0;
	public static final int HQ_SPAWN_FORCE_RESEARCH_NUKE = 1;
	public static final int HQ_SPAWN_NUKE_IS_ARMED = 2;
	public static final int HQ_SPAWN_MP_RUSH = 3;
}
