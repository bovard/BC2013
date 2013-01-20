package team122.behavior.soldier;

import java.util.HashMap;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import team122.RobotInformation;
import team122.behavior.Behavior;
import team122.behavior.IComBehavior;
import team122.communication.Communicator;
import team122.robot.Soldier;

public class SoldierEncamper extends Behavior implements IComBehavior {

	public Soldier robot;
	public HashMap<MapLocation, Boolean> alliedEncampment;
	public boolean encamp = true;
	public RobotType encampmentType;
	public boolean capturingNearHQ = false;
	public int encampmentToTry = -1;
	public int initialData;
	public RobotInformation info;

	public SoldierEncamper(Soldier robot) {
		this.robot = robot;
		alliedEncampment = new HashMap<MapLocation, Boolean>();
		info = robot.info;
	}

	/**
	 * When we start we determine if there is any allied encampments
	 */
	public void start() throws GameActionException {
		robot.info.setEncampmentsAndSort();

		// Setups the data : type and what generator to start with.
		int camperType = robot.initialData % 100;
		int campToTry = (robot.initialData) / 100;

		System.out.println("CamperType: " + camperType + " :: " + campToTry);

		// We are readying the generator encamper.
		if (robot.initialData == SoldierSelector.GENERATOR_ENCAMPER) {

			// These encampments should be furthest away from enemy hq.
			encampmentToTry = info.totalEncampments - (1 + campToTry);
			encampmentType = RobotType.GENERATOR;
		} else {

			// Other encampments should be close to the HQ.
			encampmentToTry = 0;
		}

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

					_setDestination();
				}
			}
		}
	}

	@Override
	public boolean pre() {
		return true;
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
		System.out.println("Selecting Encampment: " + encampmentType);
		if (encampmentType == RobotType.GENERATOR) {

			// Generator will find the best possible fit, if there is one
			// required.
			while (encampmentToTry > -1
					&& encampmentToTry < info.totalEncampments) {
				encamp = info.encampments[encampmentToTry];

				System.out.println("Attempting Location: " + encamp + " with: "
						+ info.alliedEncampments.containsKey(encamp) + " : "
						+ info.encampmentsDistances[encampmentToTry] + " < "
						+ info.enemyDistances[encampmentToTry] + " && "
						+ info.encampmentsDistances[encampmentToTry] + " > "
						+ ARTILLERY_MED_BAY_DISTANCE);

				// The encampment we are going for should not be an encampment.
				if (!info.alliedEncampments.containsKey(encamp)
						&& info.encampmentsDistances[encampmentToTry] < info.enemyDistances[encampmentToTry]
						&& info.encampmentsDistances[encampmentToTry] > ARTILLERY_MED_BAY_DISTANCE) {

					// We have found our destination.
					robot.navSystem.navMode.setDestination(encamp);
					return true;
				}

				encampmentToTry--;
				continue;
			}
		}

		return false;
	}

	public final static int ARTILLERY_MED_BAY_DISTANCE = 58;
}
