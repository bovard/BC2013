package team122;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team122.behavioral.*;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		if (rc.getType() == RobotType.HQ) {
			new HQBasicBehavior(rc).behave();
		} else if (rc.getType() == RobotType.SOLDIER) {
			new SoldierBehavior(rc).behave();
		} else {
			new EncampmentBehavior(rc).behave();
		}
	}
}
