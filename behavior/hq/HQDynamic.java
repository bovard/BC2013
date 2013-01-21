package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierEncamper;
import team122.behavior.soldier.SoldierSelector;
import team122.communication.CommunicationDecoder;
import team122.communication.Communicator;
import team122.robot.HQ;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class HQDynamic extends Behavior {
	
	protected HQ robot;
	protected HQUtils utils;
	protected RobotController rc;
	protected int nukeCount;

	public HQDynamic(HQ robot) {
		super();
		this.robot = robot;
		this.utils = robot.hqUtils;
		this.rc = robot.rc;
		nukeCount = 0;
	}
	
	@Override
	public void run() throws GameActionException {
		
		int i = 0;
		
		
		if (i % 2 == 0 && robot.hasMoreGenSpots && utils.encamperCount < 1) {
			if (robot.peekGeneratorEncampment() != null) {
				//Make this use utils.generatorCost/supplierCost/artilleryCost to figure out what encampment to make
				
				robot.spawn(SoldierSelector.SOLDIER_ENCAMPER, new CommunicationDecoder(robot.popGeneratorEncampment(), SoldierEncamper.GENERATOR_ENCAMPER), Communicator.CHANNEL_ENCAMPER_LOCATION);
			}
		} else if (robot.hasMoreArtillerySpots && utils.encamperCount < 2) {
			if (robot.peekArtilleryEncampment() != null) {
				//Make this use utils.generatorCost/supplierCost/artilleryCost to figure out what encampment to make
				
				robot.spawn(SoldierSelector.SOLDIER_ENCAMPER, new CommunicationDecoder(robot.popArtilleryEncampment(), SoldierEncamper.ARTILLERY_ENCAMPER), Communicator.CHANNEL_ENCAMPER_LOCATION);
			}
		}
		i++;
	}

	@Override
	public boolean pre() {
		return robot.rc.isActive();
	}
	
	public static final int MINER_COUNT = 2;
	public static final int ROBOT_LOWER_SOLDIER_COUNT = 8;
	public static final int ROBOT_UPPER_SOLDIER_COUNT = 80;
	public static final int ROBOT_SUPPLIER_COUNT = 15;
	public static final int ROBOT_GENERATOR_COUNT = 5;
	public static final int ROBOT_ENCAMPER_COUNT = 3;
}
