package team122.behavior.lib;

import team122.communication.Communicator;
import team122.robot.Generator;
import battlecode.common.GameActionException;

public class GeneratorIdle extends Behavior {
	
	protected Generator robot;

	public GeneratorIdle(Generator robot) {
		super();
		this.robot = robot;
	}
	
	@Override
	public void run() throws GameActionException {
		//TODO: Jam channels.
		System.out.println("GENERATOR!");
		robot.com.increment(Communicator.CHANNEL_GENERATOR_COUNT);
	}

	@Override
	public boolean pre() {
		return true;
	}

}