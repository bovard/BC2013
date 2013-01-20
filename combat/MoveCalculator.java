package team122.combat;

import team122.robot.TeamRobot;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class MoveCalculator {
	
	private CombatHashMap combatHash;
	private char[][] map;
	private TeamRobot robot;
	
	
	public MoveCalculator(TeamRobot robot) {
		combatHash = new CombatHashMap();
		this.robot = robot;
	}
	
	public boolean load() {
		if (combatHash.count < combatHash.TO_LOAD) {
			combatHash.load();
			return false;
		}
		return true;
	}
	
	public Direction calculateMove (Robot[] nearby, MapLocation loc) throws GameActionException{
		int time = -Clock.getBytecodeNum();
		System.out.println("Starting caclMove at " + -time);
		_makeMap(nearby, loc);
		
		int[] xyDir = new int[2];
		int score = -1000;
		int temp;
		
		for(int x=-1;x<=1;x++) {
			for (int y=1;y>=-1;y--) {
				if (map[3+x][3+y] == 'o') {
					temp = _evalMove(3+x,3+y,x,y);
					if (temp > score) {
						score = temp;
						xyDir[0] = x;
						xyDir[1] = y;
					}
				}
				
			}
		}
		
		System.out.println("Ending calcMove at " + Clock.getBytecodeNum());
		
		Direction dir = _xyToDir(xyDir[0], xyDir[1]); 
		
		return dir;
	}
	
	private Direction _xyToDir(int x, int y) {
		if (x > 0) {
			if (y > 0) 
				return Direction.SOUTH_EAST;
			if (y == 0) 
				return Direction.EAST;
			else
				return Direction.NORTH_EAST;
		} else if ( x < 0) {
			if (y > 0)
				return Direction.SOUTH_WEST;
			if (y == 0)
				return Direction.WEST;
			else
				return Direction.NORTH_WEST;
		} else {
			if (y > 0)
				return Direction.SOUTH;
			if (y == 0)
				return Direction.NONE;
			else
				return Direction.NORTH;
		}
	}

	private void _makeMap(Robot[] nearby, MapLocation loc) throws GameActionException{
		int time = - Clock.getBytecodeNum();
		map = new char[7][7];
		for (int i=0;i<7;i++) {
			for (int j=0;j<7;j++) {
				map[i][j] = 'o';
			}
		}
		
		for (Robot r : nearby) {
			RobotInfo info = robot.rc.senseRobotInfo(r);
			int x = info.location.x - loc.y;
			int y = info.location.y - loc.y;
			if (Math.abs(x) < 3 && Math.abs(y) < 3) {
				if (info.team == robot.info.enemyTeam) {
					map[3+x][3+y] = 'e';
				} else { 
					map[3+x][3+y] = 'a';
				}
			}
			
		}
		time += Clock.getBytecodeNum();
		System.out.println("One makeMap is " + time);
	}
	
	private int _evalMove(int x, int y, int right_switch, int up_switch) {
		int time = - Clock.getBytecodeNum();
		int score = 0;
		int num = 0;
		boolean zero_switch = up_switch == 0 && right_switch == 0;
		//int i, j;
		String hash;
		
		// pointed up!
		
//		for (j = 0; j <= 2; j++ ) {
//			for (i = -1; i <= 1; i++ ) {
//				hash += map[x+i][y+j];
//			}
//		}
		if (up_switch > 0 || zero_switch) {
			//System.out.println("Hash: "+hash);
			hash = "" + map[x-1][y] + map[x][y] + map[x+1][y] + map[x-1][y+1] + map[x][y+1] + map[x+1][y+1] + map[x-1][y+2] + map[x][y+2] + map[x+1][y+2];
			if (hash.contains("e"))
				score += 10
			else
				score += combatHash.m.get(hash);
			num++;
		}
		
//		hash = "";
//		// pointed right
//		for (i = 0; i <= 2; i++ ) {
//			for (j = 1; j >= -1; j-- ) {
//				hash += map[x+i][y+j];
//			}
//		}
		if (right_switch > 0) {
			hash = "" + map[x][y+1] + map[x][y] + map[x][y-1] + map[x+1][y+1] + map[x+1][y] + map[x+1][y-1] + map[x+2][y+1] + map[x+2][y] + map[x+2][y-1]; 
			//System.out.println("Hash: "+hash);
		
			if (hash.contains("e"))
				score += 10;
			else
				score += combatHash.m.get(hash);
			num++;
		}
		
		
//		hash = "";
//		// pointed left
//		for (i = 0; i >= -2; i-- ) {
//			for (j = -1; j <= 1; j++ ) {
//				hash += map[x+i][y+j];
//			}
//		}
		if (right_switch < 0) {
			hash = "" + map[x][y-1] + map[x][y] + map[x][y+1] + map[x-1][y-1] + map[x-1][y] + map[x-1][y+1] + map[x-2][y-1] + map[x-2][y] + map[x-2][y+1];
			//System.out.println("Hash: "+hash);
			if (hash.contains("e"))
				score += 10;
			else
				score += combatHash.m.get(hash);
			num ++;
		}
		
//		hash = "";
//		// pointed down
//		for (j = 0; j >= -2; j-- ) {
//			for (i = 1; i >= -1; i-- ) {
//				hash += map[x+i][y+j];
//			}
//		}
		if (up_switch < 0 || zero_switch) {
			hash = "" + map[x+1][y] + map[x][y] + map[x-1][y] + map[x+1][y-1] + map[x][y-1] + map[x-1][y-1] + map[x+1][y-2] + map[x][y-2] + map[x-1][y-2];
			//System.out.println("Hash: "+hash);
			if (hash.contains("e"))
				score += 10;
			else
				score += combatHash.m.get(hash);
			num++;
		}
		
		time += Clock.getBytecodeNum();
		System.out.println("One evalMove is " + time);
		
		return score/num;
	}
	
}
