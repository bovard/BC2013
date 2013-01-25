package team122.utils;

import java.util.ArrayList;

import battlecode.common.MapLocation;
	



public class MinePlacement {
	
	public static MapLocation hq;
	public static int width;
	public static int height;
	
	
	private void insertMine(int x, int y, ArrayList<MapLocation> mineSpots) {
		int map_width = width,
	        map_height = height;
		MapLocation created;
		if (x >= 0 && x < map_width && y >= 0 && y < map_height) {
			created = new MapLocation(x, y);
			mineSpots.add(created);
		}
	}
	
	private void _assignRing(int start_ring, int end_ring, int right_nodes, int top_nodes, int left_nodes, int bottom_nodes, ArrayList<MapLocation> mineSpots) {
		int hq_x = hq.x; 
		int hq_y = hq.y;
		
		int x = hq_x,
			y = hq_y;
		
		for (int i = start_ring; i <= end_ring; i++) {
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
				insertMine(x, y, mineSpots);
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
				insertMine(x, y, mineSpots);
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
				insertMine(x, y, mineSpots);
			}
			// insert bottom nodes
			for (int m = 1; m <= bottom_nodes; m++) {
				if (m == 1) {
					x += 2;
					y += 2;
				} else {
					x += 3;
				}
				insertMine(x, y, mineSpots);
			}
			right_nodes += 2;
			top_nodes += 1;
			left_nodes += 2;
			bottom_nodes += 1;
		}
	}
	
	public ArrayList<MapLocation> getMiningLocations(int start_ring, int end_ring, boolean hasPickAxe) {
		ArrayList<MapLocation> mineSpots = new ArrayList<MapLocation>();
		
		if (hasPickAxe) {
			mineSpots.clear();
			
			int right_nodes = 1,
			    top_nodes = 2,
			    left_nodes = 1,
			    bottom_nodes = 1;
			
			for (int i = 1; i < start_ring; i++) {
				right_nodes += 2;
				top_nodes += 1;
				left_nodes += 2;
				bottom_nodes += 1;
			}
			_assignRing(start_ring, end_ring, right_nodes, top_nodes, left_nodes, bottom_nodes, mineSpots);
		} else {
			int hq_x = hq.x;
			int hq_y = hq.y;
			// lay mines one at a time
			int right_nodes = 1,
				top_nodes = 3,
				left_nodes = 1,
				bottom_nodes = 3,
				x = hq_x,
				y = hq_y;
			
			for (int i = 0; i <= 12; i++) {
				// insert right nodes
				for (int j = 0; j < right_nodes; j++) {
					if (j == 0) {
						x += 1;
					} else {
						y -= 1;
					}
					insertMine(x, y, mineSpots);
				}
				// insert top nodes
				for (int k = 0; k < top_nodes; k++) {
					if (k == 0) {
						y -= 1;
					} else {
						x -= 1;
					}
					insertMine(x, y, mineSpots);
				}
				// insert left nodes
				for (int l = 0; l < left_nodes; l++) {
					y += 1;
					insertMine(x, y, mineSpots);
				}
				// insert bottom nodes
				for (int m = 0; m < bottom_nodes; m++) {
					if (m == 0) {
						y += 1;
					} else {
						x += 1;
					}
					insertMine(x, y, mineSpots);
				}
				right_nodes += 2;
				top_nodes += 2;
				left_nodes += 2;
				bottom_nodes += 2;
			}
		}
		return mineSpots;
	}
}
