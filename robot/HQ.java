package team122.robot;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team122.RobotInformation;
import team122.behavior.hq.HQUtils;
import team122.communication.Communicator;
import team122.trees.HQTree;

public class HQ extends TeamRobot {
	public HQUtils hqUtils;
	public int military;
	public int econ;
	public int tech;
	public int defensive;
	public int enemyHQDistance;
	
	public HQ(RobotController rc, RobotInformation info) {
		super(rc, info);
		hqUtils = new HQUtils(rc, com);
		tree = new HQTree(this);
		com.seedChannels(5, new int[] {
			Communicator.CHANNEL_NEW_SOLDIER_MODE, 
			Communicator.CHANNEL_GENERATOR_COUNT, 
			Communicator.CHANNEL_SUPPLIER_COUNT, 
			Communicator.CHANNEL_ARTILLERY_COUNT, 
			Communicator.CHANNEL_SOLDIER_COUNT, 
			Communicator.CHANNEL_MINER_COUNT, 
			Communicator.CHANNEL_ENCAMPER_COUNT
		});
		military = 0;
		econ = 0;
		tech = 0;
		defensive = 0;
	}

	@Override
	public void environmentCheck() throws GameActionException {
		//TODO: What's the environment check here?
		
		if (Clock.getRoundNum() % HQ_COUNT_ROUND == 0) {
			hqUtils.counts();
			
//			if (Clock.getRoundNum() % (HQ_COUNT_ROUND * 10) == 0) {
//				hqUtils.printState();
//			}
		}
	}


	/**
	 * Spawns a user in any random direction.
	 * @param type
	 * @throws GameActionException
	 */
	public void spawn(int type) throws GameActionException {

		int tries = 0;
		while (tries < 8) {
			Direction dir = Direction.values()[(int)(Math.random() * 8)];
			if (rc.canMove(dir)) {
				rc.spawn(dir);	
				System.out.println("Spawning: " + type);
				com.communicate(Communicator.CHANNEL_NEW_SOLDIER_MODE, type);
				break;
			}
		}
	}
	
	/**
	 * calculates the economics of the board.  This is 
	 * what type of board we will attempt to get.
	 * 
	 * -- NOTE WILL TAKE 2 ROUNDS -- 
	 */
	public void calculateStrategyPoints() {
		info.setEncampmentsAndSort();
		info.setNeutralMines();
		
		//We determine what strategy to use.
		
	}
	
	public static final int HQ_COUNT_ROUND = 3;

	public static final int ECON_ENCAMPMENT_DENOMINATOR = 8;
	public static final int DEFENSIVE_HQ_DIST_DENOMINATOR = 128;
	public static final int TECH_HQ_DIST_DENOMINATOR = 64; 
	public static final int TECH_MINE_RATIO_MUL = 3;
}
