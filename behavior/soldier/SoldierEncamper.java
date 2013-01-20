package team122.behavior.soldier;

import java.util.HashMap;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import team122.RobotInformation;
import team122.behavior.Behavior;
import team122.behavior.IComBehavior;
import team122.communication.Communicator;
import team122.robot.Soldier;

public class SoldierEncamper extends Behavior implements IComBehavior {

	public Soldier robot;
	public boolean encamp = true;
	public RobotType encampmentType;
	public boolean capturingNearHQ = false;
	public int encampmentToTry = -1;
	public int initialData;
	public RobotInformation info;

	public SoldierEncamper(Soldier robot) {
		this.robot = robot;
		info = robot.info;
	}

	/**
	 * When we start we determine if there is any allied encampments
	 */
	public void start() throws GameActionException {
		robot.info.setEncampmentsAndSort();

		// Setups the data : type and what generator to start with.
		int camperType = robot.initialData % 100;
		encampmentToTry = (robot.initialData) / 1000;

		// We are readying the generator encamper.
		if (camperType == SoldierSelector.GENERATOR_ENCAMPER || camperType == SoldierSelector.SUPPLIER_ENCAMPER) {

			// These encampments should be furthest away from enemy hq.
			encampmentType = camperType == SoldierSelector.GENERATOR_ENCAMPER ? 
					RobotType.GENERATOR : RobotType.SUPPLIER;
		} else {
			
			//TODO: Other encampments.

			// Other encampments should be close to the HQ.
			encampmentType = RobotType.ARTILLERY;
		}
		
		encampmentToTry = 0;

		_setDestination();
	}

	/**
	 * echos the com behavior to the communicator.
	 */
	@Override
	public void comBehavior() throws GameActionException {
		robot.com.increment(Communicator.CHANNEL_ENCAMPER_COUNT);
	}

	@Override
	public void run() throws GameActionException {
		if (robot.rc.isActive()) {

			// Moves closer to the destination if it has one.
			if (robot.navSystem.navMode.hasDestination) {

				if (robot.navSystem.navMode.attemptsExausted()) {
					_setDestination();
				}
				robot.navSystem.navMode.move();

				// It is at the destination.
			} else if (robot.navSystem.navMode.atDestination) {

				// Capture encampment.
				if (robot.navSystem.navMode.destination.equals(robot.rc
						.getLocation())) {
					robot.rc.captureEncampment(encampmentType);

					// Else it cannot capture encampment, someone is there.
				} else {
					GameObject obj = robot.rc.senseObjectAtLocation(robot.navSystem.navMode.destination);
					
					if (obj == null) {
						//Killed Encampment so move then capture.
						robot.rc.move(robot.rc.getLocation().directionTo(robot.navSystem.navMode.destination));
						
					} else if (obj.getTeam() != info.myTeam) {
						//Wait until dead.
						return;
					} else {
						info.alliedEncampments.put(robot.navSystem.navMode.destination, true);
						_setDestination();
					}
				}
			}
		}
	}

	/**
	 * only will try to capture half the encampments.
	 */
	@Override
	public boolean pre() {
		return info.totalEncampments / 2 > info.alliedEncampments.size();
	}

	/**
	 * Sets the destination of the robot encamper.
	 */
	private boolean _setDestination() {
		MapLocation encamp;

		// Depending on the initial data is what the encamper should do.
		// One big and obvious thing is that the encamper should pick
		// encampments that are away
		// from the enemy if its a supplier and generator.
		// A good rule of thumb is that any generator/supplier should be picked
		// from the end of the
		// enemy list.
		if (encampmentType == RobotType.GENERATOR || encampmentType == RobotType.SUPPLIER) {
			int loc;
			int offset = encampmentToTry;
			MapLocation mapLoc = null;
			
			while (offset < info.totalEncampments) {
				loc = miniMaxLocation(offset);
				mapLoc = info.encampments[loc];
				
				if (info.alliedEncampments.containsKey(mapLoc) ||
					info.hq.distanceSquaredTo(mapLoc) <= ARTILLERY_MED_BAY_DISTANCE) {
					offset++;
					continue;
				}
				
				robot.navSystem.navMode.setDestination(mapLoc);
				break;
			}
		}

		return false;
	}
	
	/**
	 * Attempting to find the 
	 * @param start
	 * @return
	 */
	private int miniMaxLocation(int offset) {
		
		int max = -1, min = -1, index = 0;
		for (int i = 0, len = info.totalEncampments; i < len; i++) {
			if (max == -1) {
				max = info.enemyDistances[i];
				min = info.encampmentsDistances[i];
				index = i;
			} else if (info.encampmentsDistances[i] < min && info.enemyDistances[i] > max) {
				index = i;
				max = info.enemyDistances[i];
				min = info.encampmentsDistances[i];
			}
		}
		
		return index;
	}

	public final static int ARTILLERY_MED_BAY_DISTANCE = 58;
}
