package team122.behavior.soldier;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team122.behavior.Behavior;
import team122.robot.Soldier;

public class SoldierDarkHorseDefender extends Behavior {

	protected Soldier robot;
	protected MapLocation[] artLocations;
	protected int turnToSpawn = 200;
	
	public SoldierDarkHorseDefender(Soldier robot) {
		this.robot = robot;
	}
	
	@Override
	public void run() throws GameActionException {
		// if it's still early game, throw up some mines
		if (turnToSpawn < 200) {
			
		} 
		
		else {
			
		}
		
	}

	@Override
	public boolean pre() throws GameActionException {
		return !robot.enemyInMelee;
	}
	

}
