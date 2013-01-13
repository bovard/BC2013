package team122;

import team122.navigation.NavigationMode;
import team122.navigation.NavigationSystem;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		NavigationSystem navSystem = null;
		NavigationMode navMode = null;
		boolean navInit = false;
		Team enemyTeam = null;
		Team myTeam = null;
		
		boolean seekingEncampment = false;
		if (rc.getType() == RobotType.SOLDIER) {
			navSystem = new NavigationSystem(rc);
		}
		
		while (true) {
			try {
				myTeam = rc.getTeam();
				if (myTeam == Team.A) {
					enemyTeam = Team.B;
				} else if (myTeam == Team.B) {
					enemyTeam = Team.A;
				}
				
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						// Spawn a soldier
						Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						if (rc.canMove(dir)) {
							rc.spawn(dir);
						}
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					
					if (rc.isActive()) {
						if (!navInit) {
							navSystem = new NavigationSystem(rc);
							navMode = navSystem.navMode;
							navInit = true;
						} else {
							
							if (!navMode.hasDestination && !navMode.atDestination) {
								navSystem.setNearestEncampmentAsDestination();
							}
							navMode.runWithLimit(5);
							
							if (navMode.atDestination) {
								if (rc.getLocation().equals(navMode.destination)) {
									rc.captureEncampment(RobotType.GENERATOR);
								} else {
									navSystem.alliedEncampments.put(navMode.destination, true);
									navSystem.setNearestEncampmentAsDestination();
								}
							}
						}
					}
					
				} // end soldier

				// End turn
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
