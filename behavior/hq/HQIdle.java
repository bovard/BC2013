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
		if (!robot.encampmentSorter.finishBaseCalculation) {
			robot.encampmentSorter.calculate(1);
		} else if (!robot.encampmentSorter.finishArtilleryCalculation) {
			System.out.println("EncampmentSorter base done");
			robot.encampmentSorter.calculateArtilleryLocations(1);
		} else if (!robot.encampmentSorter.finishGeneratorCalculation) {
			System.out.println("EncampmentSorter artillery done");
			robot.encampmentSorter.calculateGeneratorLocations(1);
		} else {
			System.out.println("OFFICIAL FINISH!");
		}
	}

	@Override
	public boolean pre() {
		return !robot.rc.isActive();
	}

}
