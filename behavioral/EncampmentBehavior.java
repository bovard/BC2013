package team122.behavioral;

import team122.RobotInformation;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class EncampmentBehavior extends Behavior {

	public EncampmentBehavior(RobotController rc, RobotInformation info) {
		super(rc, info);
	}
	
	public void behave() {
		while (true) {
			
			try {
				if (rc.getType() == RobotType.ARTILLERY) {
					if (rc.isActive()) {
						GameObject[] robots = rc.senseNearbyGameObjects(GameObject.class, RobotType.ARTILLERY.attackRadiusMaxSquared, enemyTeam);
						
						if (robots.length > 0) {
							rc.attackSquare(rc.senseLocationOf(robots[0]));
						}
					} // end is active.
				} // end artillery
			} catch (Exception e) {
				e.printStackTrace();
			}
			rc.yield();
		}
	}
}
