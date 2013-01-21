package team122;

import java.util.ArrayList;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class MapInformation {

	public MapLocation[] neutralMines;
	public double mineDensity;
	public RobotController rc;
	public int width;
	public int height;
	public MapLocation center;
	public ArrayList<MapLocation> centerPath;
	public MapLocation hq;
	public MapLocation enemyHq;
	public Direction dirToEnemyHq;
	public int distanceToEnemyHq;
	
	public MapInformation(RobotController rc) {
		this.rc = rc;
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		center = new MapLocation(width / 2, height / 2);
		hq = rc.senseHQLocation();
		enemyHq = rc.senseEnemyHQLocation();
		dirToEnemyHq = hq.directionTo(enemyHq);
		distanceToEnemyHq = hq.distanceSquaredTo(enemyHq);
		centerPath = new ArrayList<MapLocation>();
		
		MapLocation startingLoc = hq.add(dirToEnemyHq).add(dirToEnemyHq).add(dirToEnemyHq).add(dirToEnemyHq);
		
		//Calculates the artillery center
		while (hq.distanceSquaredTo(startingLoc) < distanceToEnemyHq) {
			centerPath.add(startingLoc);
			
			//150 Bytecodes for this operation. 
			startingLoc = startingLoc.add(startingLoc.directionTo(enemyHq));
			startingLoc = startingLoc.add(startingLoc.directionTo(enemyHq));
			startingLoc = startingLoc.add(startingLoc.directionTo(enemyHq));
			startingLoc = startingLoc.add(startingLoc.directionTo(enemyHq));
			startingLoc = startingLoc.add(startingLoc.directionTo(enemyHq));
			startingLoc = startingLoc.add(startingLoc.directionTo(enemyHq));
			startingLoc = startingLoc.add(startingLoc.directionTo(enemyHq));
			startingLoc = startingLoc.add(startingLoc.directionTo(enemyHq));
			startingLoc = startingLoc.add(startingLoc.directionTo(enemyHq));
			startingLoc = startingLoc.add(startingLoc.directionTo(enemyHq));
		}
	}
	
	public double updateMineDensity() throws GameActionException {
		neutralMines = rc.senseMineLocations(center, width * 1000, Team.NEUTRAL);
		mineDensity = neutralMines.length / (width * height);
		
		return mineDensity;
	}
	
	/**
	 * if its in the range of the center path.
	 * @param loc
	 * @param rSquared
	 * @return
	 */
	public boolean inRangeOfCenterPath(MapLocation loc, int rSquared) {
		for (int i = 0, len = centerPath.size(); i < len; i++) {
			if (centerPath.get(i).distanceSquaredTo(loc) > rSquared) {
				return true;
			}
		}
		return false;
	}
}
