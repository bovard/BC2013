package team122.communication;

import java.util.HashMap;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Communicator {

	public RobotController rc;
	private HashMap<Integer, Integer[]> modeToChannels;
	private boolean init = false;
	
	//TODO: If the channels idea with hashmap costs to many byte codes then we need to use
	//if / else statements.
	
	public Communicator(RobotController rc) {
		this.rc = rc;

		modeToChannels = new HashMap<Integer, Integer[]>();
		init = true;
	}
	
	/**
	 * Communicates the soldier mode to HQ.  This way HQ
	 * @param data
	 * @throws GameActionException 
	 */
	public void communicate(int mode, int data) throws GameActionException {
		Integer[] channels = modeToChannels.get(mode);
		
		rc.broadcast(channels[0], data);
		rc.broadcast(channels[1], data);
		rc.broadcast(channels[2], data);
	}
	
	/**
	 * Shortcut for the communicate channel.
	 * @param channels
	 * @param data
	 * @throws GameActionException
	 */
	public void communicate(Integer[] channels, int data) throws GameActionException {
		rc.broadcast(channels[0], data);
		rc.broadcast(channels[1], data);
		rc.broadcast(channels[2], data);
	}
	
	/**
	 * increments the value within the given mode, if no value is provided then it will communicate 1.
	 * @param mode
	 * @throws GameActionException 
	 */
	public void increment(int mode) throws GameActionException {
		Integer[] channels = modeToChannels.get(mode);
		int value = _getData(channels, -1);
		
		if (value < 0) {
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
	public CommunicationNode receiveWithLocation(int mode) throws GameActionException {
		return new CommunicationNode(_getData(modeToChannels.get(mode), 0));
	}
	
	/**
	 * The soldier will call this to determine what type of soldier to become.
	 * @throws GameActionException 
	 */
	public int receive(int mode) throws GameActionException {	
		return _getData(modeToChannels.get(mode), -1);
	}
	
	/**
	 * Gets the data but allows for a default.
	 * @param channels
	 * @param defaultData
	 * @return
	 * @throws GameActionException
	 */
	public int receive(int mode, int defaultData) throws GameActionException {
		return _getData(modeToChannels.get(mode), defaultData);
	}	
	
	/**
	 * Gets data from the stream.
	 * @param channels
	 * @param defaultValue
	 * @return
	 * @throws GameActionException 
	 */
	private int _getData(Integer[] channels, int defaultValue) throws GameActionException {

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
	 * Initializes the channels with the seed provided.
	 * @param seed
	 */
	public void seedChannels(int seed, int[] channels) {
		
		//Reseeds the channels.
		modeToChannels.clear();
		int seedMul = seed * SEED_MULTIPLIER;
		int seedMulDiff = seedMul * CHANNEL_DIFFERENCE;
		int seedMulDiff2x = seedMulDiff * 2;

		for (int i = 0, len = channels.length; i < len; i++) {

			Integer[] c = new Integer[3];
			
			if (channels[i] == CHANNEL_NEW_SOLDIER_MODE) {

				//New Soldier	
				c[0] = CHANNEL_NEW_SOLDIER_RANGE[0] + seedMul % CHANNEL_NEW_SOLDIER_RANGE[1];
				c[1] = CHANNEL_NEW_SOLDIER_RANGE[0] + seedMulDiff % CHANNEL_NEW_SOLDIER_RANGE[1];
				c[2] = CHANNEL_NEW_SOLDIER_RANGE[0] + seedMulDiff2x % CHANNEL_NEW_SOLDIER_RANGE[1];
			} else if (channels[i] == CHANNEL_GENERATOR_COUNT) {
				
				//Generator Count
				c[0] = CHANNEL_GENERATOR_COUNT_RANGE[0] + seedMul % CHANNEL_GENERATOR_COUNT_RANGE[1];
				c[1] = CHANNEL_GENERATOR_COUNT_RANGE[0] + seedMulDiff % CHANNEL_GENERATOR_COUNT_RANGE[1];
				c[2] = CHANNEL_GENERATOR_COUNT_RANGE[0] + seedMulDiff2x % CHANNEL_GENERATOR_COUNT_RANGE[1];
			} else if (channels[i] == CHANNEL_ARTILLERY_COUNT) {
				
				//Artillery Count
				c[0] = CHANNEL_ARTILLERY_COUNT_RANGE[0] + seedMul % CHANNEL_ARTILLERY_COUNT_RANGE[1];
				c[1] = CHANNEL_ARTILLERY_COUNT_RANGE[0] + seedMulDiff % CHANNEL_ARTILLERY_COUNT_RANGE[1];
				c[2] = CHANNEL_ARTILLERY_COUNT_RANGE[0] + seedMulDiff2x % CHANNEL_ARTILLERY_COUNT_RANGE[1];
			} else if (channels[i] == CHANNEL_SUPPLIER_COUNT) {
				
				//Supplier Count
				c[0] = CHANNEL_SUPPLIER_COUNT_RANGE[0] + seedMul % CHANNEL_SUPPLIER_COUNT_RANGE[1];
				c[1] = CHANNEL_SUPPLIER_COUNT_RANGE[0] + seedMulDiff % CHANNEL_SUPPLIER_COUNT_RANGE[1];
				c[2] = CHANNEL_SUPPLIER_COUNT_RANGE[0] + seedMulDiff2x % CHANNEL_SUPPLIER_COUNT_RANGE[1];
			} else if (channels[i] == CHANNEL_SOLDIER_COUNT) {
				
				//Soldier Count
				c[0] = CHANNEL_SOLDIER_COUNT_RANGE[0] + seedMul % CHANNEL_SOLDIER_COUNT_RANGE[1];
				c[1] = CHANNEL_SOLDIER_COUNT_RANGE[0] + seedMulDiff % CHANNEL_SOLDIER_COUNT_RANGE[1];
				c[2] = CHANNEL_SOLDIER_COUNT_RANGE[0] + seedMulDiff2x % CHANNEL_SOLDIER_COUNT_RANGE[1];
			} else if (channels[i] == CHANNEL_MINER_COUNT) {
				
				//Miner Count
				c[0] = CHANNEL_MINER_COUNT_RANGE[0] + seedMul % CHANNEL_MINER_COUNT_RANGE[1];
				c[1] = CHANNEL_MINER_COUNT_RANGE[0] + seedMulDiff % CHANNEL_MINER_COUNT_RANGE[1];
				c[2] = CHANNEL_MINER_COUNT_RANGE[0] + seedMulDiff2x % CHANNEL_MINER_COUNT_RANGE[1];
			} else if (channels[i] == CHANNEL_ENCAMPER_COUNT) {
				
				//Encamper Count
				c[0] = CHANNEL_ENCAMPER_COUNT_RANGE[0] + seedMul % CHANNEL_ENCAMPER_COUNT_RANGE[1];
				c[1] = CHANNEL_ENCAMPER_COUNT_RANGE[0] + seedMulDiff % CHANNEL_ENCAMPER_COUNT_RANGE[1];
				c[2] = CHANNEL_ENCAMPER_COUNT_RANGE[0] + seedMulDiff2x % CHANNEL_ENCAMPER_COUNT_RANGE[1];
			} else if (channels[i] == CHANNEL_ENCAMPER_COUNT) {
				
				//Defender Count
				c[0] = CHANNEL_DEFENDER_COUNT_RANGE[0] + seedMul % CHANNEL_DEFENDER_COUNT_RANGE[1];
				c[1] = CHANNEL_DEFENDER_COUNT_RANGE[0] + seedMulDiff % CHANNEL_DEFENDER_COUNT_RANGE[1];
				c[2] = CHANNEL_DEFENDER_COUNT_RANGE[0] + seedMulDiff2x % CHANNEL_DEFENDER_COUNT_RANGE[1];
			}

			modeToChannels.put(channels[i], c);
		} // end for 
	}
	
	/**
	 * The additional difference between communication channel.
	 */
	public static final int CHANNEL_DIFFERENCE = 1021;
	public static final int CHANNEL_NEW_SOLDIER_MODE = 1;
	public static final int CHANNEL_GENERATOR_COUNT = 2;
	public static final int CHANNEL_SUPPLIER_COUNT = 3;
	public static final int CHANNEL_ARTILLERY_COUNT = 4;
	public static final int CHANNEL_SOLDIER_COUNT = 5;
	public static final int CHANNEL_ENCAMPER_COUNT = 6;
	public static final int CHANNEL_MINER_COUNT = 7;
	public static final int CHANNEL_DEFENDER_COUNT = 7;
	public static final int SEED_MULTIPLIER = 17;
	
	//Ranges:   {StartPt, Range}
	public static final Integer[] CHANNEL_NEW_SOLDIER_RANGE = new Integer[] {3000, 4000};
	public static final Integer[] CHANNEL_GENERATOR_COUNT_RANGE = new Integer[] {7000, 4000};
	public static final Integer[] CHANNEL_SUPPLIER_COUNT_RANGE = new Integer[] {11000, 4000};
	public static final Integer[] CHANNEL_ARTILLERY_COUNT_RANGE = new Integer[] {15000, 4000};
	public static final Integer[] CHANNEL_MINER_COUNT_RANGE = new Integer[] {19000, 4000};
	public static final Integer[] CHANNEL_SOLDIER_COUNT_RANGE = new Integer[] {23000, 4000};
	public static final Integer[] CHANNEL_ENCAMPER_COUNT_RANGE = new Integer[] {27000, 4000};
	public static final Integer[] CHANNEL_DEFENDER_COUNT_RANGE = new Integer[] {31000, 4000};
}
