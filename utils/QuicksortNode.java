package team122.utils;

import java.util.Arrays;

import battlecode.common.Clock;
import battlecode.common.MapLocation;

public class QuicksortNode {
	private int low, high, i, j; 
	private MapLocation[] mapList; 
	private int[] mapScores;
	private int pivot;
	
	/**
	 * Builds a new state of the quicksort node.
	 * @param list
	 * @param scores
	 * @param low
	 * @param high
	 * @param i
	 * @param j
	 */
	public QuicksortNode(MapLocation[] list, int[] scores, int low, int high) {
		this.low = low;
		this.high = high;
		this.mapList = list;
		this.mapScores = scores;
		this.i = low;
		this.j = high;
		pivot = mapScores[low + (high - low) / 2];
	}
	
	/**
	 * Sorts the list.
	 * @return
	 */
	public boolean sort() {
		
		while (i <= j) {
			
			if (Clock.getBytecodeNum() > 9500) {
				return false;
			}
			
			while (mapScores[i] < pivot) {
				i++;
			}
			
			while (mapScores[j] > pivot) {
				j--;
			}
			
			if (i <= j) {
				int temp = mapScores[i];
				mapScores[i] = mapScores[j];
				mapScores[j] = temp;
				
				MapLocation t = mapList[i];
				mapList[i] = mapList[j];
				mapList[j] = t;
				
				i++;
				j--;
			}
		} // end while i <= j
		
		//If we make it here its been sorted.
		return true;
	}
	
	/**
	 * Gets the lower node if there is one.
	 * @return
	 */
	public QuicksortNode getLow() {
		if (low < j) {
			return new QuicksortNode(mapList, mapScores, low, j);
		}
		return null;
	}
	
	/**
	 * Gets the upper half if there is one.
	 * @return
	 */
	public QuicksortNode getHigh() {
		if (i < high) {
			return new QuicksortNode(mapList, mapScores, i, high);
		}
		return null;
	}
}
