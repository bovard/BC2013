package team122.robot;

import team122.communication.Communicator;
import team122.trees.GeneratorTree;
import team122.RobotInformation;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.RobotController;

public class Generator extends TeamRobot {
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
	public void environmentCheck() throws GameActionException {
		canShoot = rc.isActive();
		if (canShoot) {
			enemiesNearby = rc.senseNearbyGameObjects(GameObject.class, 81, info.enemyTeam);
			enemyNearby = enemiesNearby.length > 0;
			
			if (enemyNearby) {
				nearbyObjects = rc.senseNearbyGameObjects(GameObject.class, 81);
			}
		}

		if (Clock.getRoundNum() % HQ.HQ_COUNT_ROUND - 1 == 0) {
			com.increment(Communicator.CHANNEL_GENERATOR_COUNT);
		}
	}
}
