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
import team122.communication.CommunicationDecoder;
import team122.communication.Communicator;
import team122.trees.HQTree;

public class HQ extends TeamRobot {
	public HQUtils hqUtils;
	public EncampmentSorter encampmentSorter;
	public MapInformation mapInfo;
	public int currentGenSupSpot;
	public int currentArtillerySpot;
	public boolean hasMoreArtillerySpots;
	public boolean hasMoreGenSpots;
	
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
			Communicator.CHANNEL_ENCAMPER_LOCATION,
		});
		encampmentSorter = new EncampmentSorter(rc, info);
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
	
	/**
	 * calculates the economics of the board.  This is 
	 * what type of board we will attempt to get.
	 * 
	 * -- NOTE WILL TAKE 2 ROUNDS -- 
	 */
	public void calculateEncamperSpots() throws GameActionException {
		
		//TODO: Make this smarter.
		encampmentSorter.setEncampments();
		
		//Basic strategy
		System.out.println("Encampers: " + encampmentSorter.totalEncampments);
		System.out.println("Clock: " + Clock.getBytecodeNum() + " : " + Clock.getRoundNum());
		if (encampmentSorter.totalEncampments <= 30) {

			System.out.println("Small Sort");
			encampmentSorter.setEncampmentsGenSort();
			encampmentSorter.setEncampmentsArtillerySort();
			encampmentSorter.intersectArtilleryWithEncampments();
		} else if (encampmentSorter.totalEncampments <= 50) {
			
			//Big map startegy.
			System.out.println("Medium Sort");
			encampmentSorter.setEncampmentsAndSort();
			encampmentSorter.setEncampmentsNearbyArtillery();
			encampmentSorter.intersectArtilleryWithEncampments();
			
		//Worst case senario.
		} else {
			
			//No strat
			System.out.println("Big Sort");
			encampmentSorter.setEncampmentsAndSort();
			encampmentSorter.setEncampmentsBasicArtillery();
		}
		
		if (encampmentSorter.totalArtillerySpots > 0) {
			hasMoreArtillerySpots = true;
		}
		if (encampmentSorter.totalEncampments > 0) {
			hasMoreGenSpots = true;
		}
		encampmentSorter.removeBlockerEncamps();
		
		System.out.println("Clock: " + Clock.getBytecodeNum() + " : " + Clock.getRoundNum());
		System.out.println("ArtillerY: " + Arrays.toString(encampmentSorter.artilleryEncamp));
		System.out.println("Gen: " + Arrays.toString(encampmentSorter.encampments));
		currentGenSupSpot = 0;
	}
	
	public MapLocation peekGeneratorEncampment() {
		if (currentGenSupSpot < encampmentSorter.totalEncampments) {
			while (currentGenSupSpot < encampmentSorter.totalEncampments && encampmentSorter.encampments[currentGenSupSpot] == null) {
				currentGenSupSpot++;
			}
			return encampmentSorter.encampments[currentGenSupSpot];
		}
		hasMoreGenSpots = false;
		return null;
	}
	
	public MapLocation popGeneratorEncampment() {
		if (currentGenSupSpot < encampmentSorter.totalEncampments) {
			return encampmentSorter.encampments[currentGenSupSpot++];
		}
		hasMoreGenSpots = false;
		return null;
	}
	
	public MapLocation peekArtilleryEncampment() {
		if (currentArtillerySpot < encampmentSorter.totalArtillerySpots) {
			while (currentArtillerySpot < encampmentSorter.totalArtillerySpots && encampmentSorter.artilleryEncamp[currentArtillerySpot] == null) {
				currentArtillerySpot++;
			}
			return encampmentSorter.artilleryEncamp[currentArtillerySpot];
		}
		hasMoreArtillerySpots = false;
		return null;
	}
	
	public MapLocation popArtilleryEncampment() {
		if (currentArtillerySpot < encampmentSorter.totalArtillerySpots) {
			return encampmentSorter.artilleryEncamp[currentArtillerySpot++];
		}
		hasMoreArtillerySpots = false;
		return null;
	}
	
	public static final int HQ_COUNT_ROUND = 3;
	public static final int RUSH_ENEMY_MAP = 400;
	public static final int RUSH_ENEMY_MAP_LONG = 1600;
	public static final double RUSH_ENEMY_MAP_LONG_DENSITY = 0.25;
}