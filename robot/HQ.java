package team122.robot;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import team122.RobotInformation;
import team122.behavior.hq.HQState;
import team122.behavior.hq.HQUtils;
import team122.behavior.soldier.SoldierEncamper;
import team122.behavior.soldier.SoldierSelector;
import team122.communication.Communicator;
import team122.communication.SoldierDecoder;
import team122.trees.HQTree;
import team122.utils.DoNotCapture;
import team122.utils.EncampmentSorter;
import team122.utils.GreedyEncampment;

public class HQ extends TeamRobot {
	public HQUtils hqUtils;
	public HQState state;
	public boolean rush;
	public boolean forceNukeRush;
	public EncampmentSorter encampmentSorter;
	public DoNotCapture doNotCapture;
	public boolean enemyResearchedNuke;
	public int nukeCount;
	public boolean retaliate;
	public boolean winning;
	public int enemyHPChangedRound;
	public boolean enemyAtBase;
	
	
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
		doNotCapture = new DoNotCapture(rc, info);
		state = new HQState();
	}
	
	@Override
	public void environmentCheck() throws GameActionException {
		//TODO: What's the environment check here?
		_checkForNuke();
		
		if (rc.canSenseSquare(info.enemyHq)) {
			Robot[] r = rc.senseNearbyGameObjects(Robot.class, info.enemyHq, 1, info.enemyTeam);
			
			if (r.length == 1) {
				RobotInfo rInfo = rc.senseRobotInfo(r[0]);
				double energon = rc.getEnergon();
				
				if (rInfo.energon < energon) {
					winning = true;
					enemyHPChangedRound = Clock.getRoundNum();
				}
			}
		}
		enemyAtBase = false;
		if (rc.senseNearbyGameObjects(Robot.class, 400, info.enemyTeam).length > 0) {
			enemyAtBase = true;
		}

		//is the next round an inc round.
		if ((Clock.getRoundNum() + 1) % HQ_COMMUNICATION_ROUND == 0) {

			com.clear(Clock.getRoundNum() + 1);
		} else if (Clock.getRoundNum() % HQ_COMMUNICATION_ROUND == 0) {
			
			hqUtils.counts();
			
			//COmmunicates nuke is armed upon each com round.
			if (enemyResearchedNuke) {
				com.nukeIsArmed();
			}
		}

	}
	
	/**
	 * sends the com attack signal.
	 * @throws GameActionException 
	 */
	public void attack() throws GameActionException {
		com.attack();
	}
	
	/**
	 * Loads the rest of the tree with bytecodes.
	 * @throws GameActionException 
	 */
	@Override
	public void load() throws GameActionException {
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
	 * Spawns a scout.
	 * @return
	 */
	public void spawnScout() throws GameActionException {
		_spawn(new SoldierDecoder(SoldierSelector.SOLDIER_SCOUT, 0));
	}

	/**
	 * Spawns a defensive miner.
	 * @return
	 */
	public void spawnMiner() throws GameActionException {
		_spawn(new SoldierDecoder(SoldierSelector.SOLDIER_MINER, 0));
	}

	/**
	 * Spawns a backdoor soldier.
	 * @return
	 */
	public void spawnBackdoor() throws GameActionException {
		_spawn(new SoldierDecoder(SoldierSelector.SOLDIER_BACK_DOOR, 0));
	}

	/**
	 * Spawns a encampment hunter.
	 * @return
	 */
	public void spawnEncampmentHunter(int group) throws GameActionException {
		_spawn(new SoldierDecoder(SoldierSelector.SOLDIER_ENCAMP_HUNTER, group));
	}
	
	/**
	 * Spawns the jackal.
	 * @return
	 */
	public void spawnTheJackal() throws GameActionException {
		_spawn(new SoldierDecoder(SoldierSelector.SOLDIER_THE_JACKAL, 0));
	}
	
	/**
	 * Spawns a econ building if possible (generator or supplier)
	 * Will spawn a generator if the number of generators + 3 is less than the supplier count
	 * @return
	 * @throws GameActionException
	 */
	public boolean spawnEconBuilding() throws GameActionException {
		if (hqUtils.generatorCount + 3 < hqUtils.supplierCount) {
			return spawnGenerator();
		} else {
			return spawnSupplier();
		}
	}
	
	/**
	 * Spawns a generator soldier if there are generator spots left, else it returns false if no 
	 * encamper has been spawned.
	 * @return
	 */
	public boolean spawnGenerator() throws GameActionException {
		if (!doNotCapture.determined) {
			return false;
		}
		if (encampmentSorter.generatorSorted) {
			MapLocation loc = encampmentSorter.popGenerator();
			
			if (loc != null) {
				_spawn(new SoldierDecoder(SoldierEncamper.GENERATOR_ENCAMPER, loc));		
				return true;
			}
		} else {
			
			//Only Works once, the next one will be the same spot.
			//TODO: Create an offset?
			MapLocation loc = GreedyEncampment.GetGreedyGenerator(rc, info.hq, info.enemyHq, doNotCapture.determinedMapLocations);
			
			if (loc != null) {
				_spawn(new SoldierDecoder(SoldierEncamper.GENERATOR_ENCAMPER, loc));		
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Performs a greedy supplier request.
	 * @return
	 * @throws GameActionException
	 */
	public boolean greedySupplier() throws GameActionException {
		if (!doNotCapture.determined) {
			return false;
		}
		
		//Only Works once, the next one will be the same spot.
		//TODO: Create an offset?
		MapLocation loc = GreedyEncampment.GetGreedyGenerator(rc, info.hq, info.enemyHq, doNotCapture.determinedMapLocations);
		
		if (loc != null) {
			_spawn(new SoldierDecoder(SoldierEncamper.SUPPLIER_ENCAMPER, loc));		
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
		if (!doNotCapture.determined) {
			return false;
		}
		if (encampmentSorter.generatorSorted) {
			MapLocation loc = encampmentSorter.popGenerator();
			
			if (loc != null) {
				_spawn(new SoldierDecoder(SoldierEncamper.SUPPLIER_ENCAMPER, loc));		
				return true;
			}
		} else {
			
			//Only Works once, the next one will be the same spot.
			//TODO: Create an offset?
			MapLocation loc = GreedyEncampment.GetGreedyGenerator(rc, info.hq, info.enemyHq, doNotCapture.determinedMapLocations);
			
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
		if (!doNotCapture.determined) {
			return false;
		}
		if (encampmentSorter.artillerySorted) {
			MapLocation loc = encampmentSorter.popArtillery();
			
			if (loc!= null) {
				_spawn(new SoldierDecoder(SoldierEncamper.ARTILLERY_ENCAMPER, loc));		
				return true;
			}
		} else {
			
			//Only Works once, the next one will be the same spot.
			//TODO: Create an offset?
			MapLocation loc = GreedyEncampment.GetGreedyArtillery(rc, info.hq, info.enemyHq, doNotCapture.determinedMapLocations);
			
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