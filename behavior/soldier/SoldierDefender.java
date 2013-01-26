package team122.behavior.soldier;

import team122.MapUtils;
import team122.RobotInformation;
import team122.behavior.Behavior;
import team122.communication.Communicator;
import team122.robot.Soldier;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public class SoldierDefender 
		extends Behavior{
	
	public Soldier robot;
	public boolean wing;
	public int order;
	public int orderSquared;
	public RobotInformation info;
	
	public SoldierDefender(Soldier robot2) {
		super();
		this.robot = robot2;
		info = robot.info;
	}

	@Override
	public void start() throws GameActionException {
		wing = Clock.getRoundNum() % 2 == 0;
		order = robot.com.receive(Communicator.CHANNEL_DEFENDER_COUNT, 0);
		orderSquared = order * order;
		robot.incChannel = Communicator.CHANNEL_DEFENDER_COUNT;
	}

	@Override
	public void stop() {
		// nothing needs to be done here
		
	}

	/**
	 * 
	 */
	@Override
	public void run() throws GameActionException {
		
		if (robot.rc.isActive()) {

			if (true) {//lining up) {

			} else {
				
				//Lets lay a mine!
				if (robot.rc.senseMine(robot.rc.getLocation()) == null) {
					robot.rc.layMine();
				}
			}
		}
	}

	@Override
	public boolean pre() throws GameActionException {
		return  !robot.isNukeArmed && 
				(robot.rc.senseMineRoundsLeft() > 0 || robot.rc.isActive() && !robot.enemyAtTheGates && !robot.enemyInMelee && !robot.enemyInSight);
	}
	

	/**
	 * Will move/defuse mine.
	 * @param dir
	 * @return
	 * @throws GameActionException
	 */
	private boolean move(Direction dir, int depth) throws GameActionException {
		if (depth == 8) {
			return false;
		}
		
		int moveable = MapUtils.canMove(robot.rc, info, dir);
		
		if (moveable == MapUtils.CAN_MOVE) {
			robot.rc.move(dir);
			return true;
		} else if (moveable == MapUtils.MUST_DEFUSE) {
			robot.rc.defuseMine(robot.rc.getLocation().add(dir));
			return true;
		} else {
			
			//Must avoid
			if (wing) {
				return move(dir.rotateLeft(), ++depth);
			} else {
				return move(dir.rotateRight(), ++depth);
			}
		}
	}

	public static final int LEFT_WING = 0;
	public static final int RIGHT_WING = 1;
	public static final int ROUND_DIVIDER = 75;
	public static final int MIN_DISTANCE = 25;
	public static final int MAX_DISTANCE = 400;
}
