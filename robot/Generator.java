package team122.robot;

import team122.communication.Communicator;
import team122.trees.GeneratorTree;
import team122.RobotInformation;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.RobotController;

public class Generator extends TeamRobot {

	public Generator(RobotController rc, RobotInformation info) {
		super(rc, info);
		tree = new GeneratorTree(this);
	}

	@Override
	public void environmentCheck() throws GameActionException {

		if ((Clock.getRoundNum() + 1) % HQ.HQ_COMMUNICATION_ROUND == 0) {
			com.increment(Communicator.CHANNEL_GENERATOR_COUNT, Clock.getRoundNum() + 1);
		}
	}
}
