package team122.utils;


import team122.robot.HQ;
import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
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
	private MapLocation[] generatorEncampments;
	private MapLocation[] artilleryEncampments;
	private MapLocation[] doNotCapture = new MapLocation[0];
	private int[] generatorScores;
	private int[] artilleryScores;
	private boolean[] generatorUsed;
	private boolean[] artilleryUsed;
	private int[] generatorRound;
	private int[] artilleryRound; 
	private int artilleryLength = 0;
	private int generatorLength = 0;
	
	//These should only be used in one spot and never set again.
	private int __generatorIndex = 0;
	private int __artilleryIndex = 0;
	private int __maxArtilleryLength = 0;
	private int __roundsToWait = 0;

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

		specialCaseX = enemy.x == hq.x;
		specialCaseY = enemy.y == hq.y;

		generatorTree = new QuicksortTree();
		artilleryTree = new QuicksortTree();

		int width = rc.getMapWidth();
		int height = rc.getMapHeight();
		double mineDensity = rc.senseMineLocations(enemy, 100000, Team.NEUTRAL).length * 1.0 / (width * height);
		
		__roundsToWait = (int)((width + height) * (1 + mineDensity * 2));
	}

	/**
	 * Sets the do not capture list.
	 * @param locs
	 */
	public void setDoNotCapture(MapLocation[] locs) {
		doNotCapture = locs;
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
		artilleryScores = new int[totalEncampments];
		artilleryRound = new int[totalEncampments];
		artilleryUsed = new boolean[totalEncampments];
		
		generatorEncampments = new MapLocation[totalEncampments];
		generatorScores = new int[totalEncampments];
		generatorRound = new int[totalEncampments];
		generatorUsed = new boolean[totalEncampments];
		
		//Somewhere above 10.
		if (totalEncampments > 10) {
			__maxArtilleryLength = totalEncampments;
		
		//Somewhere between 2 and 10.
		} else if (totalEncampments > 1) {
			__maxArtilleryLength = totalEncampments / 2;
			
		//If there is only one, it should be an artillery.
		} else {
			__maxArtilleryLength = 1;
		}
	}

	/**
	 * Will do a generator search.
	 * 
	 * @return
	 */
	public MapLocation popGenerator() {
		MapLocation generator = null;

		if (totalEncampments > 0) {
			for (int i = 0; i < generatorLength; i++) {
				if (!generatorUsed[i] && !_doNotCapture(generatorEncampments[i])) {
					generatorUsed[i] = true;
					generatorRound[i] = Clock.getRoundNum();
					
					generator = generatorEncampments[i];
					break;
				}
			} // end for
		} // end totalEncamps
		
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
			for (int i = 0; i < artilleryLength; i++) {
				if (!artilleryUsed[i] && !_doNotCapture(generatorEncampments[i])) {
					artilleryUsed[i] = true;
					artilleryRound[i] = Clock.getRoundNum();
					
					artillery = artilleryEncampments[i];
					break;
				}
			} // end for
		} // end totalEncamps
		return artillery;
	}
	
	/**
	 * Cycles through the encampments and continually updates them.
	 */
	public void refresh() {
		
		//Processes a generator and an artillery back and forth.
		do {
			_processGenerator();
			_processArtillery();
		} while (Clock.getBytecodesLeft() > HQ.MINIMUM_BYTECODES_LEFT);
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
				&& Clock.getBytecodesLeft() > HQ.MINIMUM_BYTECODES_LEFT && i < totalEncampments; i++) {
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
				artilleryEncampments[__artilleryIndex] = enc;
				artilleryScores[__artilleryIndex++] = encampmentDistances[i];

			} else {
				generatorLength++;
				generatorEncampments[__generatorIndex] = enc;
				generatorScores[__generatorIndex++] = encampmentDistances[i] - enemyDistances[i];

			}
		}

		_currentRound = i;

		calculated = !(_currentRound < totalEncampments);
		if (calculated) {
			generatorTree.setData(generatorEncampments, generatorScores, generatorLength - 1);
			artilleryTree.setData(artilleryEncampments, artilleryScores, artilleryLength - 1);
			__generatorIndex = 0;
			__artilleryIndex = 0;
		}
		return calculated;
	}

	
	/**
	 * Processes a single artillery then returns back to the 
	 */
	private void _processArtillery() {
		
		Team team = rc.getTeam();
		if (__artilleryIndex < artilleryLength) {
			for (;__artilleryIndex < artilleryLength && Clock.getBytecodesLeft() > HQ.MINIMUM_BYTECODES_LEFT; __artilleryIndex++) {
				
				//We claim to have an encampment here and if we do not then we set it to false
				//and break from the loop so artillery can have some processing as well.
				if (artilleryUsed[__artilleryIndex] && Clock.getRoundNum() - artilleryRound[__artilleryIndex] > __roundsToWait) {
					
					Robot[] r = rc.senseNearbyGameObjects(Robot.class, artilleryEncampments[__artilleryIndex], 1, team);
					if (r.length == 0) {
						artilleryUsed[__artilleryIndex] = false;
						break;
					}
				}
			}
		} // end artillery
		
		__artilleryIndex = 0;
	}
	
	private void _processGenerator() {

		Team team = rc.getTeam();
		if (__generatorIndex < generatorLength) {
			for (;__generatorIndex < generatorLength && Clock.getBytecodesLeft() > HQ.MINIMUM_BYTECODES_LEFT; __generatorIndex++) {
				
				//We claim to have an encampment here and if we do not then we set it to false
				//and break from the loop so artillery can have some processing as well.
				if (generatorUsed[__generatorIndex] && Clock.getRoundNum() - generatorRound[__generatorIndex] > __roundsToWait) {
					
					Robot[] r = rc.senseNearbyGameObjects(Robot.class, generatorEncampments[__generatorIndex], 1, team);
					if (r.length == 0) {
						generatorUsed[__generatorIndex] = false;
						break;
					}
				}
			}
		} 
		__generatorIndex = 0;
	}
	
	/**
	 * returns if you should not capture
	 * @param loc
	 * @return
	 */
	private boolean _doNotCapture(MapLocation loc) {
		
		for (int i = 0; i < doNotCapture.length; i++) {
			if (doNotCapture[i].equals(loc)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Scores the artillery based on encampment distance.
	 * @param distanceFromHQ
	 * @return
	 */
	public static final int ScoreArtillery(int distanceFromHQ) {
		return distanceFromHQ;
	}
	
	/**
	 * Scoring for the generator encampments.
	 * @param distanceToHQ
	 * @param distanceToEnemy
	 * @return
	 */
	public static final int ScoreGenerator(int distanceToHQ, int distanceToEnemy) {
		return distanceToHQ - distanceToEnemy;
	}
	
	public static final boolean IsEncampmentArtillery(MapLocation hq, MapLocation enemy, MapLocation encampment) {

		double x = hq.x - enemy.x;
		double y = hq.y - enemy.y;

		double uX = x / Math.sqrt(x * x + y * y); // can we remove sqrt later?
		double uY = y / Math.sqrt(x * x + y * y);
		
		double dot = ((hq.x - encampment.x) * uX + (hq.y - encampment.y) * uY);
		double newX = dot * uX;
		double newY = dot * uY;
		
		if (hq.distanceSquaredTo(encampment) - (newX * newX + newY * newY) < ARTILLERY_PERP_DISTANCE) {
			return true;
		}
		return false;
	}

	public static final int MAX_ARRAY_LENGTH_FOR_ALLIES = 9;
	public static final int MAX_ALLY_LENGTH_NO_RESORT = 50;
	public static final int ARTILLERY_PERP_DISTANCE = 35;
}
