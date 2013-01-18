package team122;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team122.behavioral.*;
import team122.robot.Artillery;
import team122.robot.Generator;
import team122.robot.Soldier;


/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		RobotInformation information = new RobotInformation(rc);
		if (rc.getType() == RobotType.HQ) {
			new HQBasicBehavior(rc, information).behave();
		} else if (rc.getType() == RobotType.SOLDIER) {
			new Soldier(rc, information).run();
		} else {
			if (rc.getType() == RobotType.ARTILLERY) {
				System.out.println("Running Artillery!");
				new Artillery(rc, information).run();
			} else if (rc.getType() == RobotType.GENERATOR) {
				System.out.println("Creating Generator!");
				new Generator(rc, information).run();
			}
		}
		// fell out
		while(true) {
			rc.yield();
		}
	}
}
