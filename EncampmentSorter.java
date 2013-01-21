package team122;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class EncampmentSorter {

	public MapLocation[] encampments;
	public int[] encampmentsDistances;
	public int[] enemyDistances;
	public MapLocation[] nearHQ;
	public MapLocation[] neutralMines;
	public ArrayList<MapLocation> alliedEncampments;
	public int totalEncampments;
	public RobotController rc;
	public RobotInformation info;
	public MapLocation[] artilleryEncamp;
	public int[] artilleryDistances;
	public int totalArtillerySpots;
	public int enemyHqDistance;
	
	// private stuff for state.
	private boolean calculatedSetEncampments = false;
	
	public EncampmentSorter(RobotController rc, RobotInformation info) {
		this.rc = rc;
		this.info = info;
		alliedEncampments = new ArrayList<MapLocation>();
		enemyHqDistance = info.hq.distanceSquaredTo(info.enemyHq);
	}

	/**
	 * This will remove all artilleries from general encampments.
	 */
	public void intersectArtilleryWithEncampments() {
		
		//Removes the artillery encampments from the actual encampments.
		for (int i = 0; i < totalArtillerySpots; i++) {
			for (int j = 0; j < totalEncampments; j++) {
				if (encampments[j] != null && encampments[j].equals(artilleryEncamp[i])) {
					encampments[j] = null;
					break;
				}
			}
		}
	}
	
	/**
	 * Removes any encampments that block the HQ.
	 */
	public void removeBlockerEncamps() throws GameActionException {
		//Allows for 2 encampments.
		Direction dir = info.enemyDir.rotateLeft();
		MapLocation loc = info.hq.add(dir);
		
		while (loc.distanceSquaredTo(info.hq) < 16) {
			for (int i = 0; i < totalEncampments; i++) {
				if (encampments[i] != null && loc.equals(encampments[i])) {
					System.out.println("Removing encampment");
					encampments[i] = null;
					break;
				}
			}
			
			for (int i = 0; i < totalArtillerySpots; i++) {
				if (artilleryEncamp[i] != null && loc.equals(artilleryEncamp[i])) {

					System.out.println("Removing artillery");
					artilleryEncamp[i] = null;
					break;
				}
			}
			
			loc = loc.add(dir);
		}
	}
	
	/**
	 * A shortened version of the regular artillery sort.
	 */
	public void setEncampmentsNearbyArtillery() throws GameActionException {
		Direction hToE = info.enemyDir;
		Direction eToH = info.enemyDir.opposite();
		MapLocation hqLoc = info.hq.add(hToE).add(hToE).add(hToE).add(hToE).add(hToE);
		MapLocation enemyLoc = info.hq.add(eToH).add(eToH).add(eToH).add(eToH).add(eToH);
		MapLocation[] hqArts = rc.senseEncampmentSquares(hqLoc, 100, Team.NEUTRAL);
		MapLocation[] enemyArts = rc.senseEncampmentSquares(enemyLoc, 100, Team.NEUTRAL);
		
		totalArtillerySpots = hqArts.length + enemyArts.length;
		artilleryEncamp = new MapLocation[totalArtillerySpots];
		artilleryDistances = new int[totalArtillerySpots];
		
		int i = 0;
		for (i = 0; i < hqArts.length; i++) {
			artilleryEncamp[i] = hqArts[i];
			artilleryDistances[i] = hqArts[i].distanceSquaredTo(info.hq);
		}
		
		for (int j = 0; j < enemyArts.length; j++) {
			artilleryEncamp[i + j] = enemyArts[j];
			artilleryDistances[i + j] = enemyArts[j].distanceSquaredTo(info.hq);			
		}
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
				encampmentsDistances[i] = encampments[i].distanceSquaredTo(info.hq);
				enemyDistances[i] = encampments[i].distanceSquaredTo(info.enemyHq);
			}

			//Sets allied encampments.
			MapLocation[] ae = rc.senseAlliedEncampmentSquares();
			for (int i = 0, len = ae.length; i < len; i++) {
				alliedEncampments.add(ae[i]);
			}
			
			calculatedSetEncampments = true;
		}
	}
	
	/**
	 * The artillery sort is to make all the encampments sorted in an artillery ordered sort.
	 */
	public void setEncampmentsArtillerySort() throws GameActionException {
		
		//The plan is that we only check along the line between the enemy hq and ours.
		ArrayList<MapLocation> map = new ArrayList<MapLocation>(10);
		ArrayList<Integer> hqDist = new ArrayList<Integer>(10);
		ArrayList<Integer> enemyDist = new ArrayList<Integer>(10);

		Direction t = info.hq.directionTo(info.enemyHq);
		MapLocation loc = info.hq.add(t).add(t).add(t).add(t).add(t);
		
		while (loc.distanceSquaredTo(info.hq) < enemyHqDistance) {
			MapLocation[] encamps = rc.senseEncampmentSquares(loc, 100, Team.NEUTRAL);
			
			for (int i = 0; i < encamps.length; i++) {
				if (!map.contains(encamps[i])) {
					map.add(encamps[i]);
					hqDist.add(info.hq.distanceSquaredTo(encamps[i]));
					enemyDist.add(info.enemyHq.distanceSquaredTo(encamps[i]));
				}
			}
			
			//Moves toward the enemyhq.
			loc = loc.add(t).add(t).add(t).add(t)
					.add(t).add(t).add(t).add(t)
					.add(t).add(t).add(t).add(t)
					.add(t).add(t).add(t).add(t);
		}
		
		artilleryEncamp = map.toArray(new MapLocation[map.size()]);
		artilleryDistances = new int[artilleryEncamp.length];
		for (int i = 0, len = artilleryEncamp.length; i < len; i++) {
			artilleryDistances[i] = hqDist.get(i) - enemyDist.get(i);
		}
		
		MapUtils.sort(artilleryEncamp, artilleryDistances);
		totalArtillerySpots = artilleryDistances.length;
	}
	
	/**
	 * The simpliest strategy.
	 */
	public void setEncampmentsBasicArtillery() throws GameActionException {
		Direction hToE = info.enemyDir;
		MapLocation hqLoc = info.hq.add(hToE).add(hToE).add(hToE).add(hToE).add(hToE);
		MapLocation[] hqArts = rc.senseEncampmentSquares(hqLoc, 100, Team.NEUTRAL);

		totalArtillerySpots = hqArts.length;
		artilleryEncamp = new MapLocation[totalArtillerySpots];
		artilleryDistances = new int[totalArtillerySpots];

		for (int i = 0; i < hqArts.length; i++) {
			artilleryEncamp[i] = hqArts[i];
			artilleryDistances[i] = hqArts[i].distanceSquaredTo(info.hq);
			
			for (int j = 0; j < totalEncampments; j++) {
				if (encampments[j] != null && encampments[j].equals(artilleryEncamp[i])) {
					encampments[j] = null;
				}
			}
		}
	}
	
	/**
	 * Sets the neutral mines.
	 */
	public void setNeutralMines() {
		neutralMines = rc.senseMineLocations(info.center, info.width * 1000, Team.NEUTRAL);
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
	
	/**
	 * Might increase it.
	 */
	private static final int ARTILLERY_RADIUS = 225;
}
