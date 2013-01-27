package team122.robot;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team122.RobotInformation;
import team122.behavior.hq.HQUtils;
import team122.behavior.soldier.SoldierEncamper;
import team122.behavior.soldier.SoldierSelector;
import team122.communication.Communicator;
import team122.communication.SoldierDecoder;
import team122.trees.HQTree;
import team122.utils.EncampmentSorter;
import team122.utils.GreedyEncampment;

public class HQ extends TeamRobot {
	public HQUtils hqUtils;
	public boolean rush;
	public boolean forceNukeRush;
	public EncampmentSorter encampmentSorter;
	public boolean enemyResearchedNuke;
	public int nukeCount;
	private boolean threeTurnsAgoPositive = true;
	private boolean twoTurnsAgoPositive = true;
	private boolean oneTurnAgoPositive = true;
	private double powerLastRound = 0;
	public double powerThisRound = 0;
	public boolean powerPositive = true;
	public boolean retaliate;
	
	
	public HQ(RobotController rc, RobotInformation info) {
		super(rc, info);
		hqUtils = new HQUtils(rc, com);
		tree = new HQTree(this);
		nukeCount = 0;
		rush = false;
		retaliate = false;
		enemyResearchedNuke = false;
		forceNukeRush = false;
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
		
		//is the next round an inc round.
		if ((Clock.getRoundNum() + 1) % HQ_COMMUNICATION_ROUND == 0) {

			com.clear(Clock.getRoundNum() + 1);
		} else if (Clock.getRoundNum() % HQ_COMMUNICATION_ROUND == 0) {
			
			hqUtils.counts();
			
			//COmmunicates nuke is armed upon each com round.
			if (enemyResearchedNuke) {
				com.nukeIsArmed();
			}

			//TODO: Get retaliation detection.
			//TODO: Determine when to attack?
			if (Clock.getRoundNum() % 201 == 0) {
				com.attack();
			}
		}

	}
	
	/**
	 * Loads the rest of the tree with bytecodes.
	 */
	@Override
	public void load() {
		HQUtils.calculate(this);
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
	}

	/**
	 * Spawns a swarmer.
	 * @return
	 */
	public void spawnSwarmer() throws GameActionException {
		_spawn(new SoldierDecoder(SoldierSelector.SOLDIER_SWARMER, 0));
	}

	/**
	 * Spawns a swarmer.
	 * @return
	 */
	public void spawnMiner() throws GameActionException {
		_spawn(new SoldierDecoder(SoldierSelector.SOLDIER_MINER, 0));
	}

	/**
	 * Spawns a swarmer.
	 * @return
	 */
	public void spawnBackdoor() throws GameActionException {
		_spawn(new SoldierDecoder(SoldierSelector.SOLDIER_BACK_DOOR, 0));
	}

	/**
	 * Spawns a swarmer.
	 * @return
	 */
	public void spawnEncampmentHunter(int group) throws GameActionException {
		_spawn(new SoldierDecoder(SoldierSelector.SOLDIER_ENCAMP_HUNTER, group));
	}
	
	/**
	 * Spawns a generator soldier if there are generator spots left, else it returns false if no 
	 * encamper has been spawned.
	 * @return
	 */
	public boolean spawnGenerator() throws GameActionException {
		if (encampmentSorter.generatorSorted) {
			MapLocation loc = encampmentSorter.popGenerator();
			
			if (loc != null) {
				_spawn(new SoldierDecoder(SoldierEncamper.GENERATOR_ENCAMPER, loc));		
				return true;
			}
		} else {
			
			//Only Works once, the next one will be the same spot.
			//TODO: Create an offset?
			MapLocation loc = GreedyEncampment.GetGreedyGenerator(rc, info.hq, info.enemyHq);
			
			if (loc != null) {
				_spawn(new SoldierDecoder(SoldierEncamper.GENERATOR_ENCAMPER, loc));		
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Spawns a supplier soldier if there are supplier spots left, else it returns false if no 
	 * encamper has been spawned.
	 * @return
	 */
	public boolean spawnSupplier() throws GameActionException {

		if (encampmentSorter.generatorSorted) {
			MapLocation loc = encampmentSorter.popGenerator();
			
			if (loc != null) {
				_spawn(new SoldierDecoder(SoldierEncamper.SUPPLIER_ENCAMPER, loc));		
				return true;
			}
		} else {
			
			//Only Works once, the next one will be the same spot.
			//TODO: Create an offset?
			MapLocation loc = GreedyEncampment.GetGreedyGenerator(rc, info.hq, info.enemyHq);
			
			if (loc != null) {
				_spawn(new SoldierDecoder(SoldierEncamper.SUPPLIER_ENCAMPER, loc));		
				return true;
			}
		}
		return false;
	}

	/**
	 * Spawns a artillery soldier if there are artillery spots left, else it returns false if no 
	 * encamper has been spawned.
	 * @return
	 */
	public boolean spawnArtillery() throws GameActionException {

		if (encampmentSorter.artillerySorted) {
			MapLocation loc = encampmentSorter.popArtillery();
			
			if (loc!= null) {
				_spawn(new SoldierDecoder(SoldierEncamper.ARTILLERY_ENCAMPER, loc));		
				return true;
			}
		} else {
			
			//Only Works once, the next one will be the same spot.
			//TODO: Create an offset?
			MapLocation loc = GreedyEncampment.GetGreedyArtillery(rc, info.hq, info.enemyHq);
			
			if (loc != null) {
				_spawn(new SoldierDecoder(SoldierEncamper.ARTILLERY_ENCAMPER, loc));		
				return true;
			}
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
				
				//TODO:  NUKE FIX
				if (nukeCount > 202) {
					forceNukeRush = true;
					return;
				}
			}
		}
	} // end check for nuke
	
	public static final int HQ_COMMUNICATION_ROUND = 3;
	public static final int RUSH_ENEMY_MAP = 1000;
	public static final int RUSH_ENEMY_MAP_LONG = 1600;
	public static final double RUSH_ENEMY_MAP_LONG_DENSITY = 0.25;
	public static final int NUKE_IS_ARMED = 1943650283;
	public static final int RETALIATE     = 1385619238;
	public static final int ATTACK_CODE   = 1726135932;
	public static final int MINIMUM_BYTECODES_LEFT = 750;
}