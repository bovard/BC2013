package team122.behavior.lib;

import team122.communication.Communicator;
import team122.robot.Generator;
import team122.robot.HQ;
import battlecode.common.Clock;
import battlecode.common.GameActionException;

public class GeneratorIdle extends Behavior {
	
	protected Generator robot;

	public GeneratorIdle(Generator robot) {
		super();
		this.robot = robot;
	}
	
	@Override
	public void run() throws GameActionException {
		//Sets the count 1 round before the HQ checks.
		if (Clock.getRoundNum() % HQ.HQ_COUNT_ROUND - 1 == 0) {
			robot.com.increment(Communicator.CHANNEL_GENERATOR_COUNT);
		}
	}

	@Override
	public boolean pre() {
		return true;
	}

}