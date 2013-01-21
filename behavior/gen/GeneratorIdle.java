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
	}

	@Override
	public boolean pre() {
		return true;
	}

}