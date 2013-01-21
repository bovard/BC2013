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
		this.children.add(new HQSpawnEcon(robot));
		this.children.add(new HQSpawnNuke(robot));
		this.children.get(HQ_SPAWN_RUSH).parent = this;
		this.children.get(HQ_SPAWN_ECON).parent = this;
		this.children.get(HQ_SPAWN_NUKE).parent = this;
	}

	@Override
	public Node select() throws GameActionException {
//		if (robot.rush) {
//			return this.children.get(HQ_SPAWN_RUSH);
//		} else if (robot.nuke) {
//			return this.children.get(HQ_SPAWN_NUKE);
//		}
		robot.nuke = true;
		return this.children.get(HQ_SPAWN_NUKE);
	}
	
	@Override
	public boolean pre() {
		return robot.rc.isActive();
	}

	public static final int HQ_SPAWN_RUSH = 0;
	public static final int HQ_SPAWN_ECON = 1;
	public static final int HQ_SPAWN_NUKE = 2;
}
