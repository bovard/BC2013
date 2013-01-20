package team122.robot;

import team122.communication.Communicator;
import team122.trees.ArtilleryTree;
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
		tree = new ArtilleryTree(this);
		
		//The seed is for a possible end time strategy of changing the seed to
		//switch all channels.
		com.seedChannels(5, new int[] {
			Communicator.CHANNEL_ARTILLERY_COUNT
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
