package team122.robot;

import team122.RobotInformation;
import battlecode.common.GameObject;
import battlecode.common.RobotController;

public class Artillery extends TeamRobot{
	public boolean canShoot;
	public GameObject[] nearbyObjects;
	public boolean enemyNearby;
	public GameObject[] enemiesNearby;

	public Artillery(RobotController rc, RobotInformation info) {
		super(rc, info);
		
	}

	@Override
	public void environmentCheck() {
		canShoot = rc.isActive();
		if (canShoot) {
			enemiesNearby = rc.senseNearbyGameObjects(GameObject.class, 81, info.enemyTeam);
			enemyNearby = enemiesNearby.length > 0;
			
			if (enemyNearby) {
				nearbyObjects = rc.senseNearbyGameObjects(GameObject.class, 81);
			}
		}
	}

}
