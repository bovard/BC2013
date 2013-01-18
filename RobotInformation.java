package team122;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class RobotInformation {
	public Team myTeam;
	public Team enemyTeam;
	public MapLocation hq;
	public MapLocation enemyHq;
	public int id;
	public int width;
	public int height;

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
		id = rc.getRobot().getID();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
	}
}
