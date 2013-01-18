package team122.communication;

import team122.RobotInformation;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Communicator {

	public RobotController rc;
	public RobotInformation info;
	
	public Communicator(RobotController rc, RobotInformation info) {
		this.rc = rc;
		this.info = info;
	}
	
	/**
	 * Communicates the soldier mode to HQ.  This way HQ
	 * @param data
	 * @throws GameActionException 
	 */
	public void communicate(int[] channels, int data) throws GameActionException {
		rc.broadcast(channels[0], data);
		rc.broadcast(channels[1], data);
		rc.broadcast(channels[2], data);
	}
	
	/**
	 * Will put the data in the first million going
	 * 2,[DDD],[XXX],[YYY]
	 * 
	 * D = Data
	 * X = X Location
	 * Y = Y Location
	 * 
	 * @param channels
	 * @param loc
	 * @param data
	 * @throws GameActionException
	 */
	public void communicateWithPosition(int[] channels, MapLocation loc, int data) throws GameActionException {
		data += loc.x * 1000;
		data += loc.y;
		
		rc.broadcast(channels[0], data);
		rc.broadcast(channels[1], data);
		rc.broadcast(channels[2], data);
	}

	/**
	 * Receives data with map location.
	 * @throws GameActionException 
	 */
	public CommunicationNode receiveWithLocation(int[] channels) throws GameActionException {
		return new CommunicationNode(_getData(channels, 0));
	}
	
	/**
	 * The soldier will call this to determine what type of soldier to become.
	 * @throws GameActionException 
	 */
	public int receive(int[] channels) throws GameActionException {	
		return _getData(channels, -1);
	}
	
	/**
	 * Gets the data but allows for a default.
	 * @param channels
	 * @param defaultData
	 * @return
	 * @throws GameActionException
	 */
	public int receive(int[] channels, int defaultData) throws GameActionException {
		return _getData(channels, defaultData);
	}
	
	/**
	 * Gets data from the stream.
	 * @param channels
	 * @param defaultValue
	 * @return
	 * @throws GameActionException 
	 */
	private int _getData(int[] channels, int defaultValue) throws GameActionException {
		int modeA = 0, modeB = 0;
		modeA = rc.readBroadcast(channels[0]);
		modeB = rc.readBroadcast(channels[1]);
		
		if (modeA == modeB) {
			return modeA;
		} else {
			int modeC = rc.readBroadcast(channels[2]);
			
			if (modeA == modeC) {
				return modeA;
			} else if (modeB == modeC) {
				return modeB;
			}
		}
		
		return defaultValue;
	}
	
	/**
	 * The additional difference between communication channel.
	 */
	public static final int CHANNEL_DIFFERENCE = 1021;
	public static final int[] CHANNEL_NEW_SOLDIER_MODE = new int[] {15321, 16322, 17881};
}
