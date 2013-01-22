package team122;

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

	/**
	 * State information about generators and artilleries being searched.
	 */
	private int _currentRound;
	private MapLocation[] sortGens;
	private MapLocation[] sortArts;
	private int[] sortGensDistance;
	private int[] sortArtsDistance;
	private int sortArtsLength = 5;
	private int sortGensLength = 15;
	private int artsLength = 0;
	private int gensLength = 0;
	private boolean genSorted = false;
	private boolean artillerySorted = false;
	private int gensIndex = 0;
	private int artsIndex = 0;
	
	public EncampmentSorter(RobotController rc) {
		this.rc = rc;
		_currentRound = 0;
		finishBaseCalculation = false;

		sortArts = new MapLocation[sortArtsLength];
		sortGens = new MapLocation[sortGensLength];
		sortArtsDistance = new int[sortArtsLength];
		sortGensDistance = new int[sortGensLength];

		for (int i = 0; i < sortArtsLength; i++) {
			sortArtsDistance[i] = 100000;
		}
		for (int i = 0; i < sortGensLength; i++) {
			sortGensDistance[i] = 100000;
		}

		hq = rc.senseHQLocation();
		enemy = rc.senseEnemyHQLocation();
		artMaxEnemyHQ = RobotType.ARTILLERY.attackRadiusMaxSquared + hq.distanceSquaredTo(enemy);
		artRange = 35;
	}
	
	/**
	 * Determines if darkhourse
	 * @return
	 */
	public boolean isDarkHorse(int required) throws GameActionException {
		
		//TODO: Do a sense and just do a local check.  It will make it fast and easy to tell when dark horse (300ish byte codes).
		//Only when finished.
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
	}

	
	/**
	 * Will do a generator search.
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
	 * @return
	 */
	public MapLocation popGenerator() {
		
		if (!genSorted) {
			MapUtils.sort(sortGens, sortGensDistance, true);
			genSorted = true;
		}
		
		if (gensIndex < gensLength) {
			return sortGens[gensIndex++];
		}
		return null;
	}
	
	/**
	 * Will do a generator search.
	 * @return
	 */
	public MapLocation popArtillery() {
		
		if (!artillerySorted) {
			MapUtils.sort(sortArts, sortArtsDistance, true);
			artillerySorted = true;
		}

		if (artsIndex < artsLength) {
			return sortArts[artsIndex++];
		}
		return null;
	}
	
	/**
	 * Calculates for the next x amount of turns (10,000 byte codes * turns)
	 * @param turns
	 * @return
	 */
	public boolean calculate() {
		
		if (finishBaseCalculation) {
			return true;
		}
		
		MapLocation enc;

		int i, j, k;
		int x1 = enemy.x - hq.x;
		int y1 = enemy.y - hq.y;
		int dot;
		int startingClock = Clock.getRoundNum();
		int newEncampIdx;
		int encampmentDistance;
		int largestEncampDistance;

		// 110 bytecodes
		for (i = _currentRound; Clock.getRoundNum() - startingClock < 1 && Clock.getBytecodeNum() < 9400 && i < totalEncampments; i++) {
			enc = encampments[i];
			encampmentDistances[i] = hq.distanceSquaredTo(enc);
			enemyDistances[i] = enemy.distanceSquaredTo(enc);
			
			//Now we update the b2
			
			dot = x1 * (enc.x - hq.x) + y1 * (enc.y - hq.y);
			if (encampmentDistances[i] - dot < artRange && enemyDistances[i] < artMaxEnemyHQ) {
				
				newEncampIdx = -1;
				largestEncampDistance = 0;
				encampmentDistance = encampmentDistances[i];
				
				for (k = 0; k < sortArtsLength; k++) {
					if (artsLength < sortArtsLength) {
						if (sortArtsDistance[k] == 100000) {
							sortArtsDistance[k]= encampmentDistances[i];
							sortArts[k]= encampments[i]; 
							artsLength++;
							break;
						}
					} else if (encampmentDistance < sortArtsDistance[k] && (newEncampIdx == -1 || sortArtsDistance[k] > largestEncampDistance)) {
						newEncampIdx = k;
						largestEncampDistance = sortArtsDistance[k];
					}
				}

				
				if (newEncampIdx != -1) {
					sortArtsDistance[newEncampIdx] = encampmentDistances[i];
					sortArts[newEncampIdx] = encampments[i];
				}
			} else {
				newEncampIdx = -1;
				encampmentDistance = encampmentDistances[i] - enemyDistances[i];
				largestEncampDistance = 0;
				
				for (k = 0; k < sortGensLength; k++) {
					if (gensLength < sortGensLength) {
						if (sortGensDistance[k] == 100000) {
							sortGensDistance[k]= encampmentDistances[i];
							sortGens[k]= encampments[i]; 
							gensLength++;
							break;
						}
					} else if (encampmentDistance < sortGensDistance[k] && (newEncampIdx == -1 || sortGensDistance[k] > largestEncampDistance)) {
						newEncampIdx = k;
						largestEncampDistance = sortGensDistance[k];
					}
				}
				
				if (newEncampIdx != -1) {
					sortGensDistance[newEncampIdx] = encampmentDistances[i];
					sortGens[newEncampIdx] = encampments[i];
				}
			}
		}

		_currentRound = i;

		finishBaseCalculation = !(_currentRound < totalEncampments);
		return finishBaseCalculation;
	}
	
	public static final int SEARCH_FREQUENCY = 200;
	public static final int ARTILLERY_STORE = 3;
	public static final int ARTILLERY_ANGLE_LOW = 11;
	public static final int ARTILLERY_ANGLE_HIGH = 349;
}
