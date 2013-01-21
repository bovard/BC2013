package team122.behavior.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import team122.RobotInformation;
import team122.behavior.Behavior;
import team122.behavior.IComBehavior;
import team122.communication.CommunicationDecoder;
import team122.communication.Communicator;
import team122.robot.Soldier;

public class SoldierEncamper extends Behavior implements IComBehavior {

	public Soldier robot;
	public boolean encamp = true;
	public RobotType encampmentType;
	public RobotInformation info;
	public MapLocation encampment;
	public boolean canCapture = true;

	public SoldierEncamper(Soldier robot) {
		this.robot = robot;
		info = robot.info;
		
		//Grabs the communcation for the encamp location.
		try {
			
			//Gets the decoder, now will update
			CommunicationDecoder decoder = robot.com.receiveWithLocation(Communicator.CHANNEL_ENCAMPER_LOCATION);
			encampment = decoder.location;
			
			if (decoder.command == GENERATOR_ENCAMPER) {
				encampmentType = RobotType.GENERATOR;
			} else if (decoder.command == SUPPLIER_ENCAMPER) {
				encampmentType = RobotType.SUPPLIER;
			} else if (decoder.command == ARTILLERY_ENCAMPER) {
				encampmentType = RobotType.ARTILLERY;
			}
			
			System.out.println("Spawning a soldier with : " + encampmentType + " Specified for " + encampment);
		} catch (Exception e) {
			
			robot.initialMode = SoldierSelector.SOLDIER_DEFENDER;
			canCapture = false;
		}
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
			TOOD: BOvard!
		}
	}

	/**
	 * only will try to capture half the encampments.
	 */
	@Override
	public boolean pre() {
		return canCapture;
	}

	/**
	 * The comm encoder will take care of the * 1000000
	 */
	public static final int GENERATOR_ENCAMPER = 1;
	public static final int SUPPLIER_ENCAMPER = 2;
	public static final int ARTILLERY_ENCAMPER = 3;
}
