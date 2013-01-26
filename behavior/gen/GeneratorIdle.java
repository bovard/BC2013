package team122.behavior.gen;

import team122.behavior.Behavior;
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
		if ((Clock.getRoundNum() + 1) % HQ.HQ_COUNT_ROUND == 0) {
			robot.com.increment(Communicator.CHANNEL_GENERATOR_COUNT, Clock.getRoundNum() + 1);
		}
	}

	@Override
	public boolean pre() {
		return true;
	}

}