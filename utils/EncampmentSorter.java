package team122.utils;

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
	public boolean calculated;
	public boolean sorted;
	public boolean generatorSorted;
	public boolean artillerySorted;
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
	public QuicksortTree generatorTree;
	public QuicksortTree artilleryTree;

	/**
	 * State information about generators and artilleries being searched.
	 */
	private int _currentRound;
	private int generatorIndex = 0;
	private int artilleryIndex = 0;
	private MapLocation[] alliedEncampments;
	private HashMap<MapLocation, String> alliedMap;
	private int artilleryBuilds = 0;
	private int generatorBuilds = 0;
	private int totalEncampmentDivisor;
	private MapLocation[] generatorEncampments;
	private MapLocation[] artilleryEncampments;
	private int[] generatorDistances;
	private int[] artilleryDistances;
	public int[] generatorScores;
	public int[] artilleryScores;
	private int artilleryLength = 0;
	private int generatorLength = 0;
	
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
		calculated = false;
		sorted = false;
		generatorSorted = false;
		artillerySorted = false;

		hq = rc.senseHQLocation();
		hqX = hq.x;
		hqY = hq.y;
		enemy = rc.senseEnemyHQLocation();
		artMaxEnemyHQ = RobotType.ARTILLERY.attackRadiusMaxSquared
				+ hq.distanceSquaredTo(enemy);
		artRange = ARTILLERY_PERP_DISTANCE;
		double x = hq.x - enemy.x;
		double y = hq.y - enemy.y;

		uX = x / Math.sqrt(x * x + y * y); // can we remove sqrt later?
		uY = y / Math.sqrt(x * x + y * y);
		generatorBuilds = 0;

		specialCaseX = enemy.x == hq.x;
		specialCaseY = enemy.y == hq.y;

		generatorTree = new QuicksortTree();
		artilleryTree = new QuicksortTree();
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
			if ((generatorBuilds > totalEncampments / totalEncampmentDivisor && alliedEncampments.length < MAX_ALLY_LENGTH_NO_RESORT) || 
					generatorIndex > generatorLength) {
				
				_RefreshAlliedEncampments();
				generatorBuilds = 0;
				generatorIndex = 0;
			}

			do {
				generator = generatorEncampments[generatorIndex];
				if (generator == null) {
					break;
				}
				
				if (alliedEncampments.length > MAX_ARRAY_LENGTH_FOR_ALLIES) {
					if (alliedMap.containsKey(generator)) {
						generator = null;
					}
				} else {
					for (int i = 0; i < alliedEncampments.length; i++) {
						
						if (alliedEncampments[i].x == generator.x && alliedEncampments[i].y == generator.y) {
							generator = null;
							break;
						}
					}
				}

				generatorIndex++;
			} while (generator == null && generatorIndex < generatorLength && Clock.getBytecodesLeft() > 500);
		}

		System.out.println("Poping gen: " + generator + " : " + generatorIndex + " : " + generatorLength);
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
			if ((artilleryBuilds > totalEncampments / totalEncampmentDivisor && alliedEncampments.length < MAX_ALLY_LENGTH_NO_RESORT) || 
					artilleryIndex > artilleryLength) {
				
				_RefreshAlliedEncampments();
				artilleryBuilds = 0;
				artilleryIndex = 0;
			}

			do {
				artillery = artilleryEncampments[artilleryIndex];
				if (artillery == null) {
					break;
				}

				if (alliedEncampments.length > MAX_ARRAY_LENGTH_FOR_ALLIES) {
					if (alliedMap.containsKey(artillery)) {
						artillery = null;
					}
				} else {
					for (int i = 0; i < alliedEncampments.length; i++) {
						if (alliedEncampments[i].x == artillery.x && alliedEncampments[i].y == artillery.y) {
							artillery = null;
							break;
						}
					}
				}

				artilleryIndex++;
			} while (artillery == null && artilleryIndex < artilleryLength && Clock.getBytecodesLeft() > 500);
		}

		artilleryBuilds++;
		return artillery;
	}
	
	public boolean sort() {
		if (!generatorTree.done) {
			generatorTree.sort();
		} else {
			generatorSorted = true;
		}
		
		if (!artilleryTree.done) {
			artilleryTree.sort();
		} else {
			artillerySorted = true;
		}
		
		sorted = artillerySorted && generatorSorted;
		return sorted;
	}

	/**
	 * Calculates for the next x amount of turns (10,000 byte codes * turns)
	 * 
	 * @param turns
	 * @return
	 */
	public boolean calculate() {

		if (calculated) {
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
				generatorScores[__buildingGeneratorIndex++] = encampmentDistances[i] - enemyDistances[i];

			}
		}

		_currentRound = i;

		calculated = !(_currentRound < totalEncampments);
		if (calculated) {
			generatorTree.setData(generatorEncampments, generatorScores, generatorLength - 1);
			artilleryTree.setData(artilleryEncampments, artilleryScores, artilleryLength - 1);
		}
		return calculated;
	}
	
	public void printState() {
//		System.out.println("Gens: " + Arrays.toString(generatorScores));
//		System.out.println("Artilleries: " + Arrays.toString(artilleryScores));
//		System.out.println("Gen: " + generatorIndex + " : " + generatorLength + " :: Arts: " + artilleryIndex + " : " + artilleryLength);
//		System.out.println((calculated ? "Is Calculated" : "NOT CALCULATED") + (sorted ? "Is Sorted" : "NOT SORTED"));
	}

	/**
	 * Refreshes the hashmap of allied encampment if its needed.
	 */
	private void _RefreshAlliedEncampments() {
		alliedEncampments = rc.senseAlliedEncampmentSquares();
		
		if (alliedEncampments.length > MAX_ARRAY_LENGTH_FOR_ALLIES) {
			alliedMap = new HashMap<MapLocation, String>(5000); // Reduces the cost by double. NEVER CLEAR
			for (int i = 0; i < alliedEncampments.length; i++) {
				alliedMap.put(alliedEncampments[i], "");
			}
		}
	}

	public static final int MAX_ARRAY_LENGTH_FOR_ALLIES = 9;
	public static final int MAX_ALLY_LENGTH_NO_RESORT = 50;
	public static final int ARTILLERY_PERP_DISTANCE = 35;
}
