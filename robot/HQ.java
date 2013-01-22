package team122.robot;

import java.util.Arrays;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team122.EncampmentSorter;
import team122.MapInformation;
import team122.RobotInformation;
import team122.behavior.hq.HQUtils;
import team122.behavior.soldier.SoldierEncamper;
import team122.behavior.soldier.SoldierSelector;
import team122.communication.CommunicationDecoder;
import team122.communication.Communicator;
import team122.trees.HQTree;

public class HQ extends TeamRobot {
	public HQUtils hqUtils;
	public int military;
	public boolean econ;
	public boolean rush;
	public boolean nuke;
	public boolean darkHorse;
	public EncampmentSorter encampmentSorter;
	public MapInformation mapInfo;
	public int currentGenSupSpot;
	public int currentArtillerySpot;
	public boolean hasMoreArtillerySpots;
	public boolean hasMoreGenSpots;
	public boolean enemyResearchedNuke;
	
	public HQ(RobotController rc, RobotInformation info) {
		super(rc, info);
		hqUtils = new HQUtils(rc, com, mapInfo);
		tree = new HQTree(this);
		com.seedChannels(5, new int[] {
			Communicator.CHANNEL_NEW_SOLDIER_MODE, 
			Communicator.CHANNEL_GENERATOR_COUNT, 
			Communicator.CHANNEL_SUPPLIER_COUNT, 
			Communicator.CHANNEL_ARTILLERY_COUNT, 
			Communicator.CHANNEL_SOLDIER_COUNT, 
			Communicator.CHANNEL_MINER_COUNT, 
			Communicator.CHANNEL_ENCAMPER_COUNT,
			Communicator.CHANNEL_DEFENDER_COUNT,
			Communicator.CHANNEL_NUKE_COUNT,
			Communicator.CHANNEL_ENCAMPER_LOCATION,
		});
		military = 0;
		econ = false;
		rush = false;
		nuke = false;
		darkHorse = false;
		enemyResearchedNuke = false;
		mapInfo = new MapInformation(rc);
		encampmentSorter = new EncampmentSorter(rc);
		currentGenSupSpot = 0;
		currentArtillerySpot = 0;
	}
	
	@Override
	public void environmentCheck() throws GameActionException {
		//TODO: What's the environment check here?
		
		if (Clock.getRoundNum() % HQ_COUNT_ROUND == 0) {
			hqUtils.counts();
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
	 * 
	 * @param type
	 * @param dec
	 */
	public void spawn(int type, CommunicationDecoder dec, int decChannel) throws GameActionException {

		if (rc.isActive()) {
			int tries = 0;
			while (tries < 8) {
				Direction dir = Direction.values()[(int)(Math.random() * 8)];
				if (rc.canMove(dir)) {
					com.communicate(Communicator.CHANNEL_NEW_SOLDIER_MODE, type);
					com.communicateWithPosition(dec, decChannel);
					rc.spawn(dir);
					break;
				}
			}
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
		} else {
			nuke = true;
		}

		encampmentSorter.getEncampments();
		if (encampmentSorter.isDarkHorse(5)) {
			rush = false;
			nuke = false;
			darkHorse = true;
		} else {

			if (rc.isActive()) {
				spawn(SoldierSelector.SOLDIER_MINER);
			}
			
			encampmentSorter.calculate();
		}
		
		currentGenSupSpot = currentArtillerySpot = 0;
	}
	
	/**
	 * Spawns a generator soldier if there are generator spots left, else it returns false if no 
	 * encamper has been spawned.
	 * @return
	 */
	public boolean spawnGenerator() throws GameActionException {
		MapLocation loc = encampmentSorter.popGenerator();
		
		if (loc != null) {
			CommunicationDecoder decoder = new CommunicationDecoder(loc, SoldierEncamper.GENERATOR_ENCAMPER);
			spawn(SoldierSelector.SOLDIER_ENCAMPER, decoder, Communicator.CHANNEL_ENCAMPER_LOCATION);		
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
			CommunicationDecoder decoder = new CommunicationDecoder(loc, SoldierEncamper.SUPPLIER_ENCAMPER);
			spawn(SoldierSelector.SOLDIER_ENCAMPER, decoder, Communicator.CHANNEL_ENCAMPER_LOCATION);		
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
			CommunicationDecoder decoder = new CommunicationDecoder(loc, SoldierEncamper.ARTILLERY_ENCAMPER);
			spawn(SoldierSelector.SOLDIER_ENCAMPER, decoder, Communicator.CHANNEL_ENCAMPER_LOCATION);		
			return true;
		}
		return false;
	}


	/**
	 * Spawns a artillery soldier if there are artillery spots left, else it returns false if no 
	 * encamper has been spawned.
	 * @return
	 */
	public boolean spawnDarkHorse() throws GameActionException {
		MapLocation loc = encampmentSorter.popDarkHorse();
		if (loc!= null) {
			CommunicationDecoder decoder = new CommunicationDecoder(loc, SoldierEncamper.ARTILLERY_ENCAMPER);
			spawn(SoldierSelector.SOLDIER_ENCAMPER, decoder, Communicator.CHANNEL_ENCAMPER_LOCATION);		
			return true;
		}
		return false;
	}
	
	public static final int HQ_COUNT_ROUND = 3;
	public static final int RUSH_ENEMY_MAP = 1000;
	public static final int RUSH_ENEMY_MAP_LONG = 1600;
	public static final double RUSH_ENEMY_MAP_LONG_DENSITY = 0.25;
}