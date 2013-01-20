package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.robot.HQ;
import battlecode.common.GameActionException;

public class HQIdle extends Behavior {
	
	protected HQ robot;

	public HQIdle(HQ robot) {
		super();
		this.robot = robot;
	}
	
	@Override
	public void run() throws GameActionException {
		//The soldier is not active but it can still process signals.
	}

	@Override
	public boolean pre() {
		return !robot.rc.isActive();
	}

}