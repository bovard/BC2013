package team122.combat;


import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
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
		//this.middle = (int)(1 + Math.sqrt(RobotType.ARTILLERY.attackRadiusMaxSquared)) + 1;
		this.middle = 10;
		//this.size = 2 * (int)(1 + Math.sqrt(RobotType.ARTILLERY.attackRadiusMaxSquared)) + 1;
		this.size = 21;
		
	}
	
	public void shoot() throws GameActionException{
		
		
		System.out.println(GameConstants.ARTILLERY_SPLASH_RATIO);
		
		// set up the map
		_setUp();
		
		// while we have byte code left, calculate the best shot
		int value, bestValue = 0;
		int x = 0, y = 0;
		int xi, yi;
		
		int i = 0;
		do {
			xi = xToCheck[i];
			yi = yToCheck[i];
			value =  map[xi-1][yi-1] + map[xi-1][yi] + map[xi-1][yi+1] + map[xi][yi-1] + map[xi][yi+1] + map[xi+1][yi-1] + map[xi+1][yi] + map[xi+1][yi+1];
			if (map[xi][yi] == 100) {
				value += RobotType.ARTILLERY.attackPower * GameConstants.ARTILLERY_SPLASH_RATIO;
			} else {
				value += (1/GameConstants.ARTILLERY_SPLASH_RATIO) * map[xi][yi];
			}
			robot.rc.setIndicatorString(0, "Value: " + value);
			if (value > bestValue) {
				bestValue = value;
				x = xToCheck[i];
				y = yToCheck[i];
			}
			i++;
		} while(i < toCheck  && Clock.getBytecodesLeft() > 500);
		
		// check on the squares
		for (int ix = -1; ix <= 1; ix++) {
			if (Clock.getBytecodesLeft() < 500) {
				break;
			}
			for (int iy = -1; iy <=1; iy++){
				if (iy == 0 && ix == 0){
					break;
				}
				i = 0;
				do {
					xi = xToCheck[i] + ix;
					yi = yToCheck[i] + iy;
					value =  map[xi-1][yi-1] + map[xi-1][yi] + map[xi-1][yi+1] + map[xi][yi-1] + map[xi][yi+1] + map[xi+1][yi-1] + map[xi+1][yi] + map[xi+1][yi+1];
					if (map[xi][yi] == 100) {
						value += RobotType.ARTILLERY.attackPower * GameConstants.ARTILLERY_SPLASH_RATIO;
					} else {
						value += (1/GameConstants.ARTILLERY_SPLASH_RATIO) * map[xi][yi];
					}
					robot.rc.setIndicatorString(0, "Value: " + value);
					if (value > bestValue) {
						bestValue = value;
						x = xToCheck[i];
						y = yToCheck[i];
					}
					i++;
				} while(i < toCheck  && Clock.getBytecodesLeft() > 500);
			}
			
		}
		
		if (x != middle || y != middle) {
			MapLocation loc = new MapLocation(me.x + x - middle, me.y + y - middle);
			robot.rc.attackSquare(loc);
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
			x = middle + info.location.x - me.x;
			y = middle + info.location.y - me.y;
			
			// if it's an enemy robot give us some points for hitting the square
			if (info.team == robot.info.enemyTeam) {
				// add this to the 'toCheck' list
				xToCheck[toCheck] = x;
				yToCheck[toCheck] = y;
				toCheck++;
				if (info.energon <= RobotType.ARTILLERY.attackPower * GameConstants.ARTILLERY_SPLASH_RATIO){
					map[x][y] = 100;
				} else {
					map[x][y] = (int) (RobotType.ARTILLERY.attackPower * GameConstants.ARTILLERY_SPLASH_RATIO);
				}
				
			} 
			
			// if it's our robot try not to hit it
			else if (info.team == robot.info.myTeam) {
				if (info.type == RobotType.SOLDIER) {
					map[x][y] = (int) (-1 * RobotType.ARTILLERY.attackPower * GameConstants.ARTILLERY_SPLASH_RATIO);
				} else if (info.type == RobotType.HQ) {
					map[x][y] = -100;
				} else {
					map[x][y] = (int) (-2 * RobotType.ARTILLERY.attackPower * GameConstants.ARTILLERY_SPLASH_RATIO);
				}
			}
		}
		robot.rc.setIndicatorString(0, "Checking " + toCheck + " enemies");
	}
}
