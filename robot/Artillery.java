package team122.robot;

import team122.communication.Communicator;
import team122.trees.ArtilleryTree;
import team122.RobotInformation;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Artillery extends TeamRobot{
	public boolean canShoot;
	public Robot[] nearbyObjects;
	public boolean enemyNearby;
	public Robot[] enemiesNearby;
	public int artilleryRange;

	public Artillery(RobotController rc, RobotInformation info) {
		super(rc, info);
		tree = new ArtilleryTree(this);
		
		//The seed is for a possible end time strategy of changing the seed to
		//switch all channels.
		artilleryRange = RobotType.ARTILLERY.attackRadiusMaxSquared;
	}

	@Override
	public void environmentCheck() throws GameActionException {
		rc.setIndicatorString(0, "-");
		canShoot = rc.isActive();
		if (canShoot) {
			
			enemiesNearby = rc.senseNearbyGameObjects(Robot.class, artilleryRange, info.enemyTeam);
			enemyNearby = enemiesNearby.length > 0;
			
			if (enemyNearby) {
				rc.setIndicatorString(0, "Found enemies!");
				nearbyObjects = rc.senseNearbyGameObjects(Robot.class, artilleryRange);
			} else {
				rc.setIndicatorString(0, "No enemies nearby");
			}
		} else {
			rc.setIndicatorString(0, "Can't shoot");
		}

		if ((Clock.getRoundNum() + 1) % HQ.HQ_COUNT_ROUND == 0) {
			com.increment(Communicator.CHANNEL_ARTILLERY_COUNT, Clock.getRoundNum() + 1);
		}
	}

}
