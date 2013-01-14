package team122.behavioral;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team122.navigation.NavigationMode;
import team122.navigation.NavigationSystem;

public class SoldierBehavior extends Behavior {

	public NavigationSystem navSystem = null;
	public NavigationMode navMode = null;
	public int type;
	
	public SoldierBehavior(RobotController rc) {
		super(rc);
		navSystem = new NavigationSystem(rc);
		navMode = navSystem.navMode;
		type = rand.nextInt(MODE_COUNT);		
	}
	
	/**
	 * Runs the behavioral loop for the soldier.
	 */
	public void behave() {
		while (true) {
		
			try {
				if (rc.isActive()) {
					
					//We can even strategy pattern this out. but we can save bytecodes.
					if (type == ENCAMPMENT_MODE) {
						if (!navMode.hasDestination && !navMode.atDestination) {
							navSystem.setNearestEncampmentAsDestination();
						}
						navMode.runWithLimit(1);
						
						if (navMode.atDestination) {
							if (rc.getLocation().equals(navMode.destination)) {
								rc.captureEncampment(RobotType.ARTILLERY);
							} else {
								navSystem.alliedEncampments.put(navMode.destination, true);
								navSystem.setNearestEncampmentAsDestination();
							}
						}
						
					} else if (type == ATTACK_ENCAMPMENT_MODE) {
						
					}
				}

				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static final int ENCAMPMENT_MODE = 0;
	public static final int ATTACK_ENCAMPMENT_MODE = 1;
	public static final int MODE_COUNT = 2;
}
