package team122.behavioral;

import battlecode.common.RobotController;

public class GeneratorBehavior extends Behavior {

	public GeneratorBehavior(RobotController rc) {
		super(rc);
	}
	
	public void behave() {
		while (true) {
			// Nothing to do?
			rc.yield();
		}
	}
}
