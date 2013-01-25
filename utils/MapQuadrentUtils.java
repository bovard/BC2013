package team122.utils;

import battlecode.common.MapLocation;

public class MapQuadrentUtils {
	
	public static MapLocation hq;
	public static MapLocation enemyHq;
	public static int width;
	public static int height;

	public static MapLocation getMapCornerForQuadrent(int quad) {
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
	
	public static int getMapQuadrent(int x, int y) {
		//   0,0     0,width
		//	    2 | 1
		//      - . -
		//      3 | 4
		// height,0   height,width
		//  ( I know this is odd, don't want to turn it around in teh head)
		
		// calculate our quadrent
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
