package team122.behavior.lib;

import java.util.ArrayList;
import java.util.HashMap;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;
import team122.robot.Soldier;

public class SoldierDefenseMiner extends Behavior {
	
	public Soldier robot;
	public HashMap<MapLocation, Boolean> mineSpots;
	public boolean init;
	public int radius;
	public int radiusSquared;
	
	public SoldierDefenseMiner(Soldier robot) {
		super();
		this.robot = robot;
		mineSpots = new HashMap<MapLocation, Boolean>();
		init = false;

		radius = 0;
	}

	@Override
	public void start() {
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
			
			if (rand.nextInt() % 3 == 0) {
				_pruneDestinations();
			}
			if (robot.navMode.atDestination) {
				Team t = robot.rc.senseMine(robot.navMode.destination);
				if (t != robot.info.myTeam) {
					robot.rc.layMine();
				}
				_setDestination();
			} else {
				if (robot.navMode.hasDestination) {
					robot.navMode.move();
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
		
		robot.navMode.setDestination(loc);
	}
	
	private void _pruneDestinations() {
		MapLocation[] locs = robot.rc.senseMineLocations(robot.rc.getLocation(), radiusSquared, robot.info.myTeam);
		for (int i = 0, len = locs.length; i < len; i++) {
			mineSpots.remove(locs[i]);
		}
	}

	@Override
	public boolean pre() {
		return true;
	}
	
	private void _setMiningLocations() {
		radius++;
		radiusSquared = radius * radius;

		MapLocation hqLoc = robot.info.hq;
		MapLocation upperLeft = _boundToBoard(hqLoc.add(Direction.NORTH_WEST, radius / 2));
		MapLocation lowerRight = _boundToBoard(hqLoc.add(Direction.SOUTH_EAST, radius / 2));
		
		for (int i = upperLeft.x; i <= lowerRight.x; i++) {
			for (int j = upperLeft.y; j <= lowerRight.y; j++) {
				mineSpots.put(new MapLocation(i, j), true);
			}
		}
		mineSpots.remove(robot.info.hq);
	}

	/**
	 * Bounds the location to the board so if its off the board it will bound it to the board.
	 * @param loc
	 * @return
	 */
	private MapLocation _boundToBoard(MapLocation loc) {
		if (robot.rc.senseTerrainTile(loc) == TerrainTile.OFF_MAP) {
			int newX = loc.x, newY = loc.y;
			
			if (newX < 0) {
				newX = 0;
			} else if (newX >= robot.info.width) {
				newX = robot.info.width - 1;
			}
			
			if (newY < 0) {
				newY = 0;
			} else if (newY >= robot.info.height) {
				newY = robot.info.height - 1;
			}
			
			return new MapLocation(newX, newY);
		}
		return loc;
	}
	public static final int MAX_RADIUS = 20;
}
