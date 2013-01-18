package team122.navigation;

import java.util.Random;

import team122.RobotInformation;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;

/**
 * Defines a navigation mode.
 * @author michaelpaulson
 */
public abstract class NavigationMode {
	public MapLocation destination;
	public int attemptsBounds;
	public int destinationTries;
	public boolean hasDestination;
	public boolean atDestination;
	public Direction direction;
	public RobotController rc;
	public RobotInformation info;
	public int width;
	public int height;
    protected Random rand = new Random();
    
	public NavigationMode(RobotController rc, RobotInformation info) {
		this.rc = rc;
		this.info = info;
		
		hasDestination = false;
		atDestination = false;
    	rand.setSeed(Clock.getRoundNum());
    	width = rc.getMapWidth();
    	height = rc.getMapHeight();
	}
	
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
		destinationTries = 0;
		attemptsBounds = location.distanceSquaredTo(rc.getLocation()) * 2;
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
     * If the tries * tries > distanceSquared * 2
     * @return
     */
	public boolean attemptsExausted() {
		return destinationTries * destinationTries > attemptsBounds;
	}
	
	/**
	 * runs the same algorithm except with a limited amount of runs.
	 * Better for yield.
	 * @param limit
	 * @throws GameActionException 
	 */
	public abstract void move() throws GameActionException;
	
	public static final int ASTAR_MODE = 1;
	public static final int CITY_BLOCK = 2;
}
