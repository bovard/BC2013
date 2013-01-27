package team122;

import java.util.HashMap;

import team122.communication.Communicator;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class RobotInformation {
	public Team myTeam;
	public Team enemyTeam;
	public MapLocation hq;
	public MapLocation enemyHq;
	public MapLocation center;
	public int id;
	public int width;
	public int height;
	public int enemyHqDistance;
	public RobotController rc;
	public Direction enemyDir;
	public double mineDensity;
	public MapLocation[] neutralMines;

	/**
	 * Will construct a robot information. These are common operations that
	 * require bytecode execution and can be saved by storing the information.
	 * 
	 * @param rc
	 */
	public RobotInformation(RobotController rc) {
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
		hq = rc.senseHQLocation();
		enemyHq = rc.senseEnemyHQLocation();
		enemyHqDistance = hq.distanceSquaredTo(enemyHq);
		id = rc.getRobot().getID();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		center = new MapLocation(width / 2, height / 2);
		enemyDir = hq.directionTo(enemyHq);
		this.rc = rc;
	}
	
	public double updateMineDensity() throws GameActionException {
		neutralMines = rc.senseMineLocations(center, width * 1000, Team.NEUTRAL);
		mineDensity = neutralMines.length / (width * height);
		
		return mineDensity;
	}
}
