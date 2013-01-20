package team122.combat;

import team122.robot.TeamRobot;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;

public class MoveCalculator {
	
	private CombatHashMap combatHash;
	private MapHashMap mapHash;
	private char[][] map;
	private TeamRobot robot;
	
	
	public MoveCalculator(TeamRobot robot) {
		combatHash = new CombatHashMap();
		mapHash = new MapHashMap();
		this.robot = robot;
	}
	
	public Direction[] calculateMove (Robot[] nearby, MapLocation loc) throws GameActionException{
		makeMap(nearby, loc);
		return null;
	}

	private void makeMap(Robot[] nearby, MapLocation loc) throws GameActionException{
		map = new char[7][7];
		
		for (Robot r : nearby) {
			RobotInfo info = robot.rc.senseRobotInfo(r);
			int x = info.location.x - loc.y;
			int y = info.location.y - loc.y;
			if (info.team == robot.info.enemyTeam) {
				map[3+x][3+y] = 'e';
			} else {
				map[3+x][3+y] = 'a';
			}
		}
	}
	
	private void _evalMove(int x, int y) {
		
		
	}
	
}
