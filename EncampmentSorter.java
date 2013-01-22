package team122;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class EncampmentSorter {

	public MapLocation[] encampments;
	public MapLocation[] usuableEncampments;
	public int[] encampmentDistances;
	public int[] enemyDistances;
	public int[] encampmentSqrtDistances;
	public int[] enemySqrtDistances;
	public int[] encampmentAngles;
	public int[] enemyAngles;
	public int totalEncampments;
	public RobotController rc;
	public boolean finishBaseCalculation;
	public boolean finishArtilleryCalculation;
	public boolean finishGeneratorCalculation;

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
	private int _currentRoundArt;
	private int _currentRoundGen;
	
	public EncampmentSorter(RobotController rc) {
		this.rc = rc;
		_currentRound = 0;
		_currentRoundArt = 0;
		_currentRoundGen = 0;
		
		_lastAlliedUpdate = 50;
		_generators = new ArrayList<MapLocation>(); 
		_artilleries = new ArrayList<MapLocation>(); 
		_alliedEncampments = new MapLocation[0];
		finishArtilleryCalculation = false;
		finishBaseCalculation = false;
		finishGeneratorCalculation = false;
	}
	
	/**
	 * Gets the encampments
	 */
	public void getEncampments() {
		encampments = rc.senseAllEncampmentSquares();
		usuableEncampments = rc.senseAllEncampmentSquares();
		totalEncampments = encampments.length;
		encampmentDistances = new int[totalEncampments];
		encampmentSqrtDistances = new int[totalEncampments];
		encampmentAngles = new int[totalEncampments];
		enemyDistances = new int[totalEncampments];
		enemySqrtDistances = new int[totalEncampments];
		enemyAngles = new int[totalEncampments];
	}
	
	/**
	 * Will do a generator search.
	 * @return
	 */
	public MapLocation popGenerator() {
		
//		if (currentRound == 0 || totalEncampments == 0) {
//			return null;
//		} else {
//			
//			if (_lastGenSearch < 0 || _lastGenSearch < Clock.getRoundNum() - SEARCH_FREQUENCY) {
//				
//			}
//		}
		return null;
	}

	public boolean calculateArtilleryLocations(int turns) {
		
		if (finishArtilleryCalculation) {
			return true;
		}

		int loopsToExecute = ((10000 - Clock.getBytecodeNum() - (200 * (turns - 1))) + ((turns - 1) * 10000)) / 52;
		for (int i = _currentRoundArt, j = 0; j < loopsToExecute && i < totalEncampments; i++, j++) { // 52 Bytecodes
			System.out.println(" Encampment: " + encampmentAngles[i]);
			if ((encampmentAngles[i] < ARTILLERY_ANGLE_LOW || encampmentAngles[i] > ARTILLERY_ANGLE_HIGH) && 
				(enemyAngles[i] < ARTILLERY_ANGLE_LOW || enemyAngles[i] > ARTILLERY_ANGLE_HIGH)) {
				_artilleries.add(encampments[i]);
			}
			_currentRoundArt = i;
		}
		_currentRoundArt++;
		
		finishArtilleryCalculation = !(_currentRoundArt < totalEncampments);
		
		//Finishes the artillery calculation by setting up the list.
		if (finishArtilleryCalculation) {
			_artilleryPossibleList = new MapLocation[_artilleries.size()];
			for (int i = 0, len = _artilleryPossibleList.length; i < len; i++) {
				_artilleryPossibleList[i] = _artilleries.get(i);
			}
			
			_artilleries.clear();
		}
		
		return finishArtilleryCalculation;
	}

	/**
	 * Calculate generator locations
	 * @param turns
	 * @return
	 */
	public boolean calculateGeneratorLocations(int turns) {
		
		if (finishGeneratorCalculation) {
			return true;
		}

		int loopsToExecute = ((10000 - Clock.getBytecodeNum() - (200 * turns)) + ((turns - 1) * 10000)) / 52;
		for (int i = _currentRoundGen, j = 0; j < loopsToExecute && i < totalEncampments; i++, j++) { // 52 Bytecodes
			if (!((encampmentAngles[i] > ARTILLERY_ANGLE_LOW || encampmentAngles[i] < ARTILLERY_ANGLE_HIGH) && 
				(enemyAngles[i] > ARTILLERY_ANGLE_LOW || enemyAngles[i] < ARTILLERY_ANGLE_HIGH))) {
				_generators.add(encampments[i]);
			}
			_currentRoundGen = i;
		}
		_currentRoundGen++;
		
		finishGeneratorCalculation = !(_currentRoundGen < totalEncampments);
		
		//Finishes the artillery calculation by setting up the list.
		if (finishGeneratorCalculation) {
			_generatorPossibleList = new MapLocation[_generators.size()];
			for (int i = 0, len = _generatorPossibleList.length; i < len; i++) {
				_generatorPossibleList[i] = _generators.get(i);
			}
			
			_generators.clear();
		}

		return finishGeneratorCalculation;
	}
	
	/**
	 * Calculates for the next x amount of turns (10,000 byte codes * turns)
	 * @param turns
	 * @return
	 */
	public boolean calculate(int turns) {
		
		if (finishBaseCalculation) {
			return true;
		}
		
		MapLocation hq = rc.senseHQLocation();
		MapLocation enemy = rc.senseEnemyHQLocation();
		int hypot = hq.distanceSquaredTo(enemy);
		int sqrtHypot = (int)Math.sqrt(hypot);
		MapLocation enc;
		
		//We seem to be off by larger amounts once the turns get to high.
		int loopsToExecute = ((10000 - Clock.getBytecodeNum() - (150 * turns)) + ((turns - 1) * 10000)) / 269;

		// 267 Bytecodes
		for (int i = _currentRound, j = 0; j < loopsToExecute && i < totalEncampments; i++, j++) {
			enc = encampments[i];
			encampmentDistances[i] = hq.distanceSquaredTo(enc);
			enemyDistances[i] = enemy.distanceSquaredTo(enc);
			encampmentSqrtDistances[i] = (int)Math.sqrt(encampmentDistances[i]);
			enemySqrtDistances[i] = (int)Math.sqrt(enemyDistances[i]);

			//computes the angle.
			encampmentAngles[i] = 
					(int)Math.acos(Math.toDegrees((encampmentDistances[i] + hypot - enemyDistances[i]) / 
							(2 * encampmentSqrtDistances[i] * sqrtHypot)));

			encampmentAngles[i] = 
					(int)Math.acos(Math.toDegrees((encampmentDistances[i] + hypot - enemyDistances[i]) / 
							(2 * encampmentSqrtDistances[i] * sqrtHypot)));

			_currentRound = i;
		}
		_currentRound++;

		finishBaseCalculation = !(_currentRound < totalEncampments);
		return finishBaseCalculation;
	}
	
	public static final int SEARCH_FREQUENCY = 200;
	public static final int ARTILLERY_STORE = 3;
	public static final int ARTILLERY_ANGLE_LOW = 11;
	public static final int ARTILLERY_ANGLE_HIGH = 349;
}
