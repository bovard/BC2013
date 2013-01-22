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
		if (!robot.enemyResearchedNuke) {
			robot.rc.senseEnemyNukeHalfDone();
		}
		if (!robot.encampmentSorter.finishBaseCalculation) {
			robot.encampmentSorter.calculate();
		} else {
		}
	}

	@Override
	public boolean pre() {
		return !robot.rc.isActive();
	}

}
