package team122.combat;


import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Robot;
import team122.robot.Artillery;

public class ArtilleryShotCalculator {

	private int[][] map;
	private Artillery robot;
	private MapLocation me;
	private int size;
	private int middle;
	private int[] xToCheck;
	private int[] yToCheck;
	private int toCheck;
	
	public ArtilleryShotCalculator(Artillery robot) {
		this.robot = robot;
		me = robot.rc.getLocation();
		this.middle = (int)(1 + Math.sqrt(RobotType.ARTILLERY.attackRadiusMaxSquared)) + 1;
		this.size = 2 * (int)(1 + Math.sqrt(RobotType.ARTILLERY.attackRadiusMaxSquared)) + 1;
		
	}
	
	public void shoot() throws GameActionException{
		// set up the map
		_setUp();
		
		// while we have byte code left, calculate the best shot
		int value, bestValue = 0;
		int x = 0, y = 0;
		int xi, yi;
		int i = 0;
		do {
			xi = xToCheck[i] + middle;
			yi = yToCheck[i] + middle;
			value = 2*map[xi][yi] + map[xi-1][yi-1] + map[xi-1][yi] + map[xi-1][yi+1] + map[xi][yi-1] + map[xi][yi+1] + map[xi+1][yi-1] + map[xi+1][yi] + map[xi+1][yi+1];
			if (value > bestValue) {
				value = bestValue;
				x = xToCheck[i];
				y = yToCheck[i];
			}
			i++;
		} while(i < toCheck  && Clock.getBytecodesLeft() > 200);
		if (x != 0 && y != 0) {
			robot.rc.attackSquare(new MapLocation(me.x + x, me.y + y));
		}
	}
	
	private void _setUp() throws GameActionException {
		map = new int[size][size];
		int x,y;
		
		Robot[] objs = robot.nearbyObjects;
		xToCheck = new int[objs.length];
		yToCheck = new int[objs.length];
		toCheck = 0;
		
		for (Robot r : objs) {
			
			RobotInfo info = robot.rc.senseRobotInfo(r);
			x = info.location.x - me.x;
			y = info.location.y - me.y;
			
			// if it's an enemy robot give us some points for hitting the square
			if (info.team == robot.info.enemyTeam) {
				// add this to the 'toCheck' list
				xToCheck[toCheck] = x;
				yToCheck[toCheck] = y;
				toCheck++;
				map[x][y] = 20;
			} 
			
			// if it's our robot try not to hit it
			else if (info.team == robot.info.myTeam) {
				if (info.type == RobotType.SOLDIER) {
					map[x][y] = -20;
				} else if (info.type == RobotType.HQ) {
					map[x][y] = -500;
				} else {
					map[x][y] = -50;
				}
			}
		}
	}
}
