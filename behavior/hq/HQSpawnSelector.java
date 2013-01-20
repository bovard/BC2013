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
		this.children.get(HQ_SPAWN_RUSH).parent = this;
		this.children.get(HQ_SPAWN_ECON).parent = this;
	}

	@Override
	public Node select() throws GameActionException {
		if (robot.rush) {
			return this.children.get(HQ_SPAWN_RUSH);
		} else if (robot.econ) {
			return this.children.get(HQ_SPAWN_ECON);
		}

		//defaults to rush.
		return this.children.get(HQ_SPAWN_RUSH);
	}
	
	@Override
	public boolean pre() {
		return robot.rc.isActive();
	}

	public static final int HQ_SPAWN_RUSH = 0;
	public static final int HQ_SPAWN_ECON = 1;
}
