package team122.behavioral;

import java.util.Random;

import team122.RobotInformation;
import team122.communication.Communicator;
import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.Team;

public abstract class Behavior {
	public RobotController rc;
	public Team myTeam;
	public Team enemyTeam;
	public Random rand;
	public RobotInformation info;
	public Communicator com;
	
	public Behavior(RobotController rc, RobotInformation info) {
		this.rc = rc;
		this.info = info;
		
		rand = new Random();
    	rand.setSeed(Clock.getRoundNum());
    	com = new Communicator(rc, info);
	}
	
	/**
	 * Calls the behavioral algorithm on the soldier.
	 */
	public abstract void behave();
	
}
