package team122.behavior.soldier;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import team122.RobotInformation;
import team122.behavior.Behavior;
import team122.behavior.IComBehavior;
import team122.communication.CommunicationDecoder;
import team122.communication.Communicator;
import team122.navigation.SoldierMove;
import team122.robot.Soldier;

public class SoldierEncamper extends Behavior implements IComBehavior {

	public Soldier robot;
	public boolean encamp = true;
	public RobotType encampmentType;
	public RobotInformation info;
	public MapLocation encampment;
	public boolean canCapture = true;
	private SoldierMove move;
	public boolean init = false;

	public SoldierEncamper(Soldier robot) {
		this.robot = robot;
		info = robot.info;
		move = new SoldierMove(robot);
	}

	/**
	 * When we start we determine if there is any allied encampments
	 */
	public void start() throws GameActionException {

		if (!init) {
			move.destination = robot.dec.loc;
			
			if (robot.dec.groupOrEncampmentType == GENERATOR_ENCAMPER) {
				encampmentType = RobotType.GENERATOR;
			} else if (robot.dec.groupOrEncampmentType == SUPPLIER_ENCAMPER) {
				encampmentType = RobotType.SUPPLIER;
			} else if (robot.dec.groupOrEncampmentType == ARTILLERY_ENCAMPER) {
				encampmentType = RobotType.ARTILLERY;
			}
			
			init = true;
		}
	}

	/**
	 * echos the com behavior to the communicator.
	 */
	@Override
	public void comBehavior() throws GameActionException {
		if (encampmentType == RobotType.GENERATOR) {
			robot.com.increment(Communicator.CHANNEL_GENERATOR_COUNT, Clock.getRoundNum());
		} else if (encampmentType == RobotType.SUPPLIER) {
			robot.com.increment(Communicator.CHANNEL_SUPPLIER_COUNT, Clock.getRoundNum());
		} else if (encampmentType == RobotType.ARTILLERY) {
			robot.com.increment(Communicator.CHANNEL_ARTILLERY_COUNT, Clock.getRoundNum());
		}
	}

	@Override
	public void run() throws GameActionException {
		if (robot.rc.isActive()) {
			
			//Move or capture.
			if (move.destination.equals(robot.rc.getLocation())) {
				Direction dir = robot.currentLoc.directionTo(robot.info.enemyHq);
				
				// add mines
//				if (robot.rc.senseMine(robot.currentLoc.add(dir)) == null &&
//						robot.rc.canMove(dir)) {
//					MapLocation old_dest = move.destination;
//					move.destination = robot.currentLoc.add(dir);
//					move.move();
//					robot.rc.yield();
//					robot.rc.layMine();
//					robot.rc.yield();
//					move.destination = old_dest;
//					move.move();
//				} else if (robot.rc.senseMine(robot.currentLoc.add(dir.rotateLeft())) == null && 
//						robot.rc.canMove(dir.rotateLeft())) {
//					MapLocation old_dest = move.destination;
//					move.destination = robot.currentLoc.add(dir.rotateLeft());
//					move.move();
//					robot.rc.yield();
//					robot.rc.layMine();
//					robot.rc.yield();
//					move.destination = old_dest;
//					move.move();
//				} else if (robot.rc.senseMine(robot.currentLoc.add(dir.rotateRight())) == null && 
//						robot.rc.canMove(dir.rotateRight())) {
//					MapLocation old_dest = move.destination;
//					move.destination = robot.currentLoc.add(dir.rotateRight());
//					move.move();
//					robot.rc.yield();
//					robot.rc.layMine();
//					robot.rc.yield();
//					move.destination = old_dest;
//					move.move();
//				} else {
					robot.rc.captureEncampment(encampmentType);
//				}
			} else {
				move.move();
			}
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
