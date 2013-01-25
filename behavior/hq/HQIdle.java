package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.robot.HQ;
import team122.utils.EncampmentSorter;
import team122.utils.QuicksortTree;
import battlecode.common.GameActionException;

public class HQIdle extends Behavior {
	
	protected HQ robot;
	protected QuicksortTree generatorTree;
	protected QuicksortTree artilleryTree;

	public HQIdle(HQ robot) {
		super();
		this.robot = robot;
		this.generatorTree = new QuicksortTree();
		this.artilleryTree = new QuicksortTree();
	}
	
	@Override
	public void run() throws GameActionException {
		HQUtils.calculate(robot);
	}

	@Override
	public boolean pre() {
		return !robot.rc.isActive();
	}

}
