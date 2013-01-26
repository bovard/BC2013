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
	public int minerCount;
	public int backdoorCount;
	public int encampmentHunterCount;
	public int totalSoldierCount;
	public int totalEncampmentCount;
	public double powerProduction;
	public double powerToCaptureEncampment;
	public double powerConsumptionFromSoldiers;
	public MapInformation mapInfo;
	
	public HQUtils(RobotController rc, Communicator com, MapInformation mapInfo) {
		this.rc = rc;
		this.com = com;
		backdoorCount = 0;
		encampmentHunterCount = 0;
		generatorCount = 0;
		artilleryCount = 0;
		supplierCount = 0;
		soldierCount = 0;
		defenderCount = 0;
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
	 * Gets the different counts available then zeroing them out.
	 * @throws GameActionException
	 */
	public void counts() throws GameActionException {
		//Stores this rounds counts
		generatorCount = com.receive(Communicator.CHANNEL_GENERATOR_COUNT, Clock.getRoundNum(), 0);
		artilleryCount = com.receive(Communicator.CHANNEL_ARTILLERY_COUNT,  Clock.getRoundNum(), 0);
		supplierCount = com.receive(Communicator.CHANNEL_SUPPLIER_COUNT,  Clock.getRoundNum(), 0);
		soldierCount = com.receive(Communicator.CHANNEL_SOLDIER_COUNT,  Clock.getRoundNum(), 0);
		minerCount = com.receive(Communicator.CHANNEL_MINER_COUNT,  Clock.getRoundNum(), 0);
		defenderCount = com.receive(Communicator.CHANNEL_DEFENDER_COUNT,  Clock.getRoundNum(), 0);
		nukeDefenderCount = com.receive(Communicator.CHANNEL_NUKE_COUNT,  Clock.getRoundNum(), 0);
		backdoorCount = com.receive(Communicator.CHANNEL_BACKDOOR_COUNT,  Clock.getRoundNum(), 0);
		encampmentHunterCount = com.receive(Communicator.CHANNEL_ENCAMPER_HUNTER_COUNT,  Clock.getRoundNum(), 0);
		
		// Basic calculations that are needed by the HQ.
		totalSoldierCount = soldierCount + minerCount + defenderCount + nukeDefenderCount + backdoorCount + encampmentHunterCount;
		totalEncampmentCount = generatorCount + supplierCount + artilleryCount;
		
		//Power production is correct but powerToCapture is incorrect.  Its overestimated.
		powerProduction = generatorCount * GameConstants.GENERATOR_POWER_PRODUCTION + GameConstants.HQ_POWER_PRODUCTION;
		powerToCaptureEncampment = GameConstants.CAPTURE_POWER_COST * (1 + totalEncampmentCount);
		powerConsumptionFromSoldiers = GameConstants.UNIT_POWER_UPKEEP * (totalSoldierCount + totalEncampmentCount);
	}

	public static final void calculate(HQ robot) {
		
		//We want to calculate first
		if (!robot.encampmentSorter.calculated) {
			robot.encampmentSorter.calculate();
		} else if (!robot.encampmentSorter.sorted) {
			robot.encampmentSorter.sort();
		} else {
			//TODO: I DO NT KNOW!
			
		}
		
		//sort second.
//		if (robot.encampmentSorter.calculated && !robot.encampmentSorter.sorted) {
//			robot.encampmentSorter.sort();
//		}
		
	}
	
}
