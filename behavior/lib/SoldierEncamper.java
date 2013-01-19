package team122.behavior.lib;

import java.util.HashMap;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import team122.communication.Communicator;
import team122.robot.Soldier;

public class SoldierEncamper  
		extends Behavior
		implements IComBehavior {
	
	public Soldier robot;
	public HashMap<MapLocation, Boolean> alliedEncampment;
	public boolean encamp = true;
	public RobotType encampmentType;
	public boolean capturingNearHQ = false;
	public int encampmentToTry = -1;
	public int initialData;
	
	public SoldierEncamper(Soldier robot) {
		this.robot = robot;
		alliedEncampment = new HashMap<MapLocation, Boolean>();
	}
	
	/**
	 * When we start we determine if there is any allied encampments
	 */
	public void start() throws GameActionException {
		robot.info.setEncampmentsAndSort();
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
			
			//Moves closer to the destination if it has one.
			if (robot.navSystem.navMode.hasDestination) {
				
				if (robot.navSystem.navMode.attemptsExausted()) {
					_setDestination();
				}
				robot.navSystem.navMode.move();
				
			//It is at the destination.
			} else if (robot.navSystem.navMode.atDestination) {
				
				//Capture encampment.
				if (robot.navSystem.navMode.destination.equals(robot.rc.getLocation())) {
					robot.rc.captureEncampment(encampmentType);
					
				//Else it cannot capture encampment, someone is there.
				} else {
					
					_setDestination();
				}
			}
		}
	}

	@Override
	public boolean pre() {
		if (encamp) {
			//Communicates active soldier encamper.
//			robot.com.communicate(Communicator., data)
		}
		return encamp;
	}
	
	/**
	 * Sets the destination of this encampment.
	 */
	private void _setDestination() {
		if (encampmentToTry < robot.info.encampments.length) {
			MapLocation loc = robot.info.encampments[++encampmentToTry];
			
			while (robot.info.alliedEncampments.containsKey(loc)) {
				loc = robot.info.encampments[++encampmentToTry];
			}
			
			if (robot.info.encampmentsDistances[encampmentToTry] < ARTILLERY_MED_BAY_DISTANCE) {
				encampmentType = RobotType.ARTILLERY;
			} else {
				encampmentType = rand.nextInt() % 2 == 0 ? RobotType.GENERATOR : RobotType.SUPPLIER;
			}
			
			robot.navSystem.navMode.setDestination(loc);
		} else {
			encamp = false;
		}
	}
	
	private final static int ARTILLERY_MED_BAY_DISTANCE = 58;
}
