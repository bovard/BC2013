package team122;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team122.behavioral.*;
import team122.robot.Artillery;
import team122.robot.Soldier;


/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		RobotInformation info = new RobotInformation(rc);
		if (rc.getType() == RobotType.HQ) {
			new HQBasicBehavior(rc, info).behave();
		} else if (rc.getType() == RobotType.SOLDIER) {
			new Soldier(rc, info).run();
		} else {
			if (rc.getType() == RobotType.ARTILLERY) {
				new Artillery(rc, info).run();
			}
		}
		// fell out
		while(true) {
			rc.yield();
		}
	}
}
