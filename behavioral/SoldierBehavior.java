package team122.behavioral;

import team122.communication.Communicator;
import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team122.RobotInformation;
import team122.navigation.NavigationMode;
import team122.navigation.NavigationSystem;

public class SoldierBehavior extends Behavior {

	public NavigationSystem navSystem = null;
	public NavigationMode navMode = null;
	public int type = SoldierBehavior.SELECT_MODE;
	
	public SoldierBehavior(RobotController rc, RobotInformation info) {
		super(rc, info);
		navSystem = new NavigationSystem(rc, info);
		navMode = navSystem.navMode;
	}
	
	/**
	 * Runs the behavioral loop for the soldier.
	 */
	public void behave() {
		while (true) {
		
			try {
				if (rc.isActive()) {
					if (type == SELECT_MODE) {
						type = com.receive(Communicator.CHANNEL_COMMUNICATE_SOLDIER_MODE, SoldierBehavior.SWARM_MODE);
						System.out.println("SELECTING: " + type);
					}
					
					switch (type) {
						case ENCAMPMENT_MODE: 
							if (!navMode.hasDestination && !navMode.atDestination) {
								navSystem.setNearestEncampmentAsDestination();
							}
							navMode.move();
							
							if (navMode.atDestination) {
								if (rc.getLocation().equals(navMode.destination)) {
									rc.captureEncampment(rand.nextInt() % 5 == 0 ? RobotType.GENERATOR : RobotType.SUPPLIER);
								} else {
									navSystem.alliedEncampments.put(navMode.destination, true);
									navSystem.setNearestEncampmentAsDestination();
								}
							} else if (navMode.attemptsExausted()) {
								navSystem.forfeitNearestEncampment();
							}							
							break;
							
						case ATTACK_ENCAMPMENT_MODE:
							
							break;
							
						case SWARM_MODE:
							if (!navMode.hasDestination && !navMode.atDestination) {
								navSystem.setInitialSwarmRallyPoint();
							}
							
							if (Clock.getRoundNum() % 100 == 0) {
								navSystem.setToEnemyHQ();
							} else {
								navMode.move();
							}
							break;
					}
				}

				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
				
				if (type == SELECT_MODE) {
					type = SWARM_MODE;
				}
			}
		}
	}

	public static final int SELECT_MODE = -1;
	public static final int ENCAMPMENT_MODE = 0;
	public static final int ATTACK_ENCAMPMENT_MODE = 1;
	public static final int SWARM_MODE = 2;
	public static final int MODE_COUNT = 3;
	
	public static final int GROUP_COMMAND_GROUP_UP = 0;
}
