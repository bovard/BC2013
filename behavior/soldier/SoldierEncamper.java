package team122.behavior.soldier;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import team122.EncampmentSorter;
import team122.RobotInformation;
import team122.behavior.Behavior;
import team122.behavior.IComBehavior;
import team122.communication.Communicator;
import team122.robot.Soldier;

public class SoldierEncamper extends Behavior implements IComBehavior {

	public Soldier robot;
	public boolean encamp = true;
	public RobotType encampmentType;
	public int encampmentToTry = -1;
	public int initialData;
	public RobotInformation info;
	public boolean canCapture = true;

	public SoldierEncamper(Soldier robot) {
		this.robot = robot;
		info = robot.info;
	}

	/**
	 * When we start we determine if there is any allied encampments
	 */
	public void start() throws GameActionException {
		
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
			//TODO: Move.
		}
	}

	/**
	 * only will try to capture half the encampments.
	 */
	@Override
	public boolean pre() {
		return canCapture;
	}
}
