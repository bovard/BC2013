package team122.aStar;

import battlecode.common.MapLocation;
import java.util.ArrayList;
import java.lang.Math;

public class AStar {
	private MapLocation startLoc,
     	                endLoc,
     	                upperLeftBound,
     	                lowerRightBound;
	
	private MapLocation[] objects,
						  mines;
	
	private boolean research;
	
	private ArrayList<MapLocation> path = new ArrayList<MapLocation>();
	
	int width,
        height;
	int[][] map;
	MapLocation[][] prevLocTracker;
	
	// int[x][y] distance from a central node starting in NW and going clockwise
	int[][] adjacentDist = new int[][]{{-1, 0, 1, 1, 1, 0, -1, -1},
								       {-1, -1, -1, 0, 1, 1, 1, 0}};
	
	int mineHeuristic = 12;
	
	AStar(MapLocation startLoc, MapLocation endLoc, MapLocation upperLeftBound,
			MapLocation lowerRightBound, MapLocation[] objects, MapLocation[] mines, boolean research) {
        this.startLoc = startLoc;
        this.endLoc = endLoc;
        this.upperLeftBound = upperLeftBound;
        this.lowerRightBound = lowerRightBound;
        this.objects = objects;
        this.mines = mines;
        this.research = research;
        
        if (this.research) {
        	mineHeuristic = 5;
        }
        
        map = buildMap();
        prevLocTracker = new MapLocation[width][height];
        findPath();
	}
	
	public MapLocation[] getPath() {
		MapLocation[] pathArray = new MapLocation[path.size()];
		for (int i = 0; i < path.size(); i++) {
			pathArray[i] = path.get(i);
		}
		return pathArray;
	}
	
	private int[][] buildMap() {
		width = lowerRightBound.x - upperLeftBound.x + 1;
	    height = lowerRightBound.y - upperLeftBound.y + 1;
        int x,
            y;
        int[][] map = new int[height][width];
        
        // objects are represented in the map as the number 2
        for (int i = 0; i < objects.length; i++) {
        	y = objects[i].y - upperLeftBound.y;
        	x = objects[i].x - upperLeftBound.x;
        	if (x < width && x >= 0 && y < height && y >= 0) {
        		map[y][x] = 2;
        	}
        }
        
        // mines are represented in the map as the number 1
        for (int j = 0; j < mines.length; j++) {
        	y = mines[j].y - upperLeftBound.y;
        	x = mines[j].x - upperLeftBound.x;
        	if (x < width && x >= 0 && y < height && y >= 0) {
        		map[y][x] = 1;
        	}
        }
        
        // starting location is represented in the map as an 8
        x = startLoc.x - upperLeftBound.x;
        y = startLoc.y - upperLeftBound.y;
        map[y][x] = 8;
        
        // ending location is represented in the map as a 9
        x = endLoc.x - upperLeftBound.x;
        y = endLoc.y - upperLeftBound.y;
        map[y][x] = 9;
        
        // PRINTING MAP FOR DEBUGGING
//        for (int i = 0; i < height; i++) {
//        	for (int j = 0; j < width; j++) {
//        		System.out.print(map[i][j] + " ");
//        	}
//        	System.out.println("");
//        }
//        System.out.println("");
        return map;
	}
	
    public void findPath() {
    	MapLocation start = new MapLocation(startLoc.x - upperLeftBound.x, startLoc.y - upperLeftBound.y);
    	MapLocation end = new MapLocation(endLoc.x - upperLeftBound.x, endLoc.y - upperLeftBound.y);
    	
    	ArrayList<MapLocation> closedSet = new ArrayList<MapLocation>(); // already evaluated (initially empty)
    	ArrayList<MapLocation> openSet = new ArrayList<MapLocation>(); // to evaluate (initially just the start)
            
        openSet.add(start);

        while (openSet.size() != 0) {
            MapLocation current = openSet.get(0);

            if (current.x == end.x && current.y == end.y) {
                reconstructPath(current);
            }
            
            openSet.remove(current);
            closedSet.add(current);

            int shortest = 0,
                distEnd,
                dX,
                dY;
            for (int i = 0; i < 8; i++) {
            	if (i == 0) {
            		shortest = 0;
            	}
            	MapLocation adjacent = new MapLocation(current.x + adjacentDist[0][i], current.y + adjacentDist[1][i]);
            	
            	if (adjacent.x < width && adjacent.x >= 0 && adjacent.y < height && adjacent.y >= 0) {
                    if (closedSet.contains(adjacent)) {
                        continue;
                    }
                    
                    if (map[adjacent.y][adjacent.x] != 2 /*&& map[adjacent.x][adjacent.y] != 1*/) {
                        if(!openSet.contains(adjacent)) {
                        	dX = end.x - adjacent.x;
                        	dY = end.y - adjacent.y;

                        	if (map[adjacent.y][adjacent.x] == 1) {
                        		//distEnd = dX*dX + dY*dY + mineHeuristic * mineHeuristic * mineHeuristic;
                        		distEnd = Math.abs(dX) + Math.abs(dY) + mineHeuristic / 2 + 1;
                        	} else {
                        		//distEnd = dX*dX + dY*dY;
                        		distEnd = Math.abs(dX) + Math.abs(dY);
                        	}
                        	if (shortest == 0) {
                        		shortest = distEnd;
                        	}
                        	if (distEnd <= shortest) {
                        		shortest = distEnd;
	                    		openSet.add(0, adjacent);
	                    		prevLocTracker[adjacent.x][adjacent.y] = current;
                        	}
                        }
                    }
            	}
            }
	    }
    }

    private void reconstructPath(MapLocation loc) {
    	ArrayList<MapLocation> shortest = new ArrayList<MapLocation>();
    	while(prevLocTracker[loc.x][loc.y] != null) {
    		MapLocation convertedLoc = new MapLocation(loc.x + upperLeftBound.x - 1, loc.y + upperLeftBound.y - 1);
    		shortest.add(0, convertedLoc);
    		map[loc.y][loc.x] = 8;
			loc = prevLocTracker[loc.x][loc.y];
    	}
    	path = shortest;
    	// PRINTING MAP FOR DEBUGGING
//        for (int i = 0; i < height; i++) {
//        	for (int j = 0; j < width; j++) {
//        		System.out.print(map[i][j] + " ");
//        	}
//        	System.out.println("");
//        }
    }
}