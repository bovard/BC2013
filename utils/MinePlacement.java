package team122.utils;

import java.util.ArrayList;
import battlecode.common.MapLocation;
	
public class MinePlacement {
	public static ArrayList<MapLocation> mineSpots = new ArrayList<MapLocation>();
	public static int mapWidth;
	public static int mapHeight;
	public static int startX;
	public static int startY;
	public static boolean hasPickAxe = false;
	public static int startRing = 1;
	public static MapLocation MiningStartLoc;
	
	public static MapLocation getMineSpot() {
		if(mineSpots.size() == 0) {
			_getMiningLocations();
			startRing++;
		}
		return mineSpots.get(0);
	}
	
	public static void reset() {
		startRing = 1;
		mineSpots.clear();
	}
	
	private static void _insertMine(int x, int y) {
		MapLocation created;
		if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight) {
			created = new MapLocation(x, y);
			mineSpots.add(created);
		}
	}
	
	private static void _buildFastRingLocations(int right_nodes, int top_nodes, int left_nodes, int bottom_nodes) {
		int x = startX,
			y = startY;
		
		if (startRing != 1) {
			x += (startRing - 2) * 2;
			y += (startRing - 1) * 2;
		}
		int i = 1;
		// insert right nodes
		for (int j = 1; j <= right_nodes; j++) {
			if (i == 1 && j == 1) {
				x += 1;
			} else if (j == 1) {
				x += 3;
			} else if (j <= (right_nodes + 3) / 2) {
				x += 1;
				y -= 2;
			} else {
				x -= 2;
				y -= 2;
			}
			_insertMine(x, y);
		}
		// insert top nodes
		for (int k = 1; k <= top_nodes; k++) {
			if (i == 1 && k == 1) {
				x += 1;
				y -= 2;
			} else if (k == 1) {
				x -= 2;
				y -= 2;
			} else {
				x -= 3;
			}
			_insertMine(x, y);
		}
		// insert left nodes
		for (int l = 1; l <= left_nodes; l++) {
			if (l <= (left_nodes + 1) / 2) {
				x -= 1;
				y += 2;
			} else {
				x += 2;
				y +=2;
			}
			_insertMine(x, y);
		}
		// insert bottom nodes
		for (int m = 1; m <= bottom_nodes; m++) {
			if (m == 1) {
				x += 2;
				y += 2;
			} else {
				x += 3;
			}
			_insertMine(x, y);
		}
	}
	
	private static void _buildSlowRingLocations(int right_nodes, int top_nodes, int left_nodes, int bottom_nodes) {
		int x = startX,
			y = startY;
		
		if (startRing != 1) {
			x += startRing - 1;
			y += startRing - 1;
		}
		
		// insert right nodes
		for (int j = 0; j < right_nodes; j++) {
			if (j == 0) {
				x += 1;
			} else {
				y -= 1;
			}
			_insertMine(x, y);
		}
		// insert top nodes
		for (int k = 0; k < top_nodes; k++) {
			if (k == 0) {
				y -= 1;
			} else {
				x -= 1;
			}
			_insertMine(x, y);
		}
		// insert left nodes
		for (int l = 0; l < left_nodes; l++) {
			y += 1;
			_insertMine(x, y);
		}
		// insert bottom nodes
		for (int m = 0; m < bottom_nodes; m++) {
			if (m == 0) {
				y += 1;
			} else {
				x += 1;
			}
			_insertMine(x, y);
		}
	}
	
	private static void _getMiningLocations() {
		if (hasPickAxe) {
			int right_nodes = 1,
			    top_nodes = 2,
			    left_nodes = 1,
			    bottom_nodes = 1;
			
			for (int i = 1; i < startRing; i++) {
				right_nodes += 2;
				top_nodes += 1;
				left_nodes += 2;
				bottom_nodes += 1;
			}
			_buildFastRingLocations(right_nodes, top_nodes, left_nodes, bottom_nodes);
		} else {
			int right_nodes = 1,
				top_nodes = 3,
				left_nodes = 1,
				bottom_nodes = 3;
				
			for (int i = 1; i < startRing; i++) {
				right_nodes += 2;
				top_nodes += 2;
				left_nodes += 2;
				bottom_nodes += 2;
			}
			_buildSlowRingLocations(right_nodes, top_nodes, left_nodes, bottom_nodes);
		}
	}
}
