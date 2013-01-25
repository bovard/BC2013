package team122.utils;

import java.util.ArrayList;

import battlecode.common.MapLocation;

public class MapQuadrantUtils {
	
	// IMPORTANT: TO USE THIS YOU MUST SET hq, enemyHQ, width, height!
	
	public static MapLocation hq;
	public static MapLocation enemyHq;
	public static int width;
	public static int height;
	
	public ArrayList<MapLocation> getPathToQuadrant(int startQuad, int targetQuad) {
		ArrayList<MapLocation> wayPoints = new ArrayList<MapLocation>();
		if (startQuad == 1) {
			if (targetQuad == 1) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(1));
			} else if (targetQuad == 2) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(4));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(3));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(2));
			} else if (targetQuad == 3) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(1));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(2));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(3));
			} else {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(2));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(3));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(4));
			}
		} else if (startQuad == 2) {
			if (targetQuad == 1) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(3));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(4));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(1));
			} else if (targetQuad == 2) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(2));
			} else if (targetQuad == 3) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(1));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(4));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(3));
			} else {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(2));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(1));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(4));
			}
		//		2 | 1
		//      - . -
		//      3 | 4
			
		} else if (startQuad == 3) {
			if (targetQuad == 1) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(3));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(2));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(1));
			} else if (targetQuad == 2) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(4));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(1));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(2));
			} else if (targetQuad == 3) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(3));
			} else {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(2));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(1));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(4));
			}
		} else {
			if (targetQuad == 1) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(3));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(2));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(1));
			} else if (targetQuad == 2) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(4));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(3));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(2));
			} else if (targetQuad == 3) {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(1));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(2));
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(3));
			} else {
				wayPoints.add(MapQuadrantUtils.getMapCornerForQuadrant(4));
			}
		}
		return wayPoints;
	}

	public static MapLocation getMapCornerForQuadrant(int quad) {
		//   0,0     0,width
		//	    2 | 1
		//      - . -
		//      3 | 4
		// height,0   height,width
		//  ( I know this is odd, don't want to turn it around in teh head)
		
		switch (quad) {
		case 1:
			return new MapLocation(width - 1, 0);
		case 2:
			return new MapLocation(0, 0);
		case 3:
			return new MapLocation(0, height - 1);
		case 4:
			return new MapLocation(width - 1, height - 1);
		}
		return null;
	}
	
	public static int getMapQuadrant(int x, int y) {
		//   0,0     0,width
		//	    2 | 1
		//      - . -
		//      3 | 4
		// height,0   height,width
		//  ( I know this is odd, don't want to turn it around in teh head)
		
		// calculate our quadrant
		// left side
		if (x < width/2) {
			// top side
			if (y < height/2){
				return 2;
			}
			// bottom side
			else {
				return 3;
			}
		}
		// east side
		else {
			// top side
			if (y < height/2){
				return 1;
			}
			// bottom side
			else {
				return 4;
			}
		}
	}
}
