package team122;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
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

	/**
	 * State information about generators and artilleries being searched.
	 */
	private int _lastAlliedUpdate;
	private ArrayList<MapLocation> _generators;
	private ArrayList<MapLocation> _artilleries;
	private MapLocation[] _alliedEncampments;
	private MapLocation[] _artilleryPossibleList;
	private MapLocation[] _generatorPossibleList;
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
		_lastAlliedUpdate = 50;
		_generators = new ArrayList<MapLocation>(); 
		_artilleries = new ArrayList<MapLocation>(); 
		_alliedEncampments = new MapLocation[0];
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
	public MapLocation popGenerator() {
		
		if (!genSorted) {
			MapUtils.sort(sortGens, sortGensDistance);
		} else {
			if (gensIndex < gensLength) {
				return sortGens[gensIndex++];
			}
		}
		return null;
	}
	
	/**
	 * Will do a generator search.
	 * @return
	 */
	public MapLocation popArtillery() {
		
		if (!artillerySorted) {
			MapUtils.sort(sortArts, sortArtsDistance);
		} else {
			if (artsIndex < artsLength) {
				return sortArts[artsIndex++];
			}
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
		
		MapLocation hq = rc.senseHQLocation();
		MapLocation enemy = rc.senseEnemyHQLocation();
		MapLocation enc;

		int i, k;
		int x1 = enemy.x - hq.x;
		int y1 = enemy.y - hq.y;
		int dot;
		int artRange = RobotType.ARTILLERY.attackRadiusMaxSquared;
		int artMaxEnemyHQ = RobotType.ARTILLERY.attackRadiusMaxSquared + hq.distanceSquaredTo(enemy);
		int startingClock = Clock.getRoundNum();

		// 110 bytecodes
		for (i = _currentRound; Clock.getRoundNum() - startingClock < 1 && Clock.getBytecodesLeft() > 170 && i < totalEncampments; i++) {
			enc = encampments[i];
			encampmentDistances[i] = hq.distanceSquaredTo(enc);
			enemyDistances[i] = enemy.distanceSquaredTo(enc);
			
			//Now we update the b2
			dot = x1 * (enc.x - hq.x) + y1 * (enc.y - hq.y);
			dot *= dot;
			if (encampmentDistances[i] - dot < artRange && artMaxEnemyHQ < enemyDistances[i]) {
				
				for (k = 0; k < sortArtsLength; k++) {
					if (encampmentDistances[i] < sortArtsDistance[k]) {
						sortArtsDistance[k]= encampmentDistances[i];
						sortArts[k]= encampments[i]; 
						artsLength++;
						break;
					}
				}
			} else {

				for (k = 0; k < sortArtsLength; k++) {
					if (encampmentDistances[i] - enemyDistances[i] < sortGensDistance[k]) {
						sortGensDistance[k]= encampmentDistances[i];
						sortGens[k]= encampments[i]; 
						gensLength++;
						break;
					}
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
