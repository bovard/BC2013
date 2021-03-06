package team122.behavior.supplier;

import team122.behavior.Behavior;
import team122.communication.Communicator;
import team122.robot.HQ;
import team122.robot.Supplier;
import battlecode.common.Clock;
import battlecode.common.GameActionException;

public class SupplierIdle extends Behavior {
	
	protected Supplier robot;

	public SupplierIdle(Supplier robot) {
		super();
		this.robot = robot;
	}
	
	@Override
	public void run() throws GameActionException {
		//Sets the count 1 round before the HQ checks.
	}

	@Override
	public boolean pre() {
		return true;
	}

}