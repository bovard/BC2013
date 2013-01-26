package team122.aStar;

import battlecode.common.MapLocation;
import team122.aStar.AStar;

public class TestDriver {
	private static MapLocation startLoc 		= new MapLocation(202, 102),
			            	   endLoc 			= new MapLocation(212, 112),
			            	   upperLeftBound 	= new MapLocation(200, 100),
			            	   lowerRightBound  = new MapLocation(214, 114);
	
	private static MapLocation[] objects = new MapLocation[] {
		new MapLocation(202, 107),
		new MapLocation(203, 106),
		new MapLocation(203, 107),
		new MapLocation(203, 108),
		new MapLocation(204, 107),
		new MapLocation(208, 104),
		new MapLocation(209, 103),
		new MapLocation(209, 104),
		new MapLocation(209, 105),
		new MapLocation(210, 104)
	};
	
	private static MapLocation[] mines = new MapLocation[] {
		new MapLocation(204, 105),
		new MapLocation(204, 106),
		new MapLocation(205, 106),
		new MapLocation(206, 105),
		new MapLocation(206, 106),
		new MapLocation(207, 105),
		new MapLocation(207, 106),
		new MapLocation(208, 105),
		new MapLocation(208, 106),
		new MapLocation(205, 107),
		new MapLocation(206, 107),
		new MapLocation(207, 107)
	};
	
	private static boolean research = true;
	
	public static void main(String[] args) {
		// using try catch for debugging
		try {
			AStar pathGenerator = new AStar(startLoc, endLoc, upperLeftBound, lowerRightBound,
					               objects, mines, research);
			
			MapLocation[] path = pathGenerator.getPath();
			
			// PRINT FINAL PATH FOR DEBUGGING
//			for (int i = 0; i < path.length; i++) {
//				int x = path[i].x;
//				int y = path[i].y;
//				if (i != path.length - 1) {
//					System.out.print("(" + x + ", " + y + "), ");
//				} else {
//					System.out.print("(" + x + ", " + y + ")");
//				}
//			} 
		} catch(java.lang.Throwable t) {
		   System.out.println(t.getClass().getName());
		   t.printStackTrace();
		}
	}
}