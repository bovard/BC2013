package team122.behavior.lib.hq;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import team122.communication.Communicator;

public class HQUtils {
	
	public Communicator com;
	public RobotController rc;
	public int generatorCount;
	public int supplierCount;
	public int soldierCount;
	public int encamperCount;
	public int minerCount;
	public int totalSoldierCount;
	public int totalEncampmentCount;
	public double powerProduction;
	
	public HQUtils(RobotController rc, Communicator com) {
		this.rc = rc;
		this.com = com;
		generatorCount = 0;
		supplierCount = 0;
		soldierCount = 0;
		encamperCount = 0;
		minerCount = 0;
		totalSoldierCount = 0;
		totalEncampmentCount = 0;
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
		encamperCount = com.receive(Communicator.CHANNEL_MINER_COUNT, 0);
		minerCount = com.receive(Communicator.CHANNEL_ENCAMPER_COUNT, 0);
		totalSoldierCount = soldierCount + encamperCount + minerCount;
		totalEncampmentCount = generatorCount + supplierCount;
		powerProduction = generatorCount * GameConstants.GENERATOR_POWER_PRODUCTION + GameConstants.HQ_POWER_PRODUCTION;
		
		//Erases so counts will be accurate.
		com.communicate(Communicator.CHANNEL_GENERATOR_COUNT, 0);
		com.communicate(Communicator.CHANNEL_SUPPLIER_COUNT, 0);
		com.communicate(Communicator.CHANNEL_SOLDIER_COUNT, 0);
		com.communicate(Communicator.CHANNEL_MINER_COUNT, 0);
		com.communicate(Communicator.CHANNEL_ENCAMPER_COUNT, 0);
	}

	
	public void printState() {
		System.out.println("Generators: " + generatorCount);
		System.out.println("Suppliers: " + supplierCount);
		System.out.println("Soldiers: " + soldierCount);
		System.out.println("Encampers: " + encamperCount);
		System.out.println("Miners: " + minerCount);
		System.out.println("Encampment Count: " + totalEncampmentCount);
		System.out.println("Soldier Count: " + totalSoldierCount);
	}
}
