package team122;

import java.util.HashMap;

import team122.communication.Communicator;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class RobotInformation {
	public Team myTeam;
	public Team enemyTeam;
	public MapLocation hq;
	public MapLocation enemyHq;
	public MapLocation center;
	public MapLocation[] encampments;
	public int[] encampmentsDistances;
	public int[] enemyDistances;
	public MapLocation[] nearHQ;
	public MapLocation[] neutralMines;
	public HashMap<MapLocation, Boolean> alliedEncampments;
	public int id;
	public int width;
	public int height;
	public int enemyHqDistance;
	public int totalEncampments;
	public RobotController rc;
	
	
	// private stuff for state.
	private boolean calculatedSetEncampments = false;

	/**
	 * Will construct a robot information. These are common operations that
	 * require bytecode execution and can be saved by storing the information.
	 * 
	 * @param rc
	 */
	public RobotInformation(RobotController rc) {
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
		hq = rc.senseHQLocation();
		enemyHq = rc.senseEnemyHQLocation();
		enemyHqDistance = hq.distanceSquaredTo(enemyHq);
		id = rc.getRobot().getID();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		center = new MapLocation(width / 2, height / 2);
		alliedEncampments = new HashMap<MapLocation, Boolean>();
		this.rc = rc;
	}
	
	/**
	 * Sets just the encampments no sorting.
	 */
	public void setEncampments() {
		if (!calculatedSetEncampments) {
			encampments = rc.senseAllEncampmentSquares();
			encampmentsDistances = new int[encampments.length];
			enemyDistances = new int[encampments.length];
			totalEncampments = encampments.length;
	
			for (int i = 0, len = encampments.length; i < len; i++) {
				encampmentsDistances[i] = encampments[i].distanceSquaredTo(hq);
				enemyDistances[i] = encampments[i].distanceSquaredTo(enemyHq);
			}

			//Sets allied encampments.
			MapLocation[] ae = rc.senseAlliedEncampmentSquares();
			for (int i = 0, len = ae.length; i < len; i++) {
				alliedEncampments.put(ae[i], true);
			}
			
			calculatedSetEncampments = true;
		}
	}
	
	/**
	 * Sets the neutral mines.
	 */
	public void setNeutralMines() {
		neutralMines = rc.senseMineLocations(center, width * 1000, Team.NEUTRAL);
	}
	
	/**
	 * Generator/supplier sort algorithm.
	 */
	public void setEncampmentsGenSort() {
		setEncampments();
		
		int[] distances = new int[totalEncampments];
		for (int i = 0; i < totalEncampments; i++) {
			distances[i] = encampmentsDistances[i] - enemyDistances[i];
		}
		
		MapUtils.sort(encampments, distances);
	}
	
	/**
	 * set encampments and sorts them along with enemy distances.
	 * 
	 * NOTE**: The insertion sort and quick sort values have changed
	 * i hade the setEncampments more efficient, no more temp variable 
	 * instantiated.
	 */
	public void setEncampmentsAndSort() { 	/* o(n^2) InsertionSort */
											/* n = 2  : 397  */
											/* n = 4  : 706  */
											/* n = 8  : 1690 */
											/* n = 20 : 7706 */
											/* o(nlogn) QuickSort */
											/* n = 2  : 405  */
											/* n = 4  : 709  */
											/* n = 8  : 1515 */
											/* n = 96 : 14582 */
		setEncampments();
		
		if (encampments.length < 5) {

			int currentDist;
			MapLocation currentLoc;
			
			//InsertionSort
			for (int i = 1, j, len = encampments.length; i < len; i++) {
				currentDist = encampmentsDistances[i];
				currentLoc = encampments[i];
				
				for (j = i; j > 0 && currentDist < encampmentsDistances[j - 1]; j--) {
					encampments[j] = encampments[j - 1];
					encampmentsDistances[j] = encampmentsDistances[j - 1];
					enemyDistances[j] = enemyDistances[j - 1];
				}
	
				encampmentsDistances[j] = currentDist;
				encampments[j] = currentLoc;
			}
		} else {
			
			//QuickSort
			_quicksort(encampmentsDistances, enemyDistances, encampments, 0, encampmentsDistances.length - 1);
		}
	}
	
	/**
	 * The quicksort algorithm uses recursion, maybe we can make it
	 * into while loops.
	 * 
	 * @param numbers
	 * @param low
	 * @param high
	 */
	private void _quicksort(int[] locsDists, int[] nmeDists, MapLocation[] locs, int low, int high) {
		int i = low, j = high;
		int pivot = locsDists[low + (high - low) / 2];
		
		while (i <= j) {
			
			while (locsDists[i] < pivot) {
				i++;
			}
			
			while (locsDists[j] > pivot) {
				j--;
			}
			
			if (i <= j) {
				int temp = locsDists[i];
				locsDists[i] = locsDists[j];
				locsDists[j] = temp;
				
				temp = nmeDists[i];
				nmeDists[i] = nmeDists[j];
				nmeDists[j] = temp;
				
				MapLocation t = locs[i];
				locs[i] = locs[j];
				locs[j] = t;
				
				i++;
				j--;
			}
		}
		
		if (low < j) {
			_quicksort(locsDists, nmeDists, locs, low, j);
		}
		if (i < high) {
			_quicksort(locsDists, nmeDists, locs, low, i);
		}
	}
}
