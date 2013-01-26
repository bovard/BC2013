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
	private int artilleryLength = 0;
	private int generatorLength = 0;
	private int generatorIndex = 0;
	private int artilleryIndex = 0;
	private MapLocation[] alliedEncampments;
	private int artilleryBuilds = 0;
	private int generatorBuilds = 0;
	private int totalEncampmentDivisor;
	
	//These should only be used in one spot and never set again.
	private int __buildingGeneratorIndex = 0;
	private int __buildingArtilleryIndex = 0;
	private int __maxArtilleryLength = 0;

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
		artRange = 25;
		double x = hq.x - enemy.x;
		double y = hq.y - enemy.y;

		uX = x / Math.sqrt(x * x + y * y); // can we remove sqrt later?
		uY = y / Math.sqrt(x * x + y * y);
		generatorBuilds = 0;

		specialCaseX = enemy.x == hq.x;
		specialCaseY = enemy.y == hq.y;
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
		
		//Somewhere above 10.
		if (totalEncampments > 10) {
			__maxArtilleryLength = totalEncampments;
			totalEncampmentDivisor = 2;
		
		//Somewhere between 1 and 10.
		} else if (totalEncampments > 1) {
			__maxArtilleryLength = totalEncampments / 2;
			totalEncampmentDivisor = 2;
			
		//If there is only one, it should be an artillery.
		} else {
			__maxArtilleryLength = 1;
			totalEncampmentDivisor = 1;
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
			if (generatorBuilds > totalEncampments / totalEncampmentDivisor || generatorIndex > generatorLength) {
				_RefreshAlliedEncampments();
				generatorBuilds = 0;
				generatorIndex = 0;
			}

outer: 		do {
				generator = generatorEncampments[generatorIndex];
				if (generator == null) {
					break;
				}

				for (int i = 0; i < alliedEncampments.length; i++) {
					
					if (alliedEncampments[i].x == generator.x && alliedEncampments[i].y == generator.y) {
						generator = null;
						break;
					}
				}

				generatorIndex++;
			} while (generator == null && generatorIndex < generatorLength);
		}

		generatorBuilds++;
		
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
			if (artilleryBuilds > totalEncampments / totalEncampmentDivisor || artilleryIndex > artilleryLength) {
				_RefreshAlliedEncampments();
				artilleryBuilds = 0;
				artilleryIndex = 0;
			}

outer:		do {
				artillery = artilleryEncampments[artilleryIndex];
				if (artillery == null) {
					break;
				}

				// TODO: When do we use a hashmap? If ever?
				for (int i = 0; i < alliedEncampments.length; i++) {
					if (alliedEncampments[i].x == artillery.x && alliedEncampments[i].y == artillery.y) {
						artillery = null;
						break;
					}
				}

				artilleryIndex++;
			} while (artillery == null && artilleryIndex < artilleryLength);
		}

		artilleryBuilds++;
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
				double dot = ((hqX - enc.x) * uX + (hqY - enc.y) * uY);
				double newX = dot * uX;
				double newY = dot * uY;
				answer = encampmentDistances[i] - (newX * newX + newY * newY);
			}

			// TODO: Scores? Do we need them? Is this a good idea. How about
			// distances?
			if (answer < artRange && enemyDistances[i] < artMaxEnemyHQ && artilleryLength < __maxArtilleryLength) {
				artilleryLength++;
				artilleryEncampments[__buildingArtilleryIndex] = enc;
				artilleryDistances[__buildingArtilleryIndex] = encampmentDistances[i];
				artilleryScores[__buildingArtilleryIndex++] = encampmentDistances[i];

			} else {
				generatorLength++;
				generatorEncampments[__buildingGeneratorIndex] = enc;
				generatorDistances[__buildingGeneratorIndex] = encampmentDistances[i];
				generatorScores[__buildingGeneratorIndex++] = encampmentDistances[i]
						- enemyDistances[i];

			}
		}

		_currentRound = i;

		finishBaseCalculation = !(_currentRound < totalEncampments);
		if (finishBaseCalculation) {
			System.out.println(Arrays.toString(artilleryEncampments));
			System.out.println(Arrays.toString(generatorEncampments));
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
