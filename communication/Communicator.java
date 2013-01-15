package team122.communication;

import team122.RobotInformation;
import battlecode.common.GameActionException;
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
		modeA = rc.readBroadcast(CHANNEL_COMMUNICATE_SOLDIER_MODE[0]);
		modeB = rc.readBroadcast(CHANNEL_COMMUNICATE_SOLDIER_MODE[1]);
		
		if (modeA == modeB) {
			return modeA;
		} else {
			int modeC = rc.readBroadcast(CHANNEL_COMMUNICATE_SOLDIER_MODE[2]);
			
			if (modeA == modeC) {
				return modeA;
			} else if (modeB == modeC) {
				return modeB;
			}
		}
		
		return defaultValue;
	}
	
	public static final int[] CHANNEL_COMMUNICATE_SOLDIER_MODE = new int[] {15321, 16322, 17881};
}
