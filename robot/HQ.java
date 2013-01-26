package team122.robot;

import java.util.Arrays;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team122.MapInformation;
import team122.RobotInformation;
import team122.behavior.hq.HQUtils;
import team122.behavior.soldier.SoldierEncamper;
import team122.behavior.soldier.SoldierSelector;
import team122.communication.CommunicationDecoder;
import team122.communication.Communicator;
import team122.communication.SoldierDecoder;
import team122.trees.HQTree;
import team122.utils.EncampmentSorter;

public class HQ extends TeamRobot {
	public HQUtils hqUtils;
	public boolean rush;
	public boolean darkHorse;
	public boolean vsNukeBot;
	public boolean vsNukeBotAndMiner;
	public boolean vsNukeBotAndMinerPickax;
	public boolean forceNukeRush;
	public EncampmentSorter encampmentSorter;
	public MapInformation mapInfo;
	public boolean enemyResearchedNuke;
	public int nukeCount;
	private boolean threeTurnsAgoPositive = true;
	private boolean twoTurnsAgoPositive = true;
	private boolean oneTurnAgoPositive = true;
	private double powerLastRound = 0;
	public double powerThisRound = 0;
	public boolean powerPositive = true;
	
	
	public HQ(RobotController rc, RobotInformation info) {
		super(rc, info);
		hqUtils = new HQUtils(rc, com, mapInfo);
		tree = new HQTree(this);
		nukeCount = 0;
		rush = false;
		darkHorse = false;
		vsNukeBot = false;
		vsNukeBotAndMiner = false;
		vsNukeBotAndMinerPickax = false;
		enemyResearchedNuke = false;
		forceNukeRush = false;
		mapInfo = new MapInformation(rc);
		encampmentSorter = new EncampmentSorter(rc);
	}
	
	@Override
	public void environmentCheck() throws GameActionException {
		//TODO: What's the environment check here?
		_checkForNuke();
		
		// check to see if we have positive energy growth
		powerThisRound = rc.getTeamPower();
		threeTurnsAgoPositive = twoTurnsAgoPositive;
		twoTurnsAgoPositive = oneTurnAgoPositive;
		oneTurnAgoPositive = powerThisRound > powerLastRound;
		powerPositive =  (threeTurnsAgoPositive && twoTurnsAgoPositive) || 
						 (twoTurnsAgoPositive && oneTurnAgoPositive) || 
						 (threeTurnsAgoPositive && oneTurnAgoPositive);
		
		if ((Clock.getRoundNum() + 1) % HQ_COUNT_ROUND == 0) {

			com.communicate(Communicator.CHANNEL_GENERATOR_COUNT, Clock.getRoundNum() + 1, 0);
			com.communicate(Communicator.CHANNEL_ARTILLERY_COUNT, Clock.getRoundNum() + 1, 0);
			com.communicate(Communicator.CHANNEL_SUPPLIER_COUNT, Clock.getRoundNum() + 1, 0);
			com.communicate(Communicator.CHANNEL_SOLDIER_COUNT, Clock.getRoundNum() + 1, 0);
			com.communicate(Communicator.CHANNEL_MINER_COUNT, Clock.getRoundNum() + 1, 0);
			com.communicate(Communicator.CHANNEL_ENCAMPER_COUNT, Clock.getRoundNum() + 1, 0);
			com.communicate(Communicator.CHANNEL_NUKE_COUNT, Clock.getRoundNum() + 1, 0);
		} else if (Clock.getRoundNum() % HQ_COUNT_ROUND == 0) {
			hqUtils.counts();
		}
	}
	
	/**
	 * calculates the economics of the board.  This is 
	 * what type of board we will attempt to get.
	 * 
	 * -- NOTE WILL TAKE 2 ROUNDS -- 
	 */
	public void calculateEncamperSpots() throws GameActionException {
		
		if (info.enemyHqDistance <= RUSH_ENEMY_MAP) {
			rush = true;
		}

		encampmentSorter.getEncampments();
		encampmentSorter.calculate();
	}

	/**
	 * Spawns a swarmer.
	 * @return
	 */
	public void spawnSwarmer() throws GameActionException {
		_spawn(new SoldierDecoder(SoldierSelector.SOLDIER_SWARMER, 1));
	}

	/**
	 * Spawns a swarmer.
	 * @return
	 */
	public void spawnMiner() throws GameActionException {
		_spawn(new SoldierDecoder(SoldierSelector.SOLDIER_MINER, 1));
	}
	
	/**
	 * Spawns a generator soldier if there are generator spots left, else it returns false if no 
	 * encamper has been spawned.
	 * @return
	 */
	public boolean spawnGenerator() throws GameActionException {
		MapLocation loc = encampmentSorter.popGenerator();
		
		if (loc != null) {
			_spawn(new SoldierDecoder(SoldierEncamper.GENERATOR_ENCAMPER, loc));		
			return true;
		}
		return false;
	}
	
	/**
	 * Spawns a supplier soldier if there are supplier spots left, else it returns false if no 
	 * encamper has been spawned.
	 * @return
	 */
	public boolean spawnSupplier() throws GameActionException {
		MapLocation loc = encampmentSorter.popGenerator();

		if (loc != null) {
			_spawn(new SoldierDecoder(SoldierEncamper.SUPPLIER_ENCAMPER, loc));		
			return true;
		}
		return false;
	}

	/**
	 * Spawns a artillery soldier if there are artillery spots left, else it returns false if no 
	 * encamper has been spawned.
	 * @return
	 */
	public boolean spawnArtillery() throws GameActionException {
		MapLocation loc = encampmentSorter.popArtillery();
		if (loc!= null) {
			_spawn(new SoldierDecoder(SoldierEncamper.ARTILLERY_ENCAMPER, loc));		
			return true;
		}
		return false;
	}


	/**
	 * Spawns a user in any random direction.
	 * @param type
	 * @throws GameActionException
	 */
	private void _spawn(SoldierDecoder dec) throws GameActionException {

		int tries = 0;
		while (tries < 8) {
			Direction dir = Direction.values()[(int)(Math.random() * 8)];
			
			if (rc.canMove(dir)) {
				rc.spawn(dir);
				com.communicate(dec, Communicator.CHANNEL_NEW_SOLDIER_MODE, Clock.getRoundNum());
				break;
			}
		}
	}

	
	/**
	 * Checks for nuke and performs an upgrade on state if there is a rush nuke.
	 * @throws GameActionException
	 */
	private void _checkForNuke() throws GameActionException {
		if (!enemyResearchedNuke && !forceNukeRush) {
			
			enemyResearchedNuke = rc.senseEnemyNukeHalfDone();
			
			if (enemyResearchedNuke) {

				//The minimum amount of nuke space we need between our enemy.
				//If we beat them, then beat them
				if (nukeCount > 202) {
					forceNukeRush = true;
					return;
				}
				
				//If the round is 200 - 205 then the user is a pure nuke bot.  Send enemies one at a time.
				int round = Clock.getRoundNum();
				
				if (round < 205) {
					vsNukeBot = true;
					rush = false;
					vsNukeBotAndMiner = false;
					vsNukeBotAndMinerPickax = false;
					darkHorse = false;
					
				//Either he has 1 miner and a nuke or he has a single artillery and a nuke
				//Do not send 1 at a time.
				} else if (round < 215) {

					vsNukeBotAndMiner = true;
					vsNukeBot = false;
					rush = false;
					vsNukeBotAndMinerPickax = false;
					darkHorse = false;
					
				//More than likely 4 miners, 1 miner + pickax, 
				} else if (round < 240) {

					vsNukeBotAndMinerPickax = true;
					vsNukeBotAndMiner = false;
					vsNukeBot = false;
					rush = false;
					darkHorse = false;
				}
			}
		}
	} // end check for nuke
	
	public static final int HQ_COUNT_ROUND = 3;
	public static final int RUSH_ENEMY_MAP = 1000;
	public static final int RUSH_ENEMY_MAP_LONG = 1600;
	public static final double RUSH_ENEMY_MAP_LONG_DENSITY = 0.25;
}