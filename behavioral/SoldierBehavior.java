package team122.behavioral;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team122.navigation.NavigationMode;
import team122.navigation.NavigationSystem;

public class SoldierBehavior extends Behavior {

	public NavigationSystem navSystem = null;
	public NavigationMode navMode = null;
	
	public SoldierBehavior(RobotController rc) {
		super(rc);
		navSystem = new NavigationSystem(rc);
		navMode = navSystem.navMode;
	}
	
	/**
	 * Runs the behavioral loop for the soldier.
	 */
	public void behave() {
		while (true) {
		
			try {
				if (rc.isActive()) {
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

				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
