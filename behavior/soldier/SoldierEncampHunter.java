package team122.behavior.soldier;

import java.util.ArrayList;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import team122.behavior.Behavior;
import team122.navigation.SoldierMove;
import team122.robot.Soldier;
import team122.utils.MapQuadrantUtils;

public class SoldierEncampHunter extends Behavior {
	
	private boolean init = false;
	private Soldier robot;
	private int quadIndex;
	private ArrayList<MapLocation> quad1 = new ArrayList<MapLocation>();
	private int quad1Index = 0;
	private ArrayList<MapLocation> quad2 = new ArrayList<MapLocation>();
	private int quad2Index = 0;
	private ArrayList<MapLocation> quad3 = new ArrayList<MapLocation>();
	private int quad3Index = 0;
	private ArrayList<MapLocation> quad4 = new ArrayList<MapLocation>();
	private int quad4Index = 0;
	private SoldierMove hunt;
	private SoldierMove travel;
	private boolean mine = false;
	private ArrayList[] quads = new ArrayList[2];
	private int[] quadIndexes = new int[2];
	
	

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
		if (!init) {
			init = true;
			int enemyQuad = MapQuadrantUtils.getMapQuadrant(robot.info.enemyHq.x, robot.info.enemyHq.y);

			// randomize our starting quadrant
			quadIndex = Clock.getRoundNum() % 4;
			
			MapLocation middle = robot.info.center;
			MapLocation enemyHq = robot.info.enemyHq;
			MapLocation[] locs = robot.rc.senseAllEncampmentSquares();
			for (MapLocation loc: locs) {
				if (loc.distanceSquaredTo(middle) > 100) {
					if (MapQuadrantUtils.getMapQuadrant(loc.x, loc.y) == 1) {
						if (enemyQuad == 1) {
							if (loc.distanceSquaredTo(enemyHq) > 100) {
								quad1.add(loc);
							}
						} else {
							quad1.add(loc);
						}
					} else if (MapQuadrantUtils.getMapQuadrant(loc.x, loc.y) == 2) {
						if (enemyQuad == 2) {
							if (loc.distanceSquaredTo(enemyHq) > 100) {
								quad2.add(loc);
							}
						} else {
							quad2.add(loc);
						}
					} else if (MapQuadrantUtils.getMapQuadrant(loc.x, loc.y) == 3) {
						if (enemyQuad == 3) {
							if (loc.distanceSquaredTo(enemyHq) > 100) {
								quad3.add(loc);
							}
						} else {
							quad3.add(loc);
						}
					} else {
						if (enemyQuad == 4) {
							if (loc.distanceSquaredTo(enemyHq) > 100) {
								quad4.add(loc);
							}
						} else {
							quad4.add(loc);
						}
					}
				}
				
			}
			System.out.println("Found "+ quad1.size() + " in quad 1");
			System.out.println("Found "+ quad2.size() + " in quad 2");
			System.out.println("Found "+ quad3.size() + " in quad 3");
			System.out.println("Found "+ quad4.size() + " in quad 4");
		} 
		// we must have just encountered an enemy, skip whichever encamp we are on
		else {
			hunt.destination = null;
			travel.destination = null;
			int currentQuad = MapQuadrantUtils.getMapQuadrant(robot.currentLoc.x, robot.currentLoc.y);
			if (currentQuad == 1) {
				quad1Index++;
			} else if (currentQuad == 2) {
				quad2Index++;
			} else if (currentQuad == 3) {
				quad3Index++;
			} else if (currentQuad == 4) {
				quad4Index++;
			}
		}
		
		
	}
	
	@Override
	public void run() throws GameActionException {
		robot.rc.setIndicatorString(0, "All Your Encampaments Are Belong to Us!");
		if (!robot.rc.isActive()) {
			return;
		}
		
		// TODO: this makes me sad, could make this much better logic at the cost
		// of a bit of byte code
		if (quadIndex == 0 ) {
			if (quad1Index >= quad1.size()) {
				quadIndex++;
				quad1Index = 0;
				travel.setDestination(MapQuadrantUtils.getMapCornerForQuadrant(2));
			} else {
				System.out.println("hunting in quad 1");
				if(hunt(quad1.get(quad1Index))) {
					quad1Index++;
				}
			}
		} else if (quadIndex == 1) {
			if (quad2Index >= quad2.size()) {
				quadIndex++;
				quad2Index = 0;
				travel.setDestination(MapQuadrantUtils.getMapCornerForQuadrant(3));
			} else {
				System.out.println("hunting in quad 2");
				if(hunt(quad2.get(quad2Index))) {
					quad2Index++;
				}
			}
			
		} else if (quadIndex == 2) {
			if (quad3Index >= quad3.size()) {
				quadIndex++;
				quad3Index = 0;
				travel.setDestination(MapQuadrantUtils.getMapCornerForQuadrant(4));
			} else {
				System.out.println("hunting in quad 3");
				if(hunt(quad3.get(quad3Index))) {
					quad3Index++;
				}
			}
		} else if (quadIndex == 3) {
			if (quad4Index >= quad4.size()) {
				quadIndex++;
				quad4Index = 0;
				travel.setDestination(MapQuadrantUtils.getMapCornerForQuadrant(1));
			} else {
				System.out.println("hunting in quad 4");
				if(hunt(quad4.get(quad4Index))) {
					quad4Index++;
				}
			}
		} else { 
			quadIndex = quadIndex % 4;
		}
	}

	private boolean hunt(MapLocation loc)  throws GameActionException {
		// start the hunt
		if (hunt.destination == null && travel.destination == null) {
			hunt.destination = loc;
		} 
		// continue the hunt
		else if (hunt.destination != null) {
			// there is a friendly there! abandon the hunt
			if (robot.rc.senseMine(loc) == robot.info.myTeam || (robot.rc.canSenseSquare(loc) && robot.rc.senseNearbyGameObjects(Robot.class, 1, robot.info.myTeam).length == 1)) {
				hunt.destination = null;
				return true;
			}
			
			// we've made it there, lay a mine for great vengence!
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
		// head to greener pastures!
		} else if (travel.destination != null) {
			if (travel.atDestination() || robot.currentLoc.isAdjacentTo(travel.destination)) {
				travel.destination = null;
				// made it back to the corner, ready for another!
				return true;
			} else {
				travel.move();
			}
		} 
		
		return false;
	}

	@Override
	public boolean pre() throws GameActionException {
		return !robot.enemyInMelee;
	}

}
