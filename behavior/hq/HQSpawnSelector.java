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
		this.children.get(HQ_SPAWN_RUSH).parent = this;
	}

	@Override
	public Node select() throws GameActionException {
		if (robot.rush) {
			return this.children.get(HQ_SPAWN_RUSH);
		} else if (robot.mid && !robot.econ) {
			
		} else if (robot.mid && robot.econ) {
			
		} else {
			
		}

		//defaults to rush.
		return this.children.get(HQ_SPAWN_RUSH);
	}
	
	@Override
	public boolean pre() {
		return robot.rc.isActive();
	}

	public static final int HQ_SPAWN_RUSH = 0;
	public static final int HQ_SPAWN_MID = 1;
	public static final int HQ_SPAWN_MID_ECON = 2;
	public static final int HQ_SPAWN_ECON = 3;
}
