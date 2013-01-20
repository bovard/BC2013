package team122.robot;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Team;
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
		econ = false;
		mid = false;
		rush = false;
	}

	@Override
	public void environmentCheck() throws GameActionException {
		//TODO: What's the environment check here?
		
		if (Clock.getRoundNum() % HQ_COUNT_ROUND == 0) {
			hqUtils.counts();
			
			if (Clock.getRoundNum() % (HQ_COUNT_ROUND * 500) == 0) {
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
		info.setEncampmentsAndSort();
		info.setNeutralMines();
		
		if (info.enemyHqDistance <= RUSH_ENEMY_MAP) {
			rush = true;
			return;
		}

		
		//We determine what strategy to use.
		int radiusSquared = info.enemyHqDistance / 2;
		double area = (Math.PI * radiusSquared);
		int mineCount = rc.senseMineLocations(info.center, radiusSquared, Team.NEUTRAL).length;
		double density = mineCount / area;
		
		System.out.println(info.enemyHqDistance + " : " + density);
		
		if (info.enemyHqDistance <= RUSH_ENEMY_MAP_LONG && density <= RUSH_ENEMY_MAP_LONG_DENSITY) {
			rush = true;
			return;
		}
		
		//Mid is just for sizing.
		if (info.enemyHqDistance < MID_DISTANCE_MAX && info.enemyHqDistance > MID_DISTANCE_MIN) {
			mid = true;
			
			if (density > MID_ECON_DENSITY || info.totalEncampments > MID_ECON_ENCAMPMENTS) {
				econ = true;
			}
			return;
		}
		
		//Mid long
		if (info.enemyHqDistance < MID_LONG_DISTANCE_MAX && info.enemyHqDistance > MID_LONG_DISTANCE_MIN) {
			mid = true;
			
			if (density > MID_LONG_ECON_DENSITY || info.totalEncampments > MID_LONG_ECON_ENCAMPMENTS) {
				econ = true;
			}
			return;
		}

		econ = true;
		return;
	}
	
	public static final int HQ_COUNT_ROUND = 3;
	public static final double MINE_DENSITY_FOR_ECON = 0.33;
	public static final int RUSH_ENEMY_MAP = 400;
	public static final int RUSH_ENEMY_MAP_LONG = 900;
	public static final double RUSH_ENEMY_MAP_LONG_DENSITY = 0.25;
	public static final int MID_DISTANCE_MIN = 900;
	public static final int MID_DISTANCE_MAX = 1600;
	public static final double MID_ECON_DENSITY = 0.35;
	public static final double MID_ECON_ENCAMPMENTS = 40;
	public static final int MID_LONG_DISTANCE_MIN = 1600;
	public static final int MID_LONG_DISTANCE_MAX = 2500;
	public static final double MID_LONG_ECON_DENSITY = 0.2;
	public static final double MID_LONG_ECON_ENCAMPMENTS = 50;
}
