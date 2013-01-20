package team122.behavior.lib;

import java.util.ArrayList;
import java.util.Arrays;

import team122.robot.Soldier;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class SoldierCombat extends Behavior{
	protected Soldier robot;
	
	public SoldierCombat(Soldier robot) {
		super();
		this.robot = robot;
	}
	
	public static Direction getDirection(int x, int y) {
		if(x > 0) {
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
			else
				return Direction.SOUTH;
		}
	}

	@Override
	public void run() throws GameActionException {
		if (!robot.rc.isActive())
			return;
		
		//System.out.println("Start Run " + Clock.getBytecodeNum());
		// first take a look at all the enemies and friends that are within 3 squares horizontal/diagonal
		// make a map
		final int SCOPE = 5;
		final int MIDDLE = 2;
		int[][] map = new int[SCOPE][SCOPE];
		int[][] numEnemies = new int[SCOPE][SCOPE];
		int[][] numAllies = new int[SCOPE][SCOPE];
		boolean[][] cantMoveBuilding = new boolean[SCOPE][SCOPE];
		boolean[][] cantMoveSoldier = new boolean[SCOPE][SCOPE];

		
		ArrayList<RobotInfo> enemySoldiers = new ArrayList<RobotInfo>();
		ArrayList<RobotInfo> enemyBuildings = new ArrayList<RobotInfo>();
		ArrayList<RobotInfo> alliedSoldiers = new ArrayList<RobotInfo>();
		ArrayList<RobotInfo> alliedBuildings = new ArrayList<RobotInfo>();
		int x,y,ix,iy,jx,jy;
		
		//System.out.println("Start sorting " + Clock.getBytecodeNum());
		// first pass to find immovable objects
		for (Robot obj:robot.meleeObjects) {
			if(!robot.rc.canSenseObject(obj))
				continue;
			RobotInfo rInfo = robot.rc.senseRobotInfo(obj);
			if(obj.getTeam() == robot.info.enemyTeam) {
				if (rInfo.type == RobotType.SOLDIER) {
					enemySoldiers.add(rInfo);

				} else {
					enemyBuildings.add(rInfo);
					// can't move on a building! update can't move
					x = robot.currentLoc.x - rInfo.location.x;
					y = robot.currentLoc.y - rInfo.location.y;
					if(Math.abs(x) < MIDDLE && Math.abs(y) < MIDDLE)
						cantMoveBuilding[MIDDLE + x][MIDDLE + y] = true;
				}
			} else {
				if (rInfo.type == RobotType.SOLDIER) {
					alliedSoldiers.add(rInfo);
				} else {
					alliedBuildings.add(rInfo);
					// can't move on a building! update can't move
					x = robot.currentLoc.x - rInfo.location.x;
					y = robot.currentLoc.y - rInfo.location.y;
					if(Math.abs(x) < MIDDLE && Math.abs(y) < MIDDLE)
						cantMoveBuilding[MIDDLE + x][MIDDLE + y] = true;
				}
			}
		}
		//System.out.println("Sorting done" + Clock.getBytecodeNum());
		
		// then do the enemy calculations
		for(RobotInfo rInfo:enemySoldiers) {
			x = -robot.currentLoc.x + rInfo.location.x;
			y = -robot.currentLoc.y + rInfo.location.y;
			cantMoveSoldier[MIDDLE + x][MIDDLE + y] = true;
			for(ix = -1; ix <= 1; ix++) {
				for(iy = -1; iy <= 1; iy++) {
					jx = MIDDLE + x + ix;
					if(jx < 0 || jx >= SCOPE)
						continue;
					jy = MIDDLE + y + iy;
					if(jy < 0 || jy >= SCOPE)
						continue;
					
					if(cantMoveBuilding[jx][jy])
						continue;
					
					int multi = Math.abs(jx*jy);
					int add = Math.abs(jx) + Math.abs(jy);
					if (multi <= 1 && add > 0)
						numEnemies[jx][jy] += 1;
					if (multi < 4)
						map[jx][jy] -= 1;
					
				}
			}
		}
		//System.out.println("Solved enemies " + Clock.getBytecodeNum());

		// then do the allied calculations
		for(RobotInfo rInfo:alliedSoldiers) {
			x = - robot.currentLoc.x + rInfo.location.x;
			y = - robot.currentLoc.y + rInfo.location.y;
			cantMoveSoldier[MIDDLE + x][MIDDLE + y] = true;
			for(ix = -1; ix <= 1; ix++) {
				for(iy = -1; iy <= 1; iy++) {
					jx = MIDDLE + x + ix;
					if(jx < 0 || jx >= SCOPE)
						continue;
					jy = MIDDLE + y + iy;
					if(jy < 0 || jy >= SCOPE)
						continue;
					
					if(cantMoveBuilding[jx][jy])
						continue;
					
					int multi = Math.abs(jx*jy);
					int add = Math.abs(jx) + Math.abs(jy);
					if (multi <= 1 && add > 0)
						numAllies[jx][jy] += 1;
					if (multi < 4)
						map[jx][jy] += 1;
					
				}
			}
		}

		//System.out.println("Solved allies" + Clock.getBytecodeNum());
		
		int toMoveValue = -1;
		int[] toMove = new int[2];
		// if there is a nearby square with an enemy in it with a non-negative map value, move there!
		for (ix = -1; ix <= 1; ix+=1) {
			for (iy = -1; iy <=1; iy+=1) {
				jx = MIDDLE + ix;
				jy = MIDDLE + iy;
				if(cantMoveBuilding[jx][jy] || cantMoveSoldier[jx][jy])
					continue;
				if(numEnemies[jx][jy] > 0){
					if(map[jx][jy] > toMoveValue) {
						toMoveValue = map[jx][jy];
						toMove[0] = ix;
						toMove[1] = iy;
					}
				}
			}
		}
		
		//System.out.println("Finished move search " + Clock.getBytecodeNum());
		//System.out.println("Finished move search: " + toMoveValue);

		if (toMoveValue > -1) {
			// we found a place to move! return!
			Direction dir = getDirection(toMove[0], toMove[1]);
			if (robot.rc.canMove(dir)) {
				robot.rc.setIndicatorString(0, "Attacking! " + Clock.getBytecodeNum() + ' ' + dir + ' ' + Arrays.toString(toMove));
				robot.rc.move(dir);
			} else {
				//System.out.println("Failed in soldier combat. Y U NO BE GOOD AT IT?");
			}
			return;
		}
		
		//System.out.println("No move " + Clock.getBytecodeNum());

		
		// if there are no nearby enemies we need to figure out if we should advance or retreat
		int[] enemyDirection = new int[2];
		int enemyValue = 1000;
		// find the enemy center
		for (ix = -1; ix <= 1; ix+=1) {
			for (iy = -1; iy <=1; iy+=1) {
				if(ix != 0 || iy != 0) {
					int sum = 0;
					for (jx = -1; jx <= 1; jx++) {
						for (jy = -1; jy <= 1; jy++) {
							sum += map[MIDDLE + ix + jx][MIDDLE + iy + jy];
						}
					}
					if (sum < enemyValue) {
						enemyValue = sum;
						enemyDirection[0] = ix;
						enemyDirection[1] = iy;
					}
				}
			}
		}
		
		Direction dir = getDirection(enemyDirection[0], enemyDirection[1]);
		
		robot.rc.setIndicatorString(0, "Advance! " + Clock.getBytecodeNum() + ' ' + dir + ' ' + Arrays.toString(enemyDirection));
		if (enemySoldiers.size() > alliedSoldiers.size()) {
			// retreat!
			dir = dir.opposite();
			robot.rc.setIndicatorString(0, "Retreat! " + Clock.getBytecodeNum() + ' ' + dir + ' ' + Arrays.toString(enemyDirection));
		} 
		
		if (robot.rc.canMove(dir)) {
			robot.rc.move(dir);
		} else if (robot.rc.canMove(dir.rotateLeft())) {
			robot.rc.move(dir.rotateLeft());
		} else if (robot.rc.canMove(dir.rotateRight())) {
			robot.rc.move(dir.rotateRight());
		} else {
			//System.out.println("Failed in soldier combat. Y U NO BE GOOD AT IT?");
		}
		
	}

	@Override
	public boolean pre() {
		return robot.enemyInMelee;
	}

}
