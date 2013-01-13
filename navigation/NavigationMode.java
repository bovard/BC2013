package team122.navigation;

import java.util.HashMap;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.GameConstants;
import battlecode.common.TerrainTile;

/**
 * Defines a navigation mode.
 * @author michaelpaulson
 */
public abstract class NavigationMode {
	public MapLocation destination;
	public boolean hasDestination;
	public boolean atDestination;
	public Direction direction;
	public RobotController rc;
	public int width;
	public int height;
	
	public NavigationMode(RobotController rc) {
		this.rc = rc;
		hasDestination = false;
		atDestination = false;
		determineMapSize();
	}
	
	/**
	 * the algorithm to run for the navigation.
	 * @return
	 */
	public abstract boolean run();
	
	/**
	 * Sets the destination
	 * @param location
	 * @return
	 */
	public boolean setDestination(MapLocation location) {
		if (rc.senseTerrainTile(location) == TerrainTile.OFF_MAP) {
			destination = null;
			return false;
		}
		destination = location;
		hasDestination = true;
		atDestination = false;
		return true;
	}

    /**
     * Checks to see if the robot is currently at it's destination
     * @return if the robot is at its destination MapLocation
     */
    public boolean isAtDestination() {
    	return rc.getLocation().equals(destination);
    }
	
	/**
	 * runs the same algorithm except with a limited amount of runs.
	 * Better for yield.
	 * @param limit
	 */
	public abstract void runWithLimit(int limit);
	
	/**
	 * Determines the map size.
	 */
	private void determineMapSize() {
		boolean find = true;
		
		int i = GameConstants.MAP_MIN_WIDTH;
		while (find) {
			
			TerrainTile tile = rc.senseTerrainTile(new MapLocation(i, GameConstants.MAP_MIN_HEIGHT));
			if (tile.equals(TerrainTile.OFF_MAP)) {
				width = i - 1;
				break;
			}
			
			i++;
		}
		
		i = GameConstants.MAP_MIN_HEIGHT;
		while (find) {
			
			TerrainTile tile = rc.senseTerrainTile(new MapLocation(GameConstants.MAP_MIN_WIDTH, i));
			if (tile.equals(TerrainTile.OFF_MAP)) {
				height = i - 1;
				break;
			}
			
			i++;
		}
	}
	
	public static final int ASTAR_MODE = 1;
	public static final int CITY_BLOCK = 2;
}
