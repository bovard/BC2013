package team122.behavior.lib;

import team122.robot.Artillery;
import battlecode.common.GameActionException;

public class ArtilleryIdle extends Behavior {
	
	protected Artillery robot;

	@Override
	public void run() throws GameActionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean pre() {
		return robot.canShoot && robot.enemyNearby;
	}

}
