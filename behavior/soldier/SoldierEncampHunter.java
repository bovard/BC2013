package team122.behavior.soldier;

import java.util.ArrayList;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import team122.behavior.Behavior;
import team122.communication.Communicator;
import team122.navigation.SoldierMove;
import team122.robot.Soldier;
import team122.utils.MapQuadrantUtils;

public class SoldierEncampHunter extends Behavior {
	
	private boolean init = false;
	private Soldier robot;
	private int quadIndex;

	private SoldierMove hunt;
	private SoldierMove travel;
	private boolean mine = false;
	private ArrayList<ArrayList> quads = new ArrayList<ArrayList>();
	private ArrayList<Integer> quadIndexes = new ArrayList<Integer>();
	private ArrayList<Integer> quadNumbers = new ArrayList<Integer>();
	private int enemyQuad;
	private int ourQuad;
	private int quadNum;
	private boolean traveling = false;
	private ArrayList<MapLocation> wayPoints = new ArrayList<MapLocation>();
	
	

	public SoldierEncampHunter(Soldier robot) {
		this.robot = robot;
		MapQuadrantUtils.width = robot.info.width;
		MapQuadrantUtils.height = robot.info.height;
		MapQuadrantUtils.hq = robot.info.hq;
		MapQuadrantUtils.enemyHq = robot.info.enemyHq;
		hunt = new SoldierMove(robot);
		travel = new SoldierMove(robot);
	}
	
	@Override
	public void start() {
		robot.incChannel = Communicator.CHANNEL_ENCAMPER_HUNTER_COUNT;
		if (!init) {
			init = true;
			int distCenter = Math.min(200, robot.info.height/4*robot.info.width/4);
			int distEnemy = Math.min(180, robot.info.height/4*robot.info.width/4);
			enemyQuad = MapQuadrantUtils.getMapQuadrant(robot.info.enemyHq.x, robot.info.enemyHq.y);
			ourQuad = MapQuadrantUtils.getMapQuadrant(robot.info.hq.x, robot.info.hq.y);

			// randomize our starting quadrant
			quadIndex = Clock.getRoundNum() % 4;
			ArrayList<MapLocation> quad1 = new ArrayList<MapLocation>();
			ArrayList<MapLocation> quad2 = new ArrayList<MapLocation>();
			ArrayList<MapLocation> quad3 = new ArrayList<MapLocation>();
			ArrayList<MapLocation> quad4 = new ArrayList<MapLocation>();
			
			MapLocation middle = robot.info.center;
			MapLocation enemyHq = robot.info.enemyHq;
			MapLocation[] locs = robot.rc.senseAllEncampmentSquares();
			for (MapLocation loc: locs) {
				if (loc.distanceSquaredTo(middle) > distCenter) {
					if (MapQuadrantUtils.getMapQuadrant(loc.x, loc.y) == 1 && enemyQuad != 1) {
						if (loc.distanceSquaredTo(enemyHq) > distEnemy) {
							quad1.add(loc);
						}
					} else if (MapQuadrantUtils.getMapQuadrant(loc.x, loc.y) == 2 && enemyQuad != 2) {
						if (loc.distanceSquaredTo(enemyHq) > distEnemy) {
							quad2.add(loc);
						}
					} else if (MapQuadrantUtils.getMapQuadrant(loc.x, loc.y) == 3 && enemyQuad != 2) {
						if (loc.distanceSquaredTo(enemyHq) > distEnemy) {
							quad3.add(loc);
						}
					} else if(MapQuadrantUtils.getMapQuadrant(loc.x, loc.y) == 4 && enemyQuad != 4) {
						if (loc.distanceSquaredTo(enemyHq) > distEnemy) {
							quad4.add(loc);
						}
					}
				}
				
			}
			System.out.println("Found "+ quad1.size() + " in quad 1");
			System.out.println("Found "+ quad2.size() + " in quad 2");
			System.out.println("Found "+ quad3.size() + " in quad 3");
			System.out.println("Found "+ quad4.size() + " in quad 4");
			
			// bases are on the Y axis, split into 2,1 and 3, 4 randomly
			if (Math.abs(robot.info.hq.x - robot.info.enemyHq.x) < 10  && Math.abs(robot.info.hq.x + robot.info.enemyHq.x - robot.info.width) < 3) {
				quadNum = 2;
				if (Clock.getRoundNum() % 2 == 0) {
					quads.add(quad1);
					quadIndexes.add(0);
					quadNumbers.add(1);
					quads.add(quad4);
					quadIndexes.add(0);
					quadNumbers.add(4);
				} else {
					quads.add(quad2);
					quadIndexes.add(0);
					quadNumbers.add(2);
					quads.add(quad3);
					quadIndexes.add(0);
					quadNumbers.add(3);
				}
				
			}
			// bases are on the X axis, split into 1,4 and 2,3 randomly
			else if (Math.abs(robot.info.hq.y - robot.info.enemyHq.y) < 10 && Math.abs(robot.info.hq.y + robot.info.enemyHq.y - robot.info.height) < 3) {
				quadNum = 2;
				if (Clock.getRoundNum() % 2 == 0) {
					quads.add(quad1);
					quadIndexes.add(0);
					quadNumbers.add(1);
					quads.add(quad2);
					quadIndexes.add(0);
					quadNumbers.add(2);
				} else {
					quads.add(quad3);
					quadIndexes.add(0);
					quadNumbers.add(3);
					quads.add(quad4);
					quadIndexes.add(0);
					quadNumbers.add(4);
				}
				
			}
			// bases are in the same quadrant (this would be bad) or opposite quadants (normal)
			else {
				for (int i = 1; i <= 4; i++){
					if(i != enemyQuad && i != ourQuad) {
						quadNum++;
						if (i == 1) {
							quads.add(quad1);
							quadIndexes.add(0);
							quadNumbers.add(1);
						} else if (i == 2) {
							quads.add(quad2);
							quadIndexes.add(0);
							quadNumbers.add(2);
						} else if (i == 3) {
							quads.add(quad3);
							quadIndexes.add(0);
							quadNumbers.add(3);
						} else {
							quads.add(quad4);
							quadIndexes.add(0);
							quadNumbers.add(4);
						}
					}
				}
			}
			
			traveling = true;
			int distance = Integer.MAX_VALUE;
			MapLocation target = robot.currentLoc;
			int ind = 0;
			for (int i = 0; i < quadNum; i++){
				MapLocation t = MapQuadrantUtils.getMapCornerForQuadrant(quadNumbers.get(i));
				int di = robot.currentLoc.distanceSquaredTo(t);
				
				if (di < distance) {
					ind = i;
					distance = di;
					target = t;
					
				}
			}
			quadIndex = ind;
			wayPoints.add(target);
			
			
		} 
		// we must have just encountered an enemy, skip whichever encamp we are on
		else {
			hunt.destination = null;
			travel.destination = null;
		}
		
		
	}
	
	@Override
	public void run() throws GameActionException {
		robot.rc.setIndicatorString(0, "All Your Encampaments Are Belong to Us!");
		
		if (!robot.rc.isActive()) {
			return;
		}
		int lastQuad;
		int newQuad;
		
		quadIndex = quadIndex % quadNum;
		
		ArrayList<MapLocation> quad = quads.get(quadIndex);
		int index = quadIndexes.get(quadIndex);
		
		// if we are traveling to a new place, go there!
		if (traveling) {
			if (travel()) {
				traveling = false;
			}
		}
		
		// if we are done with this quad, move on!
		else if (index >= quad.size()) {
			quadIndexes.set(quadIndex, 0);
			lastQuad = quadNumbers.get(quadIndex);
			quadIndex = quadIndex + 1;
			quadIndex = quadIndex % quadNum;
			newQuad = quadNumbers.get(quadIndex);
			traveling = true;
			populateWayPoints(lastQuad, newQuad);
		} 
		// else lets go hunting!
		else {
			robot.rc.setIndicatorString(0, "Hunting");
			while(robot.rc.isActive()) {
				if(hunt(quad.get(index))) {
					index ++;
					quadIndexes.set(quadIndex, index);
				}
			}
		} 
	}

	private boolean hunt(MapLocation loc)  throws GameActionException {
		// start the hunt
		if (hunt.destination == null) {
			hunt.destination = loc;
		} 
		// continue the hunt
		else if (hunt.destination != null) {
			// there is a friendly there! abandon the hunt
			if (robot.rc.senseMine(loc) == robot.info.myTeam || (robot.rc.canSenseSquare(loc) && robot.rc.senseNearbyGameObjects(Robot.class, 1, robot.info.myTeam).length == 1)) {
				hunt.destination = null;
				return true;
			}
			
			// we've made it there, lay a mine for great vengeance!
			else if (robot.currentLoc.equals(loc)) {
				if (robot.rc.senseMine(loc) == null) {
					if (mine) {
						robot.rc.layMine();
					}
					
					hunt.destination = null;
					return true;
				}
				
			} else {
				hunt.move();
			}
		} 
		
		return false;
	}
	
	private boolean travel() throws GameActionException {
		
		if (travel.destination == null) {
			if (wayPoints.size() > 0) {
				travel.destination = wayPoints.remove(0);
			} else {
				return true;
			}
		} else if (travel.atDestination() || robot.currentLoc.isAdjacentTo(travel.destination)) {
			travel.destination = null;
		} else {
			travel.move();
		}
		return false;
	}
	
	private void populateWayPoints(int startQuad, int endQuad) {
		wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(startQuad));
		
		if(Math.abs(startQuad - endQuad) == 2) {
			wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(ourQuad));
		}
		
		wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(endQuad));
	}

	@Override
	public boolean pre() throws GameActionException {
		return !robot.enemyInMelee;
	}

}
