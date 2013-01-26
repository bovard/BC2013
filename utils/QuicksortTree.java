package team122.utils;

import java.util.ArrayList;

import battlecode.common.MapLocation;

public class QuicksortTree {

	public boolean done;
	public boolean hasData;

	private ArrayList<QuicksortNode> sortList;
	private QuicksortNode current;
	
	/**
	 * Constructs a blank tree.  Waits for setData to be called.
	 */
	public QuicksortTree() {
		hasData = false;
		done = false;
	}
	
	/**
	 * Quicksort tree will sort a set of locations and distances through
	 * a breadth first search.
	 * 
	 * @param locations
	 * @param locationDistances
	 */
	public QuicksortTree(MapLocation[] locations, int[] locationDistances, int high) {
		setData(locations, locationDistances, high);
	}
	
	/**
	 * Constructs the data into the tree.
	 * @param locations
	 * @param locationDistances
	 */
	public void setData(MapLocation[] locations, int[] locationDistances, int high) {

		if (locations.length == 0) {
			done = false;
			hasData = false;
			return;
		}

		sortList = new ArrayList<QuicksortNode>();
		
		//Sets the first sorter.
		current = new QuicksortNode(locations, locationDistances, 0, high);
		done = false;
		hasData = true;
	}
	
	/**
	 * quicksort 
	 */
	public void sort() {
		
		//If the list has made it in the small amount of time required then it will
		//continue on sorting to the next list.
		while (current.sort()) {
			QuicksortNode low = current.getLow();
			QuicksortNode high = current.getHigh();
			
			//I believe some sides can end before others (wont always be even).
			if (low != null) {
				sortList.add(low);
			}
			if (high != null) {
				sortList.add(high);
			}
			
			if (sortList.size() > 0) {
				current = sortList.remove(0);
			} else {
				done = true;
				return;
			} 
		} // end current.sort()
		
	}
}
