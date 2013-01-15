package team122.behavioral;

import team122.RobotInformation;
import battlecode.common.Direction;
import battlecode.common.RobotController;

public class HQBasicBehavior extends Behavior {
	
	int spawnCount = 0;
	public HQBasicBehavior(RobotController rc, RobotInformation info) {
		super(rc, info);
	}
	
	/**
	 * This will be the behavioral loop for the robots life.
	 */
	public void behave() {
		while (true) {
			try {
				if (rc.isActive()) {
					Direction dir = Direction.values()[(int)(Math.random() * 8)];
					if (rc.canMove(dir) && spawnCount < 1000) {
						rc.spawn(dir);
						spawnCount++;
					}
				}			
				
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
