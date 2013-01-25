package team122.behavior.soldier;

import java.util.ArrayList;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team122.behavior.Behavior;
import team122.navigation.SoldierMove;
import team122.robot.Soldier;
import team122.utils.MapQuadrentUtils;

public class SoldierBackDoor extends Behavior {

	private boolean calced = false;
	private Soldier robot;
	private ArrayList<MapLocation> wayPoints = new ArrayList<MapLocation>();
	private SoldierMove move;
	
	public SoldierBackDoor(Soldier robot) {
		this.robot = robot;
		this.move = new SoldierMove(robot);
		MapQuadrentUtils.width = robot.info.width;
		MapQuadrentUtils.height = robot.info.height;
		MapQuadrentUtils.hq = robot.info.hq;
		MapQuadrentUtils.enemyHq = robot.info.enemyHq;
	}
	
	
	private void _createWayPoints() {
		//   0,0     0,width
		//	    2 | 1
		//      - . -
		//      3 | 4
		// height,0   height,width
		//  ( I know this is odd, don't want to turn it around in teh head)
		int quadHQ = MapQuadrentUtils.getMapQuadrent(robot.info.hq.x, robot.info.hq.y);
		System.out.println("Our hq is in quadrent " + quadHQ);
		int quadEnemyHQ = MapQuadrentUtils.getMapQuadrent(robot.info.enemyHq.x, robot.info.enemyHq.y);
		System.out.println("Enemy hq is in quadrent " + quadEnemyHQ);
		
		if (quadHQ == 1) {
			if (quadEnemyHQ == 1) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(1));
			} else if (quadEnemyHQ == 2) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(4));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(3));
				wayPoints.add(new MapLocation(0, robot.info.enemyHq.y));
			} else if (quadEnemyHQ == 3) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(1));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(2));
				wayPoints.add(new MapLocation(0, robot.info.enemyHq.y));
			} else {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(2));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(3));
				wayPoints.add(new MapLocation(robot.info.enemyHq.x, robot.info.height - 1));
			}
		} else if (quadHQ == 2) {
			if (quadEnemyHQ == 1) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(3));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(4));
				wayPoints.add(new MapLocation(robot.info.width -1 , robot.info.enemyHq.y));
			} else if (quadEnemyHQ == 2) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(2));
			} else if (quadEnemyHQ == 3) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(1));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(4));
				wayPoints.add(new MapLocation(robot.info.enemyHq.x, robot.info.height - 1));
			} else {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(2));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(1));
				wayPoints.add(new MapLocation(robot.info.width - 1 , robot.info.enemyHq.y));
			}
		//		2 | 1
		//      - . -
		//      3 | 4
			
		} else if (quadHQ == 3) {
			if (quadEnemyHQ == 1) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(3));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(2));
				wayPoints.add(new MapLocation(robot.info.enemyHq.x, 0));
			} else if (quadEnemyHQ == 2) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(4));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(1));
				wayPoints.add(new MapLocation(robot.info.enemyHq.x, 0));
			} else if (quadEnemyHQ == 3) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(3));
			} else {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(2));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(1));
				wayPoints.add(new MapLocation(robot.info.width - 1, robot.info.enemyHq.y));
			}
		} else {
			if (quadEnemyHQ == 1) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(3));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(2));
				wayPoints.add(new MapLocation(robot.info.enemyHq.x, 0));
			} else if (quadEnemyHQ == 2) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(4));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(3));
				wayPoints.add(new MapLocation(0, robot.info.enemyHq.y));
			} else if (quadEnemyHQ == 3) {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(1));
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(2));
				wayPoints.add(new MapLocation(0, robot.info.enemyHq.y));
			} else {
				wayPoints.add(MapQuadrentUtils.getMapCornerForQuadrent(4));
			}
		}
		
		wayPoints.add(robot.info.enemyHq);
		System.out.println("Now we have " + wayPoints.size() + " way Points");
		for (int i = 0; i < wayPoints.size(); i++) {
			System.out.println(wayPoints.get(i).toString());
		}
	}
	
	@Override
	public void start() {
		if (!calced) {
			calced = true;
			// He's a super secret guy in a hat.
			robot.rc.wearHat();
			
			// create the way points to the enemy base!
			_createWayPoints();
			move.setDestination(wayPoints.remove(0));
			
		}
		
	}
	
	@Override
	public void run() throws GameActionException {
		robot.rc.setIndicatorString(0, "SECRET AGENT MAN");
		
		if (!robot.rc.isActive()) {
			return;
		}
		
		int distanceToHq = robot.currentLoc.distanceSquaredTo(robot.info.enemyHq);
		int distanceToDestination = robot.currentLoc.distanceSquaredTo(move.destination);
		
		// if we are damaging the enemy HQ, let forth a mighty TROLOLOL
		if (robot.currentLoc.isAdjacentTo(robot.info.enemyHq)) {
			robot.rc.setIndicatorString(0, "TROLOLOLOLOL");
		} 
		// if we are close to the enemy hq, go for it!
		else if (distanceToHq < 26) {
			move.destination = robot.info.enemyHq;
			move.dumbMove();
		// if we are in melee, fight it up
		} else if (robot.enemyInMelee) {
			robot.mCalc.move(robot.meleeObjects, robot.currentLoc);
		// if we are close enough to our destination, go to the next one
		} else if (distanceToDestination < 5) {
			move.setDestination(wayPoints.remove(0));
			move.move();
		// otherwise just move on along
		} else {
			move.move();
		}	
	}

	@Override
	public boolean pre() throws GameActionException {
		return true;
	}

}
