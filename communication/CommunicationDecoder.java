package team122.communication;

import battlecode.common.MapLocation;

public class CommunicationDecoder {
	public MapLocation location;
	public int command;
	
	public CommunicationDecoder(int data) {
		command = data / 1000000;
		
		int restOfData = data % 1000000;
		location = new MapLocation(restOfData / 1000, restOfData % 1000);
	}
	
	/**
	 * SEts the communcation decoder
	 * @param location
	 * @param command
	 */
	public CommunicationDecoder(MapLocation location, int command) {
		this.location = location;
		this.command = command;
	}
	
	public int toData() {
		return command * 1000000 + location.x * 1000 + location.y;
	}
}
