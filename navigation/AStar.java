package team122.navigation;

import java.util.ArrayList;

import battlecode.common.MapLocation;
import team122.robot.TeamRobot;

public class AStar {
	TeamRobot robot;
	
	public AStar(TeamRobot robot) {
		this.robot = robot;
	}
	
	public MapLocation[] getPathTo(MapLocation loc) {
		
		
		return _aStar(robot.rc.getLocation(), loc);
	}
	
	
	private MapLocation[] _aStar(MapLocation start, MapLocation goal) {
		ArrayList<MapLocation> closedSet = new ArrayList<MapLocation>();
		ArrayList<MapLocation> openSet = new ArrayList<MapLocation>();
		ArrayList<MapLocation> cameFrom = new ArrayList<MapLocation>();
		
		return null;
	}

}
