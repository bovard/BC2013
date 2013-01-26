package team122.behavior.hq;

import java.util.Arrays;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Upgrade;
import team122.MapInformation;
import team122.RobotInformation;
import team122.communication.Communicator;
import team122.robot.HQ;
import team122.utils.QuicksortTree;

public class HQUtils {
	
	public Communicator com;
	public RobotController rc;
	public int generatorCount;
	public int artilleryCount;
	public int supplierCount;
	public int soldierCount;
	public int defenderCount;
	public int nukeDefenderCount;
	public int encamperCount;
	public int minerCount;
	public int totalSoldierCount;
	public int totalEncampmentCount;
	public double powerProduction;
	public double powerToCaptureEncampment;
	public double powerConsumptionFromSoldiers;
	public MapInformation mapInfo;
	
	public HQUtils(RobotController rc, Communicator com, MapInformation mapInfo) {
		this.rc = rc;
		this.com = com;
		generatorCount = 0;
		artilleryCount = 0;
		supplierCount = 0;
		soldierCount = 0;
		defenderCount = 0;
		encamperCount = 0;
		minerCount = 0;
		totalSoldierCount = 0;
		totalEncampmentCount = 0;
		powerProduction = 0;
		powerToCaptureEncampment = 0;
		powerConsumptionFromSoldiers = 0;
		nukeDefenderCount = 0;
		this.mapInfo = mapInfo;
	}
	
	/**
	 * The cost of capturing this generator.
	 * @param hq
	 * @param encampment
	 * @return
	 * @throws GameActionException 
	 */
	public int generatorCost(MapLocation hq, MapLocation encampment, MapLocation enemy) throws GameActionException {
		
		double mineDensity = mapInfo.updateMineDensity();
		int hqDist = hq.distanceSquaredTo(encampment);
		double decayRate = rc.hasUpgrade(Upgrade.FUSION) ? GameConstants.POWER_DECAY_RATE_FUSION : GameConstants.POWER_DECAY_RATE;
		int defuseRate = rc.hasUpgrade(Upgrade.DEFUSION) ? GameConstants.MINE_DEFUSE_DEFUSION_DELAY : GameConstants.MINE_DEFUSE_DELAY;
		double roundsUntilPayback = powerToCaptureEncampment / (GameConstants.GENERATOR_POWER_PRODUCTION * decayRate);
		int enemyCost = hqDist - enemy.distanceSquaredTo(encampment);
		double movementCost = hqDist * (1 + (mineDensity * defuseRate));
		boolean inMiddleGrounds = mapInfo.inRangeOfCenterPath(encampment, 81);
		
		//TODO: Power production is to high so we need to have another cost.
		
		return (int)(roundsUntilPayback + enemyCost + movementCost + (inMiddleGrounds ? 20 : -10));
	}
	
	public int supplierCost(MapLocation hq, MapLocation encampment, MapLocation enemy) throws GameActionException {

		//TODO:  THIS IS BAD CALCULATIONS ONLY CONSIDERED MOVEMENT.
		
		double mineDensity = mapInfo.updateMineDensity();
		int hqDist = hq.distanceSquaredTo(encampment);
		double decayRate = rc.hasUpgrade(Upgrade.FUSION) ? GameConstants.POWER_DECAY_RATE_FUSION : GameConstants.POWER_DECAY_RATE;
		int defuseRate = rc.hasUpgrade(Upgrade.DEFUSION) ? GameConstants.MINE_DEFUSE_DEFUSION_DELAY : GameConstants.MINE_DEFUSE_DELAY;
		double movementCost = hqDist * (1 + (mineDensity * defuseRate));
		return (int)(0 - artilleryCount + movementCost);
	}
	
	public int artilleryCost(MapLocation hq, MapLocation encampment, MapLocation enemy)  throws GameActionException {

		//TODO:  THIS IS BAD CALCULATIONS ONLY CONSIDERED MOVEMENT.
		
		double mineDensity = mapInfo.updateMineDensity();
		int hqDist = hq.distanceSquaredTo(encampment);
		double decayRate = rc.hasUpgrade(Upgrade.FUSION) ? GameConstants.POWER_DECAY_RATE_FUSION : GameConstants.POWER_DECAY_RATE;
		int defuseRate = rc.hasUpgrade(Upgrade.DEFUSION) ? GameConstants.MINE_DEFUSE_DEFUSION_DELAY : GameConstants.MINE_DEFUSE_DELAY;
		double movementCost = hqDist * (1 + (mineDensity * defuseRate));
		return (int)(1 - supplierCount);
	}

	/**
	 * Gets the different counts available then zeroing them out.
	 * @throws GameActionException
	 */
	public void counts() throws GameActionException {
		//Stores this rounds counts
		generatorCount = com.receive(Communicator.CHANNEL_GENERATOR_COUNT, 0);
		artilleryCount = com.receive(Communicator.CHANNEL_ARTILLERY_COUNT, 0);
		supplierCount = com.receive(Communicator.CHANNEL_SUPPLIER_COUNT, 0);
		soldierCount = com.receive(Communicator.CHANNEL_SOLDIER_COUNT, 0);
		encamperCount = com.receive(Communicator.CHANNEL_ENCAMPER_COUNT, 0);
		minerCount = com.receive(Communicator.CHANNEL_MINER_COUNT, 0);
		defenderCount = com.receive(Communicator.CHANNEL_DEFENDER_COUNT, 0);
		nukeDefenderCount = com.receive(Communicator.CHANNEL_NUKE_COUNT, 0);
		
		int round = Clock.getRoundNum();

		//Erases so counts will be accurate.
		com.communicate(Communicator.CHANNEL_GENERATOR_COUNT, round, 0);
		com.communicate(Communicator.CHANNEL_ARTILLERY_COUNT, round, 0);
		com.communicate(Communicator.CHANNEL_SUPPLIER_COUNT, round, 0);
		com.communicate(Communicator.CHANNEL_SOLDIER_COUNT, round, 0);
		com.communicate(Communicator.CHANNEL_MINER_COUNT, round, 0);
		com.communicate(Communicator.CHANNEL_ENCAMPER_COUNT, round, 0);
		com.communicate(Communicator.CHANNEL_NUKE_COUNT, round, 0);
		
		// Basic calculations that are needed by the HQ.
		totalSoldierCount = soldierCount + encamperCount + minerCount + defenderCount + nukeDefenderCount;
		totalEncampmentCount = generatorCount + supplierCount + artilleryCount;
		
		//Power production is correct but powerToCapture is incorrect.  Its overestimated.
		powerProduction = generatorCount * GameConstants.GENERATOR_POWER_PRODUCTION + GameConstants.HQ_POWER_PRODUCTION;
		powerToCaptureEncampment = GameConstants.CAPTURE_POWER_COST * (1 + totalEncampmentCount);
		powerConsumptionFromSoldiers = GameConstants.UNIT_POWER_UPKEEP * (totalSoldierCount + totalEncampmentCount);
	}

	public static final void calculate(HQ robot) {
		
		if (!robot.encampmentSorter.finishBaseCalculation) {
			robot.encampmentSorter.calculate();
		} else if (!robot.encampmentSorter.generatorTree.done || !robot.encampmentSorter.artilleryTree.done) {
			
			if (!robot.encampmentSorter.generatorTree.done) {
				robot.encampmentSorter.generatorTree.sort();
			}
			
			if (!robot.encampmentSorter.artilleryTree.done) {
				robot.encampmentSorter.artilleryTree.sort();
			}
		} else {
			
		}
	}
	
}
