package team122.behavior.lib;

import java.util.ArrayList;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;
import team122.robot.Soldier;

public class SoldierDefenseMiner extends Behavior {
	
	public Soldier robot;
	public ArrayList<MapLocation> mineSpots;
	public boolean init;
	public int radius = SoldierDefenseMiner.ROBOT_MINE_RADIUS;
	
	public SoldierDefenseMiner(Soldier robot) {
		
		this.robot = robot;
		mineSpots = new ArrayList<MapLocation>();
		init = false;
	}

	@Override
	public void start() {
		if (!init) {
			init = true;
			
			//We need to create a latice around our HQ
			MapLocation hqLoc = robot.info.hq;
			MapLocation upperLeft = hqLoc.add(Direction.NORTH_WEST, radius / 2);
			
			if (robot.rc.senseTerrainTile(upperLeft) == TerrainTile.OFF_MAP) {
				int newX = upperLeft.x, newY = upperLeft.y;
				if (newX < 0) {
					newX = 0;
				}
				if (newY < 0) {
					newY = 0;
				}
				
			}
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
		
	}

	@Override
	public boolean pre() {
		return true;
	}

	public static final int ROBOT_MINE_RADIUS = 20;
}
