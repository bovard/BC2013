package team122.behavior.artillery;

import team122.behavior.Behavior;
import team122.robot.Artillery;
import battlecode.common.GameActionException;

public class ArtilleryIdle extends Behavior {
	
	protected Artillery robot;

	public ArtilleryIdle(Artillery robot) {
		super();
		this.robot = robot;
	}
	
	@Override
	public void run() throws GameActionException {
				
	}

	@Override
	public boolean pre() {
		return !robot.canShoot || !robot.enemyNearby;
	}

}
