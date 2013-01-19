package team122.behavior.lib.hq;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import team122.RobotInformation;
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
	public double powerToCaptureEncampment;
	public double powerConsumptionFromSoldiers;
	
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
		encamperCount = com.receive(Communicator.CHANNEL_MINER_COUNT, 0);
		minerCount = com.receive(Communicator.CHANNEL_ENCAMPER_COUNT, 0);
		totalSoldierCount = soldierCount + encamperCount + minerCount;
		totalEncampmentCount = generatorCount + supplierCount;
		
		//Power production is correct but powerToCapture is incorrect.  Its overestimated.
		powerProduction = generatorCount * GameConstants.GENERATOR_POWER_PRODUCTION + GameConstants.HQ_POWER_PRODUCTION;
		powerToCaptureEncampment = GameConstants.CAPTURE_POWER_COST * (1 + totalEncampmentCount + encamperCount);
		powerConsumptionFromSoldiers = GameConstants.UNIT_POWER_UPKEEP * totalSoldierCount;
		
		//Erases so counts will be accurate.
		com.communicate(Communicator.CHANNEL_GENERATOR_COUNT, 0);
		com.communicate(Communicator.CHANNEL_SUPPLIER_COUNT, 0);
		com.communicate(Communicator.CHANNEL_SOLDIER_COUNT, 0);
		com.communicate(Communicator.CHANNEL_MINER_COUNT, 0);
		com.communicate(Communicator.CHANNEL_ENCAMPER_COUNT, 0);
	}

	/**
	 * If we require an immediant miner.
	 * @return
	 */
	public boolean requireMiner(int defensive) {
		return defensive / REQUIRE_DEFENSIVE_MINER_DIVIDER > 0;
	}
	
	/**
	 * If a soldier can be spawned.
	 * @return
	 */
	public boolean canSpawnSoldier() {
		return powerProduction > powerConsumptionFromSoldiers + GameConstants.UNIT_POWER_UPKEEP;
	}
	
	/**
	 * Requires soldiers.
	 * @return
	 */
	public boolean requireSoldier(int defensive, int econ) {
		return totalSoldierCount - defensive / REQUIRE_DEFENSIVE_SOLDIER_DIVIDER > REQUIRE_DEFENSIVE_SOLDIER_CUTOFF_LINE;
	}
	
	/**
	 * If a generator should be created asap.
	 * @return
	 */
	public boolean requireGenerator() {
		return powerProduction - powerConsumptionFromSoldiers < powerToCaptureEncampment * REQUIRE_GENERATOR_MUL;
	}
	
	/**
	 * If a supplier can be created.
	 * @return
	 */
	public boolean canCreateSupplier() {
		return powerProduction - powerConsumptionFromSoldiers > powerToCaptureEncampment * REQUIRE_SUPPLIER_MUL;
	}
	
	/**
	 * can create artillery
	 * @return
	 */
	public boolean canCreateArtillery() {
		return powerProduction - powerConsumptionFromSoldiers > powerToCaptureEncampment * REQUIRE_ARTILLERY_MUL;
	}
	
	/**
	 * if an encampment should be created.
	 * @param info
	 * @param defensive
	 * @return
	 */
	public boolean shouldCreateEncampment(RobotInformation info, int defensive) {
		return info.encampments.length / 2 < totalEncampmentCount + encamperCount;
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

	public final static int REQUIRE_GENERATOR_MUL = 3;
	public final static int REQUIRE_SUPPLIER_MUL = 3;
	public final static int REQUIRE_ARTILLERY_MUL = 5;
	public final static int REQUIRE_DEFENSIVE_MINER_DIVIDER = 33;
	public final static int REQUIRE_DEFENSIVE_SOLDIER_DIVIDER = 3;
	public final static int REQUIRE_DEFENSIVE_SOLDIER_CUTOFF_LINE = 10;
}
