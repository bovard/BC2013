package team122.behavior.soldier;

import java.util.HashMap;

import team122.behavior.Behavior;
import team122.communication.Communicator;
import team122.navigation.SoldierMove;
import team122.robot.Soldier;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class SoldierDefenseMiner 
		extends Behavior{
	
	public Soldier robot;
	public HashMap<MapLocation, Boolean> mineSpots;
	public HashMap<MapLocation, Boolean> seenSpots;
	public boolean init;
	public int radius;
	public int radiusSquared;
	
	public SoldierDefenseMiner(Soldier robot2) {
		super();
		this.robot = robot2;
		mineSpots = new HashMap<MapLocation, Boolean>();
		seenSpots = new HashMap<MapLocation, Boolean>();
		init = false;

		radius = 0;
	}

	@Override
	public void start() {
		robot.incChannel = Communicator.CHANNEL_MINER_COUNT;
		if (!init) {
			init = true;
			_setMiningLocations();
		}
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
			if (mineSpots.size() == 0) {
				_setMiningLocations();
			}
			
			if (robot.move.atDestination()) {
				Team t = robot.rc.senseMine(robot.move.destination);
				if (t != robot.info.myTeam) {
					robot.rc.layMine();
				} else {
				}
				_setDestination();
			} else {
				if (robot.move.destination != null) {
					robot.move.move();
				} else {
					_setDestination();
				}
			}
		}
	}
	
	/**
	 * Sets the destination of the robot and removes one of the
	 * mines.
	 */
	private void _setDestination() {
		MapLocation loc = (MapLocation)mineSpots.keySet().toArray()[0];
		mineSpots.remove(loc);
		robot.move.destination = loc;
		
	}

	@Override
	public boolean pre() {
		return !robot.enemyInMelee;
	}
	
	private void _setMiningLocations() {
		MapLocation hqLoc = robot.info.hq;
		MapLocation seenUpperLeft = SoldierMove.BoundToBoard(robot, hqLoc.add(Direction.NORTH_WEST, radius / 2));
		MapLocation seenLowerRight = SoldierMove.BoundToBoard(robot, hqLoc.add(Direction.SOUTH_EAST, radius / 2));

		radius++;
		radiusSquared = radius * radius;
		MapLocation upperLeft = SoldierMove.BoundToBoard(robot, hqLoc.add(Direction.NORTH_WEST, radius / 2));
		MapLocation lowerRight = SoldierMove.BoundToBoard(robot, hqLoc.add(Direction.SOUTH_EAST, radius / 2));
		
		MapLocation created;
		if (robot.rc.hasUpgrade(Upgrade.PICKAXE)) {
			for (int i = upperLeft.x; i <= lowerRight.x; i += 2) {
				for (int j = upperLeft.y; j <= lowerRight.y; j += 2) {
					
					if (i >= seenUpperLeft.x && i <= seenLowerRight.x &&
						j >= seenUpperLeft.y && j <= seenLowerRight.y) {
						continue;
					}
					
					created = new MapLocation(i, j);
					if (!seenSpots.containsKey(created)) {
						mineSpots.put(created, true);
					}
				}
			}	
		} else {
			for (int i = upperLeft.x; i <= lowerRight.x; i++) {
				for (int j = upperLeft.y; j <= lowerRight.y; j++) {
					
					if (i >= seenUpperLeft.x && i <= seenLowerRight.x &&
						j >= seenUpperLeft.y && j <= seenLowerRight.y) {
						continue;
					}
					
					created = new MapLocation(i, j);
					if (!seenSpots.containsKey(created)) {
						mineSpots.put(created, true);
					}
				}
			}	
		}
	}
	public static final int MAX_RADIUS = 20;
}
