package team122.communication;

import team122.robot.HQ;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;

public class Communicator {

	public RobotController rc;
	
	
	//TODO: If the channels idea with hashmap costs to many byte codes then we need to use
	//if / else statements.
	
	public Communicator(RobotController rc) {
		this.rc = rc;
	}
	
	/**
	 * Communicates the soldier mode to HQ.  This way HQ
	 * @param data
	 * @throws GameActionException 
	 */
	public void communicate(int mode, int round, int data) throws GameActionException {
		int[] channels = _channels(mode, round);
		
		rc.broadcast(channels[0], data);
		rc.broadcast(channels[1], data);
		rc.broadcast(channels[2], data);
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
	 * communicates with a decoder.
	 * @param com
	 * @param mode
	 * @param round
	 * @throws GameActionException 
	 */
	public void communicate(CommunicationDecoder com, int mode, int round) throws GameActionException {
		int[] channels = _channels(mode, round);
		communicate(channels, com.getData());
	}
	
	/**
	 * increments the value within the given mode, if no value is provided then it will communicate 1.
	 * @param mode
	 * @throws GameActionException 
	 */
	public void increment(int mode, int round) throws GameActionException {
		
		increment(mode, round, 1);
	}
	
	/**
	 * incrementing a channel by a variable amount.
	 * @param mode
	 * @param amount
	 */
	public void increment(int mode, int round, int amount) throws GameActionException {
		int[] channels = _channels(mode, round);
		int value = _getData(channels, -1);
		
		if (value <= 0) {
			communicate(channels, 1);
		} else {
			communicate(channels, ++value);
		}
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
	public void communicateWithPosition(CommunicationDecoder dec, int channel, int round) throws GameActionException {
		int data = dec.getData();
		int[] channels = _channels(channel, round);
		
		rc.broadcast(channels[0], data);
		rc.broadcast(channels[1], data);
		rc.broadcast(channels[2], data);
	}

	/**
	 * Receives data with map location.
	 * @throws GameActionException 
	 */
	public SoldierDecoder receiveNewSoldier() throws GameActionException {
		return new SoldierDecoder(_getData(_channels(Communicator.CHANNEL_NEW_SOLDIER_MODE, Clock.getRoundNum()), 0));
	}
	
	/**
	 * The soldier will call this to determine what type of soldier to become.
	 * @throws GameActionException 
	 */
	public int receive(int mode, int round) throws GameActionException {	
		
		return _getData(_channels(mode, round), -1);
	}

	
	/**
	 * The soldier will call this to determine what type of soldier to become.
	 * @throws GameActionException 
	 */
	public int receive(int mode, int round, int defaultData) throws GameActionException {	
		
		return _getData(_channels(mode, round), defaultData);
	}
	
	/**
	 * Clears out the channel to 0.
	 * @param round
	 * @throws GameActionException 
	 */
	public void clear(int round) throws GameActionException {
		for (int i = 1; i < CHANNEL_COUNT; i++) {
			communicate(i, round, 0);
		}
	}
	
	/**
	 * If the nuke is armed then alert like crazy.
	 * @param round
	 * @throws GameActionException 
	 */
	public void nukeIsArmed() throws GameActionException {
		
		communicate(CHANNEL_NUKE_IS_ARMED, Clock.getRoundNum(), HQ.NUKE_IS_ARMED);
		communicate(CHANNEL_NUKE_IS_ARMED_BACKUP, Clock.getRoundNum(), HQ.NUKE_IS_ARMED);
	}
	
	/**
	 * If the nuke is armed then alert like crazy.
	 * @param round
	 * @throws GameActionException 
	 */
	public boolean isNukeArmed() throws GameActionException {
		
		int nukeArmedCode = receive(CHANNEL_NUKE_IS_ARMED, Clock.getRoundNum(), 0);
		if (nukeArmedCode == receive(CHANNEL_NUKE_IS_ARMED_BACKUP, Clock.getRoundNum(), 0) && 
				nukeArmedCode == HQ.NUKE_IS_ARMED) {
			
			//Nuke is armed
			return true;
		}
		return false;
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
	 * Creates the channels from the mode.
	 * @param mode
	 * @param round
	 * @return
	 */
	private int[] _channels(int mode, int round) {
		int c1 = (mode * round * ALPHA) % MAX_CHANNELS;
		
		return new int[] {
			c1, (c1 + BETA) % MAX_CHANNELS, (c1 + BETA + BETA) % MAX_CHANNELS
		};
	}
	
	/**
	 * The additional difference between communication channel.
	 */
	public static final int MAX_CHANNELS = GameConstants.BROADCAST_MAX_CHANNELS;
	public static final int ALPHA = 39989;
	public static final int BETA = 30029;
	public static final int CHANNEL_NEW_SOLDIER_MODE = 1;
	public static final int CHANNEL_GENERATOR_COUNT = 2;
	public static final int CHANNEL_SUPPLIER_COUNT = 3;
	public static final int CHANNEL_ARTILLERY_COUNT = 4;
	public static final int CHANNEL_SOLDIER_COUNT = 5;
	public static final int CHANNEL_MINER_COUNT = 7;
	public static final int CHANNEL_DEFENDER_COUNT = 8;
	public static final int CHANNEL_NUKE_COUNT = 9;
	public static final int CHANNEL_ENCAMPER_HUNTER_COUNT = 10;
	public static final int CHANNEL_BACKDOOR_COUNT = 11;
	public static final int CHANNEL_NUKE_IS_ARMED = 12;
	public static final int CHANNEL_NUKE_IS_ARMED_BACKUP = 13;
	public static final int CHANNEL_GROUP_0 = 14;
	public static final int CHANNEL_GROUP_1 = 15;
	public static final int CHANNEL_GROUP_2 = 16;
	public static final int CHANNEL_COUNT = 16;
}
