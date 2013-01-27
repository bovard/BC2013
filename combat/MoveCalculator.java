package team122.combat;


import team122.RobotInformation;
import team122.navigation.SoldierMove;
import team122.robot.Soldier;
import team122.utils.Timer;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class MoveCalculator {
	
	private char[][] map;
	private Soldier robot;
	private boolean soldierNearby;
	private boolean positionSoldierNearby;
	private SoldierMove combatMove;
	private boolean hq;
	private final int MIDDLE = 3;
	private final int SIZE = 7;
	
	
	public MoveCalculator(Soldier robot) {
		this.robot = robot;
		combatMove = new SoldierMove(robot);
	}
	
	public void move (Robot[] nearby, MapLocation loc) throws GameActionException{
		//int time = -Clock.getBytecodeNum();
		
		if (robot.currentLoc.isAdjacentTo(robot.info.enemyHq)) {
			return;
		}
		
		if (nearby.length == 1) {
			_moveOneOnOne(nearby, loc);
			return;
		}
		
		
		_makeMap(nearby, loc);
		
		hq = false;
		
		int[] xyDir = new int[2];
		int score = -1000;
		int temp;
		
		_evalMove(MIDDLE, MIDDLE);
		if (positionSoldierNearby) {
			robot.rc.setIndicatorString(0, "Can't move enemy nearby");
			return;
		}
		
		for(int x=-1;x<=1;x++) {
			for (int y=1;y>=-1;y--) {
				if (map[MIDDLE + x][MIDDLE + y] == 'o' || map[MIDDLE + x][MIDDLE + y] == 'c') {
					temp = _evalMove(MIDDLE + x,MIDDLE + y);
					if (temp > score) {
						score = temp;
						xyDir[0] = x;
						xyDir[1] = y;
					}
				}
			}
		}
		
		
		Direction dir = _xyToDir(xyDir[0], xyDir[1]);
		robot.rc.setIndicatorString(0, dir.toString());
		robot.rc.setIndicatorString(0, "Soldier " +soldierNearby);
		
		// if there are no enemy soldiers nearby, move!
		if (robot.rc.isActive() && !soldierNearby) {
			Robot r = robot.enemiesInSight[0];
			RobotInfo info = robot.rc.senseRobotInfo(r);
			combatMove.destination = info.location;
			combatMove.move();
		}
		
		// if we calculated a move, move it!
		if (robot.rc.isActive() && dir != Direction.NONE){
			if (robot.rc.canMove(dir)) {
				robot.rc.move(dir);
			}
		} 

		
		// otherwise try and defuse
		if (robot.rc.isActive() && !hq) {
			// score 12 = surrounded by 4 allies, 27 is surrounded by 9 allies
			if (score >= 12 && score <= 27 ) {
				dir = robot.info.enemyDir;
				for (int i = 0; i < 8; i++ ) {
					if(robot.rc.senseMine(robot.currentLoc.add(dir)) == robot.info.enemyTeam) {
						robot.rc.defuseMine(robot.currentLoc.add(dir));
						return;
					}
					dir = dir.rotateLeft();
				}
			}
		}
		
	}
	
	/*
	 * This is called if there is only one other robot nearby!
	 */
	private void _moveOneOnOne(Robot[] nearby, MapLocation loc) throws GameActionException {
		RobotInfo info = robot.rc.senseRobotInfo(nearby[0]);
		
		
		if (info.team == robot.info.enemyTeam) {
			
			// an active enemy soldier
			if (info.type == RobotType.SOLDIER && info.roundsUntilMovementIdle < 3) {
				int distance = robot.currentLoc.distanceSquaredTo(info.location);
				// if they have lower health
				if (info.energon <= robot.rc.getEnergon()) {
					// we are close, go for the kill!
					if (distance < 8 && !robot.currentLoc.isAdjacentTo(info.location)) {
						combatMove.destination = info.location;
						combatMove.move();
					}
					// we are already there or too far away, hang out
					
				}
				// if they have higher health
				else {
					
					if (distance > 3) {
						combatMove.destination = robot.currentLoc.add(robot.currentLoc.directionTo(info.location).opposite());
						combatMove.move();
					}
				}
			}
			
			// an inactive enemy soldier or building
			else {
				combatMove.destination = info.location;
				combatMove.move();
			}
			
			
			
		}
		
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
		//int time = - Clock.getBytecodeNum();
		map = new char[SIZE][SIZE];
		for (int i=0;i<7;i++) {
			for (int j=0;j<7;j++) {
				map[i][j] = 'o';
			}
		}

		for (MapLocation l : robot.allied_mines) {
			int x = l.x - loc.x;
			int y = l.y - loc.y;
			map[MIDDLE + x][MIDDLE + y] = 'c';
		}
		
		for (MapLocation l : robot.enemy_mines) {
			int x = l.x - loc.x;
			int y = l.y - loc.y;
			map[MIDDLE + x][MIDDLE + y] = 'g';
		}
		
		for (MapLocation l : robot.neutral_mines) {
			int x = l.x - loc.x;
			int y = l.y - loc.y;
			map[MIDDLE + x][MIDDLE + y] = 'g';
		}
		
		soldierNearby = false;
		for (Robot r : nearby) {
			RobotInfo info = robot.rc.senseRobotInfo(r);
			int x = info.location.x - loc.x;
			int y = info.location.y - loc.y;
			if (Math.abs(x) < 3 && Math.abs(y) < 3) {
				if (info.team == robot.info.enemyTeam) {
					if (info.type == RobotType.SOLDIER && info.roundsUntilMovementIdle < 3){
						soldierNearby = true;
						map[MIDDLE + x][MIDDLE + y] = 'e';
					} else {
						map[MIDDLE + x][MIDDLE + y] = 'f';
					}
				} else { 
					map[MIDDLE + x][MIDDLE + y] = 'a';
				}
			}
		}
		
		int enemyX = robot.info.enemyHq.x - loc.x;
		int enemyY = robot.info.enemyHq.y - loc.y;
		if (Math.abs(enemyX) < 3 && Math.abs(enemyY) < 3) {
			map[MIDDLE + enemyX][MIDDLE + enemyY] = 'v';
		}
	}
	
	private int _scoreHash(String hash) {
		positionSoldierNearby = false;
		int robotPos = 4;
		
		// if we are on a mine, bad position!
		if (hash.charAt(robotPos) == 'g') {
			return -100;
		}
		
		int aCount = 0;
		int eCount = 0;
		int fCount = 0;
		for (int i = 0; i < hash.length(); i++) {
			char c = hash.charAt(i);
			if (c == 'e') {
				positionSoldierNearby = true;
				eCount++;
			} else if (c == 'a') {
				aCount++;
			} else if (c == 'f') {
				fCount++;
			} else if (c == 'v') {
				// found their hq, kill it!
				hq = true;
				return 1000;
			}
		}
		
		// if we can outnumber an enemy do it!
		if (eCount > 0) {
			if(aCount + 1 > (eCount + fCount)) {
				return 100;
			}
			return 6 * (aCount - (eCount+fCount)) + 5;
		}
		// if we can take out an enemy building, do it!
		else if (fCount > 0) {
			return 100;
		} 
		// stick together team!
		else {
			return 3 * aCount;
		}
			
		
	}
	
	
	private int _evalMove(int x, int y) {
		int score = _scoreHash("" + map[x-1][y-1] + map[x][y-1] + map[x+1][y-1] + map[x-1][y] + map[x][y] + map[x+1][y] + map[x-1][y+1] + map[x][y+1] + map[x+1][y+1]); 
		return score;
	}
	

}
