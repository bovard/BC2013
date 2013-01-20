package team122.robot;

import team122.communication.Communicator;
import team122.trees.GeneratorTree;
import team122.RobotInformation;
import battlecode.common.GameObject;
import battlecode.common.RobotController;

public class Generator extends Robot {
	public boolean canShoot;
	public GameObject[] nearbyObjects;
	public boolean enemyNearby;
	public GameObject[] enemiesNearby;

	public Generator(RobotController rc, RobotInformation info) {
		super(rc, info);
		tree = new GeneratorTree(this);
		com.seedChannels(5, new int[] { 
			Communicator.CHANNEL_GENERATOR_COUNT
		});
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
