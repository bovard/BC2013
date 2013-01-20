package team122.combat;

import team122.robot.TeamRobot;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;

public class MoveCalculator {
	
	private CombatHashMap combatHash;
	private char[][] map;
	private TeamRobot robot;
	
	
	public MoveCalculator(TeamRobot robot) {
		combatHash = new CombatHashMap();
		this.robot = robot;
	}
	
	public Direction calculateMove (Robot[] nearby, MapLocation loc) throws GameActionException{
		System.out.println("Starting caclMove at " + Clock.getBytecodeNum());
		_makeMap(nearby, loc);
		
		int[] xyDir = new int[2];
		int score = -10;
		int temp;
		
		for(int x=-1;x<=1;x++) {
			for (int y=1;y>=-1;y--) {
				temp = _evalMove(3+x,3+y);
				if (temp > score) {
					score = temp;
					xyDir[0] = x;
					xyDir[1] = y;
				}
			}
		}
		
		System.out.println("Ending calcMove at " + Clock.getBytecodeNum());
		
		return _xyToDir(xyDir[0], xyDir[1]);
	}
	
	private Direction _xyToDir(int x, int y) {
		if (x > 0) {
			if (y > 0) 
				return Direction.NORTH_EAST;
			if (y == 0) 
				return Direction.EAST;
			else
				return Direction.SOUTH_EAST;
		} else if ( x < 0) {
			if (y > 0)
				return Direction.NORTH_WEST;
			if (y == 0)
				return Direction.WEST;
			else
				return Direction.SOUTH_WEST;
		} else {
			if (y > 0)
				return Direction.NORTH;
			if (y == 0)
				return Direction.NONE;
			else
				return Direction.SOUTH;
		}
	}

	private void _makeMap(Robot[] nearby, MapLocation loc) throws GameActionException{
		map = new char[7][7];
		for (int i=0;i<7;i++) {
			for (int j=0;j<7;j++) {
				map[i][j] = 'e';
			}
		}
		
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
	
	private int _evalMove(int x, int y) {
		int score = 0;
		int i, j;
		String hash = "";
		
		// pointed up!
		for (i = 1; i >= -1; i-- ) {
			for (j = 0; j <= 2; j++ ) {
				hash += map[x+i][y+j];
			}
		}
		score += combatHash.m.get(hash);
		
		// pointed right
		for (i = 0; i <= 2; i++ ) {
			for (j = 1; j >= -1; j-- ) {
				hash += map[x+i][y+j];
			}
		}
		score += combatHash.m.get(hash);
		
		// pointed left
		for (i = 0; i >= -2; i-- ) {
			for (j = -1; j <= 1; j++ ) {
				hash += map[x+i][y+j];
			}
		}
		score += combatHash.m.get(hash);
		
		// pointed down
		for (i = 1; i >= -1; i-- ) {
			for (j = 0; j >= -2; j-- ) {
				hash += map[x+i][y+j];
			}
		}
		score += combatHash.m.get(hash);
		
		return score;
	}
	
}
