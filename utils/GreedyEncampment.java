package team122.utils;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class GreedyEncampment {

	/**
	 * Greedy searches out a supplier/generator spot.
	 * @param rc
	 * @param hq
	 * @param enemy
	 * @return
	 * @throws GameActionException
	 */
	public static final MapLocation GetGreedyGenerator(RobotController rc, MapLocation hq, MapLocation enemy) throws GameActionException {
		Direction dir = hq.directionTo(enemy).rotateLeft();
		MapLocation[] encampments = rc.senseEncampmentSquares(hq.add(dir).add(dir).add(dir), GREEDY_RADIUS_GENERATOR, Team.NEUTRAL);
		int enemyDistance = hq.distanceSquaredTo(enemy);
		
		//Short circuit.
		if (encampments.length == 0) {
			return null;
		}
		
		int lowestScore = 10000;
		int index = 0;
		for (int i = 0; i < encampments.length; i++) {
			int score = encampments[i].distanceSquaredTo(hq) - (encampments[i].distanceSquaredTo(enemy) - enemyDistance / 2);
			if (score < lowestScore) {
				lowestScore = score;
				index = i;
			}
		}
		
		
		return encampments[index];
	}

	/**
	 * Greedy searches out a supplier/generator spot.
	 * @param rc
	 * @param hq
	 * @param enemy
	 * @return
	 * @throws GameActionException
	 */
	public static final MapLocation GetGreedyArtillery(RobotController rc, MapLocation hq, MapLocation enemy) throws GameActionException {
		Direction dirToEnemy = hq.directionTo(enemy);
		
		//Moves out 5 squares.
		MapLocation[] encampments = rc.senseEncampmentSquares(
				hq.add(dirToEnemy).add(dirToEnemy).add(dirToEnemy).add(dirToEnemy).add(dirToEnemy), GREEDY_RADIUS_ARTILLERY, Team.NEUTRAL);
		
		//Short circuit.
		if (encampments.length == 0) {
			return null;
		}
		
		int lowestScore = 10000;
		int index = 0;
		for (int i = 0; i < encampments.length; i++) {
			int score = encampments[i].distanceSquaredTo(hq);
			if (score < lowestScore) {
				lowestScore = score;
				index = i;
			}
		}
		
		
		return encampments[index];
	}

	public static final int GREEDY_RADIUS_GENERATOR = 250;
	public static final int GREEDY_RADIUS_ARTILLERY = 75;
}
