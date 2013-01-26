package team122.communication;

import team122.behavior.soldier.SoldierSelector;
import battlecode.common.MapLocation;

public class SoldierDecoder extends CommunicationDecoder {
	public int soldierType;
	public int groupOrEncampmentType;
	public MapLocation loc;
	
	public SoldierDecoder(int soldierType, int group) {
		this.soldierType = soldierType;
		this.groupOrEncampmentType = group;
	}
	
	public SoldierDecoder(int encamperType, MapLocation loc) {
		this.soldierType = SoldierSelector.SOLDIER_ENCAMPER;
		this.groupOrEncampmentType = encamperType;
		this.loc = loc;
	}
	
	public SoldierDecoder(int data) {
		soldierType = data / SOLDIER_TYPE_MULTIPLIER;
		groupOrEncampmentType = (data % SOLDIER_TYPE_MULTIPLIER) / GROUP_MULTIPLIER;
		
		int restOfData = data % GROUP_MULTIPLIER;
		if (restOfData % GROUP_MULTIPLIER > 0) {
			loc = new MapLocation(restOfData / X_LOCATION_MULTIPLIER, restOfData % X_LOCATION_MULTIPLIER);
		}
	}
	
	/**
	 * Extra data that is allowed to be set.
	 * @param loc
	 */
	public void setLocation(MapLocation loc) {
		this.loc = loc;
	}
	
	/**
	 * Strinifies this
	 */
	@Override
	public String toString() {
		return "Soldier Decoder: " + loc + " of " + soldierType + " : " + groupOrEncampmentType;
	}
	
	@Override
	public int getData() {
		int data = soldierType * SOLDIER_TYPE_MULTIPLIER + groupOrEncampmentType * GROUP_MULTIPLIER;
		
		if (loc != null) {
			data += loc.x * X_LOCATION_MULTIPLIER;
			data += loc.y;
		}
		
		return data;
	}
	
	//The soldier type multiplier
	private static final int SOLDIER_TYPE_MULTIPLIER = 10000000;
	private static final int GROUP_MULTIPLIER = 1000000;
	private static final int X_LOCATION_MULTIPLIER = 1000;
}
