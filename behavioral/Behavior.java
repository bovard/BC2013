package team122.behavioral;

import java.util.Random;
import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.Team;

public abstract class Behavior {
	public RobotController rc;
	public Team myTeam;
	public Team enemyTeam;
	public Random rand;
	
	public Behavior(RobotController rc) {
		this.rc = rc;

		myTeam = rc.getTeam();
		if (myTeam == Team.A) {
			enemyTeam = Team.B;
		} else if (myTeam == Team.B) {
			enemyTeam = Team.A;
		}
		
		rand = new Random();
    	rand.setSeed(Clock.getRoundNum());
	}
	
	/**
	 * Calls the behavioral algorithm on the soldier.
	 */
	public abstract void behave();
	
}
