package team122.utils;

import java.util.ArrayList;

import battlecode.common.MapLocation;
	



public class MinePlacement {
	
	public static int width;
	public static int height;
	public static ArrayList<MapLocation> mineSpots;
	
	
	private static void insertMine(int x, int y) {
		MapLocation created;
		if (x >= 0 && x < width && y >= 0 && y < height) {
			created = new MapLocation(x, y);
			mineSpots.add(created);
		}
	}
	
	private static void _assignRing(int startX, int startY, 
									int start_ring, int end_ring, 
									int right_nodes, int top_nodes, 
									int left_nodes, int bottom_nodes) {
		int x = startX,
			y = startY;
		
		if (start_ring != 1) {
			x += (start_ring - 2) * 2;
			y += (start_ring - 1) * 2;
		}
		
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
				insertMine(x, y);
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
				insertMine(x, y);
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
				insertMine(x, y);
			}
			// insert bottom nodes
			for (int m = 1; m <= bottom_nodes; m++) {
				if (m == 1) {
					x += 2;
					y += 2;
				} else {
					x += 3;
				}
				insertMine(x, y);
			}
			right_nodes += 2;
			top_nodes += 1;
			left_nodes += 2;
			bottom_nodes += 1;
		}
	}
	
	public static ArrayList<MapLocation> getMiningLocations(int mapWidth, int mapHeight, int startX, int startY, 
			int start_ring, int end_ring, boolean hasPickAxe) {
		mineSpots = new ArrayList<MapLocation>();
		width = mapWidth;
		height = mapHeight;
		
		if (hasPickAxe) {
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
			_assignRing(startX, startY, start_ring, end_ring, right_nodes, top_nodes, left_nodes, bottom_nodes);
		} else {
			// lay mines one at a time
			int right_nodes = 1,
				top_nodes = 3,
				left_nodes = 1,
				bottom_nodes = 3,
				x = startX,
				y = startY;
			
			for (int i = 0; i <= 12; i++) {
				// insert right nodes
				for (int j = 0; j < right_nodes; j++) {
					if (j == 0) {
						x += 1;
					} else {
						y -= 1;
					}
					insertMine(x, y);
				}
				// insert top nodes
				for (int k = 0; k < top_nodes; k++) {
					if (k == 0) {
						y -= 1;
					} else {
						x -= 1;
					}
					insertMine(x, y);
				}
				// insert left nodes
				for (int l = 0; l < left_nodes; l++) {
					y += 1;
					insertMine(x, y);
				}
				// insert bottom nodes
				for (int m = 0; m < bottom_nodes; m++) {
					if (m == 0) {
						y += 1;
					} else {
						x += 1;
					}
					insertMine(x, y);
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
