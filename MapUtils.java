package team122;

import java.util.Arrays;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class MapUtils {

	/**
	 * Sorts the map locations in accending distance order
	 * @param locs
	 */
	public static void sort(MapLocation start, MapLocation[] locs, boolean ascending) {
		int[] dists = new int[locs.length];
		for (int i = 0, len = locs.length; i < len; i++) {
			dists[i] = locs[i].distanceSquaredTo(start);
		}
		
		sort(locs, dists, ascending);
	}
	
	/**
	 * Sorts the map in accending order.
	 * @param locs
	 * @param dists
	 */
	public static void sort(MapLocation[] locs, int[] dists, boolean ascending) {
		
		if (locs.length < 5) {
			int currentDist;
			MapLocation currentLoc;
			
			if (ascending) {
				
				//InsertionSort
				for (int i = 1, j, len = locs.length; i < len; i++) {
					currentDist = dists[i];
					currentLoc = locs[i];
					
					for (j = i; j > 0 && currentDist < dists[j - 1]; j--) {
						locs[j] = locs[j - 1];
						dists[j] = dists[j - 1];
					}
		
					dists[j] = currentDist;
					locs[j] = currentLoc;
				}
			} else {

				
				//InsertionSort
				for (int i = 1, j, len = locs.length; i < len; i++) {
					currentDist = dists[i];
					currentLoc = locs[i];
					
					for (j = i; j > 0 && currentDist > dists[j - 1]; j--) {
						locs[j] = locs[j - 1];
						dists[j] = dists[j - 1];
					}
		
					dists[j] = currentDist;
					locs[j] = currentLoc;
				}
			}
		} else {
			
			if (ascending) {
				//QuickSort
				_quicksort(dists, locs, 0, locs.length - 1);
			} else {
				_quicksort_descend(dists, locs, 0, locs.length - 1);
			}
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
	private static void _quicksort(int[] locsDists, MapLocation[] locs, int low, int high) {
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
				
				MapLocation t = locs[i];
				locs[i] = locs[j];
				locs[j] = t;
				
				i++;
				j--;
			}
		}
		
		if (low < j) {
			_quicksort(locsDists, locs, low, j);
		}
		if (i < high) {
			_quicksort(locsDists, locs, i, high);
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
	private static void _quicksort_descend(int[] locsDists, MapLocation[] locs, int low, int high) {
		int i = low, j = high;
		int pivot = locsDists[low + (high - low) / 2];
		
		while (i <= j) {
			
			while (locsDists[i] > pivot) {
				i++;
			}
			
			while (locsDists[j] < pivot) {
				j--;
			}
			
			if (i <= j) {
				int temp = locsDists[i];
				locsDists[i] = locsDists[j];
				locsDists[j] = temp;
				
				MapLocation t = locs[i];
				locs[i] = locs[j];
				locs[j] = t;
				
				i++;
				j--;
			}
		}
		
		if (low < j) {
			_quicksort_descend(locsDists, locs, low, j);
		}
		if (i < high) {
			_quicksort_descend(locsDists, locs, i, high);
		}
	}
	
	/**
	 * Attempts to move in the direction with defusion.
	 * @param rc
	 * @param dir
	 * @return
	 */
	public static final int canMove(RobotController rc, RobotInformation info, Direction dir) {
		MapLocation loc = rc.getLocation();
		
		if (rc.canMove(dir)) {
			Team t = rc.senseMine(loc.add(dir));
			if (t != null && t != info.myTeam) {
				return MUST_DEFUSE;
			}

			return CAN_MOVE;
		} else {
			return CANT_MOVE;
		}	
	}

	public static final int CAN_MOVE = 0;
	public static final int MUST_DEFUSE = 1;
	public static final int CANT_MOVE = 2;
}
