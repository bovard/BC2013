package team122;

import java.util.Arrays;
import java.util.HashMap;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class EncampmentSorter {

	public MapLocation[] encampments;
	public int[] encampmentDistances;
	public int[] enemyDistances;
	public int totalEncampments;
	public RobotController rc;
	public boolean finishBaseCalculation;
	public MapLocation hq;
	public MapLocation enemy;
	public int artRange;
	public int artMaxEnemyHQ;
	public MapLocation[] darkHorseArt;
	public int darkHorseIdx;
	public double uX;
	public double uY;
	public int hqX;
	public int hqY;

	/**
	 * State information about generators and artilleries being searched.
	 */
	private int _currentRound;
	private MapLocation[] generatorEncampments;
	private MapLocation[] artilleryEncampments;
	private int[] generatorDistances;
	private int[] artilleryDistances;
	private int[] generatorScores;
	private int[] artilleryScores;
	private int artsLength = 0;
	private int gensLength = 0;
	private int gensIndex = 0;
	private int artsIndex = 0;
	private MapLocation[] alliedEncampments;
	private int artBuilds = 0;
	private int genBuilds = 0;
	private int totalEncampmentDivisor;

	// Sorting
	private boolean specialCaseX;
	private boolean specialCaseY;

	public EncampmentSorter(RobotController rc) {
		this.rc = rc;
		_currentRound = 0;
		finishBaseCalculation = false;

		hq = rc.senseHQLocation();
		hqX = hq.x;
		hqY = hq.y;
		enemy = rc.senseEnemyHQLocation();
		artMaxEnemyHQ = RobotType.ARTILLERY.attackRadiusMaxSquared
				+ hq.distanceSquaredTo(enemy);
		artRange = 35;
		double x = hq.x - enemy.x;
		double y = hq.y - enemy.y;

		uX = x / Math.sqrt(x * x + y * y); // can we remove sqrt later?
		uY = y / Math.sqrt(x * x + y * y);
		genBuilds = 0;

		specialCaseX = enemy.x == hq.x;
		specialCaseY = enemy.y == hq.y;
		System.out.println("Unit: (" + uX + ", " + uY + ")");
	}

	/**
	 * Determines if darkhourse
	 * 
	 * @return
	 */
	public boolean isDarkHorse(int required) throws GameActionException {

		// TODO: Do a sense and just do a local check. It will make it fast and
		// easy to tell when dark horse (300ish byte codes).
		// Only when finished.
		Direction toEnemy = hq.directionTo(enemy);
		MapLocation[] locs = rc.senseEncampmentSquares(
				hq.add(toEnemy).add(toEnemy), artRange, Team.NEUTRAL);

		if (locs.length >= required) {
			darkHorseArt = locs;
			darkHorseIdx = 0;

			MapUtils.sort(hq, locs, false);

			return true;
		}
		return false;
	}

	/**
	 * Gets the encampments
	 */
	public void getEncampments() {
		encampments = rc.senseAllEncampmentSquares();
		totalEncampments = encampments.length;
		encampmentDistances = new int[totalEncampments];
		enemyDistances = new int[totalEncampments];

		artilleryEncampments = new MapLocation[totalEncampments];
		generatorEncampments = new MapLocation[totalEncampments];
		artilleryDistances = new int[totalEncampments];
		generatorDistances = new int[totalEncampments];
		artilleryScores = new int[totalEncampments];
		generatorScores = new int[totalEncampments];

		alliedEncampments = new MapLocation[0];
		
		if (totalEncampments > 7) {
			totalEncampmentDivisor = 3;
		} else {
			totalEncampmentDivisor = 2;
		}
	}

	/**
	 * Will do a generator search.
	 * 
	 * @return
	 */
	public MapLocation popDarkHorse() {

		if (darkHorseIdx < darkHorseArt.length) {
			return darkHorseArt[darkHorseIdx++];
		}
		return null;
	}

	/**
	 * Will do a generator search.
	 * 
	 * @return
	 */
	public MapLocation popGenerator() {
		MapLocation generator = null;

		if (totalEncampments > 0) {
			if (genBuilds > totalEncampments / totalEncampmentDivisor || gensIndex > gensLength) {
				_RefreshAlliedEncampments();
				genBuilds = 0;
				gensIndex = 0;
				
				System.out.println("Fresh new stuff!: ");
			}

outer: 		do {
				generator = generatorEncampments[gensIndex];

				for (int i = 0; i < alliedEncampments.length; i++) {
					
					if (alliedEncampments[i].x == generator.x && alliedEncampments[i].y == generator.y) {
						generator = null;
						break outer;
					}
				}

				gensIndex++;
			} while (generator == null && gensIndex < gensLength);
		}

		genBuilds++;
		return generator;
	}

	/**
	 * Will do a artillery search.
	 * 
	 * @return
	 */
	public MapLocation popArtillery() {
		MapLocation artillery = null;

		if (totalEncampments > 0) {
			if (artBuilds > totalEncampments / totalEncampmentDivisor || artsIndex > artsLength) {
				_RefreshAlliedEncampments();
				artBuilds = 0;
				artsIndex = 0;
			}

outer:		do {
				artillery = artilleryEncampments[artsIndex];

				// TODO: When do we use a hashmap? If ever?
				for (int i = 0; i < alliedEncampments.length; i++) {
					if (alliedEncampments[i].x == artillery.x && alliedEncampments[i].y == artillery.y) {
						artillery = null;
						break outer;
					}
				}

				artsIndex++;
			} while (artillery == null && artsIndex < artsLength);
		}

		artBuilds++;
		return artillery;
	}

	/**
	 * Calculates for the next x amount of turns (10,000 byte codes * turns)
	 * 
	 * @param turns
	 * @return
	 */
	public boolean calculate() {

		if (finishBaseCalculation) {
			return true;
		}

		MapLocation enc;
		int i;
		int startingClock = Clock.getRoundNum();
		double answer = 0;

		// 110 bytecodes
		for (i = _currentRound; Clock.getRoundNum() - startingClock < 1
				&& Clock.getBytecodeNum() < 9400 && i < totalEncampments; i++) {
			enc = encampments[i];
			encampmentDistances[i] = hq.distanceSquaredTo(enc);
			enemyDistances[i] = enemy.distanceSquaredTo(enc);

			if (specialCaseX) {
				answer = (hq.x - enc.x) * (hq.x - enc.x);
			} else if (specialCaseY) {
				answer = (hq.y - enc.y) * (hq.y - enc.y);
			} else {
				double dot = (hqX - enc.x * uX + hqY - enc.y * uY);
				double newX = dot * uX;
				double newY = dot * uY;
				answer = encampmentDistances[i] - (newX * newX + newY * newY);
			}

			// TODO: Scores? Do we need them? Is this a good idea. How about
			// distances?
			if (answer < artRange && enemyDistances[i] < artMaxEnemyHQ) {
				artsLength++;
				artilleryEncampments[artsIndex] = enc;
				artilleryDistances[artsIndex] = encampmentDistances[i];
				artilleryScores[artsIndex++] = encampmentDistances[i];

			} else {
				gensLength++;
				generatorEncampments[gensIndex] = enc;
				generatorDistances[gensIndex] = encampmentDistances[i];
				generatorScores[gensIndex++] = encampmentDistances[i]
						- enemyDistances[i];

			}
		}

		_currentRound = i;

		finishBaseCalculation = !(_currentRound < totalEncampments);

		if (finishBaseCalculation) {
			artsIndex = 0;
			gensIndex = 0;
		}
		return finishBaseCalculation;
	}

	/**
	 * Refreshes the hashmap of allied encampment if its needed.
	 */
	private void _RefreshAlliedEncampments() {
		alliedEncampments = rc.senseAlliedEncampmentSquares();
	}

	public static final int SEARCH_FREQUENCY = 200;
	public static final int ARTILLERY_STORE = 3;
	public static final int ARTILLERY_ANGLE_LOW = 11;
	public static final int ARTILLERY_ANGLE_HIGH = 349;
}
