package team122.behavioral;

import battlecode.common.GameConstants;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class EncampmentBehavior extends Behavior {

	public EncampmentBehavior(RobotController rc) {
		super(rc);
	}
	
	public void behave() {
		while (true) {
			
			if (rc.getType() == RobotType.ARTILLERY) {
//				rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), GameConstants., team)
			}
			
			rc.yield();
		}
	}
}
