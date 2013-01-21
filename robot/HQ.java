package team122.robot;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team122.MapInformation;
import team122.RobotInformation;
import team122.behavior.hq.HQUtils;
import team122.communication.Communicator;
import team122.trees.HQTree;

public class HQ extends TeamRobot {
	public HQUtils hqUtils;
	public int military;
	public boolean econ;
	public boolean mid;
	public boolean rush;
	public boolean nuke;
	public MapInformation mapInfo;
	
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
			Communicator.CHANNEL_ENCAMPER_COUNT,
			Communicator.CHANNEL_DEFENDER_COUNT
		});
		military = 0;
		econ = false;
		mid = false;
		rush = false;
		nuke = false;
		mapInfo = new MapInformation(rc, info);
	}
	
	@Override
	public void environmentCheck() throws GameActionException {
		//TODO: What's the environment check here?
		
		if (Clock.getRoundNum() % HQ_COUNT_ROUND == 0) {
			hqUtils.counts();
			
			if (Clock.getRoundNum() % (HQ_COUNT_ROUND * 250) == 0) {
				hqUtils.printState();	
			}
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
	public void calculateStrategyPoints() throws GameActionException {
		mapInfo.setEncampmentsAndSort();
		mapInfo.setNeutralMines();
		
		if (info.enemyHqDistance <= RUSH_ENEMY_MAP) {
			rush = true;
			return;
		} else {
			nuke = true;
			return;
		}

		
//		//We determine what strategy to use.
//		int radiusSquared = info.enemyHqDistance / 2;
//		double area = (Math.PI * radiusSquared);
//		int mineCount = rc.senseMineLocations(info.center, radiusSquared, Team.NEUTRAL).length;
//		double density = mineCount / area;
//		
//		System.out.println(info.enemyHqDistance + " : " + density);
//		
//		if (info.enemyHqDistance <= RUSH_ENEMY_MAP_LONG && density <= RUSH_ENEMY_MAP_LONG_DENSITY) {
//			rush = true;
//			return;
//		}
//
//		econ = true;
//		return;
	}
	
	public static final int HQ_COUNT_ROUND = 3;
	public static final int RUSH_ENEMY_MAP = 400;
	public static final int RUSH_ENEMY_MAP_LONG = 1600;
	public static final double RUSH_ENEMY_MAP_LONG_DENSITY = 0.25;
}
