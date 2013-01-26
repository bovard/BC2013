package team122.robot;

import team122.communication.Communicator;
import team122.trees.SupplierTree;
import team122.RobotInformation;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.RobotController;

public class Supplier extends TeamRobot {

	public Supplier(RobotController rc, RobotInformation info) {
		super(rc, info);
		tree = new SupplierTree(this);
	}

	@Override
	public void environmentCheck() throws GameActionException {
		if ((Clock.getRoundNum() + 1) % HQ.HQ_COUNT_ROUND == 0) {
			com.increment(Communicator.CHANNEL_SUPPLIER_COUNT, Clock.getRoundNum() + 1);
		}
	}
}
