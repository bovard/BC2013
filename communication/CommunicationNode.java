package team122.communication;

import battlecode.common.MapLocation;

public class CommunicationNode {
	public MapLocation location;
	public int command;
	
	public CommunicationNode(int data) {
		command = data / 1000000;
		
		int restOfData = data % 1000000;
		location = new MapLocation(restOfData / 1000, restOfData % 1000);
	}
}
