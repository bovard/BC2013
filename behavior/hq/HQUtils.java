package team122.behavior.hq;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import team122.MapInformation;
import team122.RobotInformation;
import team122.communication.Communicator;

public class HQUtils {
	
	public Communicator com;
	public RobotController rc;
	public int generatorCount;
	public int supplierCount;
	public int soldierCount;
	public int defenderCount;
	public int encamperCount;
	public int minerCount;
	public int totalSoldierCount;
	public int totalEncampmentCount;
	public double powerProduction;
	public double powerToCaptureEncampment;
	public double powerConsumptionFromSoldiers;
	public double teamPower;
	
	public HQUtils(RobotController rc, Communicator com) {
		this.rc = rc;
		this.com = com;
		generatorCount = 0;
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
	}

	/**
	 * Gets the different counts available then zeroing them out.
	 * @throws GameActionException
	 */
	public void counts() throws GameActionException {
		//Stores this rounds counts
		generatorCount = com.receive(Communicator.CHANNEL_GENERATOR_COUNT, 0);
		supplierCount = com.receive(Communicator.CHANNEL_SUPPLIER_COUNT, 0);
		soldierCount = com.receive(Communicator.CHANNEL_SOLDIER_COUNT, 0);
		encamperCount = com.receive(Communicator.CHANNEL_ENCAMPER_COUNT, 0);
		minerCount = com.receive(Communicator.CHANNEL_MINER_COUNT, 0);
		defenderCount = com.receive(Communicator.CHANNEL_DEFENDER_COUNT, 0);

		//Erases so counts will be accurate.
		com.communicate(Communicator.CHANNEL_GENERATOR_COUNT, 0);
		com.communicate(Communicator.CHANNEL_SUPPLIER_COUNT, 0);
		com.communicate(Communicator.CHANNEL_SOLDIER_COUNT, 0);
		com.communicate(Communicator.CHANNEL_MINER_COUNT, 0);
		com.communicate(Communicator.CHANNEL_ENCAMPER_COUNT, 0);
		
		// Basic calculations that are needed by the HQ.
		totalSoldierCount = soldierCount + encamperCount + minerCount + defenderCount;
		totalEncampmentCount = generatorCount + supplierCount;
		
		//Power production is correct but powerToCapture is incorrect.  Its overestimated.
		powerProduction = generatorCount * GameConstants.GENERATOR_POWER_PRODUCTION + GameConstants.HQ_POWER_PRODUCTION;
		powerToCaptureEncampment = GameConstants.CAPTURE_POWER_COST * (1 + totalEncampmentCount + encamperCount);
		powerConsumptionFromSoldiers = GameConstants.UNIT_POWER_UPKEEP * totalSoldierCount;
		teamPower = rc.getTeamPower();
	}
	
	/**
	 * if an encampment should be created.
	 * @param info
	 * @param defensive
	 * @return
	 */
	public boolean shouldCreateEncampment(MapInformation info) {
		return info.totalEncampments / 2 > totalEncampmentCount + encamperCount;
	}
	
	public void printState() {
		System.out.println("Generators: " + generatorCount);
		System.out.println("Suppliers: " + supplierCount);
		System.out.println("Soldiers: " + soldierCount);
		System.out.println("Encampers: " + encamperCount);
		System.out.println("Miners: " + minerCount);
		System.out.println("Defender Count: " + defenderCount);
		System.out.println("Encampment Count: " + totalEncampmentCount);
		System.out.println("Soldier Count: " + totalSoldierCount);
	}
}
