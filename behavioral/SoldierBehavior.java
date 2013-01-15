package team122.behavioral;

import battlecode.common.Clock;
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
		int type = rand.nextInt(500);
		
		if (type < 130) {
			this.type = ENCAMPMENT_MODE;
		} else {
			this.type = SWARM_MODE;
		}
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
								rc.captureEncampment(RobotType.SUPPLIER);
							} else {
								navSystem.alliedEncampments.put(navMode.destination, true);
								navSystem.setNearestEncampmentAsDestination();
							}
						}

					} else if (type == ATTACK_ENCAMPMENT_MODE) {
						//we need to start actually having swarm
					} else if (type == SWARM_MODE) {
						
						//We are going group up till round 200 then attack
						if (!navMode.hasDestination && !navMode.atDestination) {
							navSystem.setInitialSwarmRallyPoint();
						}
						
						if (Clock.getRoundNum() % 100 == 0) {
							navSystem.setToEnemyHQ();
						} else {
							navMode.runWithLimit(1);
						}
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
	public static final int SWARM_MODE = 2;
	public static final int MODE_COUNT = 3;
}
