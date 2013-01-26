package team122.communication;

import battlecode.common.MapLocation;

public class MapDecoder extends CommunicationDecoder {

	public MapLocation location;
	public int command;
	
	public MapDecoder(MapLocation location, int command) {
		this.location = location;
		this.command = command;
	}
	
	public MapDecoder(int data) {
		command = data / COMMAND_MULTIPLIER;
		int restOfData = data % COMMAND_MULTIPLIER;
		location = new MapLocation(restOfData / X_LOCATION_MULTIPLIER, restOfData % X_LOCATION_MULTIPLIER);
	}
	
	@Override
	public int getData() {
		// TODO Auto-generated method stub
		return command * COMMAND_MULTIPLIER + location.x * X_LOCATION_MULTIPLIER + location.y;
	}
	
	public static final int COMMAND_MULTIPLIER = 1000000;
	public static final int X_LOCATION_MULTIPLIER = 1000;
}
