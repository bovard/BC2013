package team122.behavior.soldier;

import java.util.ArrayList;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team122.behavior.Behavior;
import team122.navigation.SoldierMove;
import team122.robot.Soldier;

public class SoldierBackDoor extends Behavior {

	private boolean calced = false;
	private Soldier robot;
	private ArrayList<MapLocation> wayPoints = new ArrayList<MapLocation>();
	private SoldierMove move;
	
	public SoldierBackDoor(Soldier robot) {
		this.robot = robot;
		this.move = new SoldierMove(robot);
	}
	
	private MapLocation _getMapCornerForQuadrent(int quad) {
		//   0,0     0,width
		//	    2 | 1
		//      - . -
		//      3 | 4
		// height,0   height,width
		//  ( I know this is odd, don't want to turn it around in teh head)
		
		switch (quad) {
		case 1:
			return new MapLocation(0, robot.info.width - 1);
		case 2:
			return new MapLocation(0, 0);
		case 3:
			return new MapLocation(robot.info.height - 1, 0);
		case 4:
			return new MapLocation(robot.info.height - 1, robot.info.width - 1);
		}
		return null;
	}
	
	private int _getMapQuadrent(int x, int y) {
		//   0,0     0,width
		//	    2 | 1
		//      - . -
		//      3 | 4
		// height,0   height,width
		//  ( I know this is odd, don't want to turn it around in teh head)
		
		
		// calculate our quadrent
		// left side
		if (robot.info.hq.x < robot.info.width/2) {
			// top side
			if (robot.info.hq.y < robot.info.height/2){
				return 2;
			}
			// bottom side
			else {
				return 3;
			}
		}
		// east side
		else {
			// top side
			if (robot.info.hq.y < robot.info.height/2){
				return 1;
			}
			// bottom side
			else {
				return 4;
			}
		}
	}
	
	private void _createWayPoints() {
		//   0,0     0,width
		//	    2 | 1
		//      - . -
		//      3 | 4
		// height,0   height,width
		//  ( I know this is odd, don't want to turn it around in teh head)
		int quadHQ = _getMapQuadrent(robot.info.hq.x, robot.info.hq.y);
		int quadEnemyHQ = _getMapQuadrent(robot.info.enemyHq.x, robot.info.enemyHq.y);
		
		if (quadHQ == 1) {
			if (quadEnemyHQ == 1) {
				wayPoints.add(_getMapCornerForQuadrent(1));
			} else if (quadEnemyHQ == 2) {
				wayPoints.add(_getMapCornerForQuadrent(4));
				wayPoints.add(_getMapCornerForQuadrent(3));
				wayPoints.add(_getMapCornerForQuadrent(2));
			} else if (quadEnemyHQ == 3) {
				wayPoints.add(_getMapCornerForQuadrent(1));
				wayPoints.add(_getMapCornerForQuadrent(2));
				wayPoints.add(_getMapCornerForQuadrent(3));
			} else {
				wayPoints.add(_getMapCornerForQuadrent(2));
				wayPoints.add(_getMapCornerForQuadrent(3));
				wayPoints.add(_getMapCornerForQuadrent(4));
			}
		} else if (quadHQ == 2) {
			if (quadEnemyHQ == 1) {
				wayPoints.add(_getMapCornerForQuadrent(4));
				wayPoints.add(_getMapCornerForQuadrent(3));
				wayPoints.add(_getMapCornerForQuadrent(2));
			} else if (quadEnemyHQ == 2) {
				wayPoints.add(_getMapCornerForQuadrent(2));
			} else if (quadEnemyHQ == 3) {
				wayPoints.add(_getMapCornerForQuadrent(1));
				wayPoints.add(_getMapCornerForQuadrent(4));
				wayPoints.add(_getMapCornerForQuadrent(3));
			} else {
				wayPoints.add(_getMapCornerForQuadrent(2));
				wayPoints.add(_getMapCornerForQuadrent(1));
				wayPoints.add(_getMapCornerForQuadrent(4));
			}
		//		2 | 1
		//      - . -
		//      3 | 4
			
		} else if (quadHQ == 3) {
			if (quadEnemyHQ == 1) {
				wayPoints.add(_getMapCornerForQuadrent(3));
				wayPoints.add(_getMapCornerForQuadrent(2));
				wayPoints.add(_getMapCornerForQuadrent(1));
			} else if (quadEnemyHQ == 2) {
				wayPoints.add(_getMapCornerForQuadrent(4));
				wayPoints.add(_getMapCornerForQuadrent(1));
				wayPoints.add(_getMapCornerForQuadrent(2));
			} else if (quadEnemyHQ == 3) {
				wayPoints.add(_getMapCornerForQuadrent(3));
			} else {
				wayPoints.add(_getMapCornerForQuadrent(2));
				wayPoints.add(_getMapCornerForQuadrent(1));
				wayPoints.add(_getMapCornerForQuadrent(4));
			}
		} else {
			if (quadEnemyHQ == 1) {
				wayPoints.add(_getMapCornerForQuadrent(3));
				wayPoints.add(_getMapCornerForQuadrent(2));
				wayPoints.add(_getMapCornerForQuadrent(1));
			} else if (quadEnemyHQ == 2) {
				wayPoints.add(_getMapCornerForQuadrent(4));
				wayPoints.add(_getMapCornerForQuadrent(2));
				wayPoints.add(_getMapCornerForQuadrent(1));
			} else if (quadEnemyHQ == 3) {
				wayPoints.add(_getMapCornerForQuadrent(1));
				wayPoints.add(_getMapCornerForQuadrent(2));
				wayPoints.add(_getMapCornerForQuadrent(3));
			} else {
				wayPoints.add(_getMapCornerForQuadrent(4));
			}
		}
		
		wayPoints.add(robot.info.enemyHq);
	}
	
	@Override
	public void start() {
		if (!calced) {
			calced = true;
			// He's a super secret guy in a hat.
			robot.rc.wearHat();
			
			// create the way points to the enemy base!
			_createWayPoints();
			move.destination = wayPoints.remove(0);
			
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
		
		// if we are close to the enemy hq, go for it!
		if(distanceToHq < 26) {
			move.destination = robot.info.enemyHq;
			move.dumbMove();
		// if we are in melee, fight it up
		} else if (robot.enemyInMelee) {
			robot.mCalc.move(robot.meleeObjects, robot.currentLoc);
		// if we are close enough to our destination, go to the next one
		} else if (distanceToDestination < 10) {
			move.destination = wayPoints.remove(0);
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
