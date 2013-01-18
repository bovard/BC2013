package team122.behavior.lib;

import java.util.HashMap;

import team122.robot.Soldier;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Team;
import battlecode.common.TerrainTile;
import battlecode.common.Upgrade;

public class SoldierDefenseMiner extends Behavior {
	
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
			
			if (robot.navSystem.navMode.atDestination) {
				Team t = robot.rc.senseMine(robot.navSystem.navMode.destination);
				if (t != robot.info.myTeam) {
					robot.rc.layMine();
				} else {
				}
				_setDestination();
			} else {
				if (robot.navSystem.navMode.hasDestination) {
					robot.navSystem.navMode.move();
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
		robot.navSystem.navMode.setDestination(loc);
		
	}

	@Override
	public boolean pre() {
		return true;
	}
	
	private void _setMiningLocations() {
		MapLocation hqLoc = robot.info.hq;
		MapLocation seenUpperLeft = _boundToBoard(hqLoc.add(Direction.NORTH_WEST, radius / 2));
		MapLocation seenLowerRight = _boundToBoard(hqLoc.add(Direction.SOUTH_EAST, radius / 2));

		radius++;
		radiusSquared = radius * radius;
		MapLocation upperLeft = _boundToBoard(hqLoc.add(Direction.NORTH_WEST, radius / 2));
		MapLocation lowerRight = _boundToBoard(hqLoc.add(Direction.SOUTH_EAST, radius / 2));
		
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
