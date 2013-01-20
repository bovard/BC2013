package team122.robot;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team122.RobotInformation;
import team122.behavior.IComBehavior;
import team122.behavior.Node;
import team122.behavior.soldier.SoldierDefenseMiner;
import team122.behavior.soldier.SoldierEncamper;
import team122.behavior.soldier.SoldierSwarm;
import team122.communication.Communicator;
import team122.navigation.NavigationMode;
import team122.navigation.NavigationSystem;
import team122.trees.SoldierTree;

public class Soldier extends Robot {
	
	public NavigationSystem navSystem = null;
	public GameObject[] enemiesAtTheGates;
	public boolean enemyAtTheGates;
	public GameObject[] enemiesInSight;
	public boolean enemyInSight;
	public GameObject[] enemiesInMelee;
	public boolean enemyInMelee;
	public boolean isNew = true;
	public int initialData;
	public int initialMode;
	
	public Soldier(RobotController rc, RobotInformation info) {
		super(rc, info);
		navSystem = new NavigationSystem(rc, info);
		this.tree = new SoldierTree(this);

		com.seedChannels(5, new int[] {
			Communicator.CHANNEL_NEW_SOLDIER_MODE,
			Communicator.CHANNEL_MINER_COUNT,
			Communicator.CHANNEL_ENCAMPER_COUNT,
			Communicator.CHANNEL_SOLDIER_COUNT
		});
	}

	@Override
	public void environmentCheck() throws GameActionException {
		// check to see if there is an enemy near our base!
		enemiesAtTheGates = rc.senseNearbyGameObjects(GameObject.class, info.hq, 36, info.enemyTeam);
		enemyAtTheGates = enemiesAtTheGates.length > 0;
		
		// check to see if there is anyone in range that can shoot us
		// TODO: change this to detect only soldiers
		enemiesInMelee = rc.senseNearbyGameObjects(GameObject.class, 3, info.enemyTeam);
		enemyInMelee = enemiesInMelee.length > 0;
		
		// check to see if we can see any enemies
		// TODO: worry about if these are soldiers or not?
		enemiesInSight = rc.senseNearbyGameObjects(GameObject.class, 32, info.enemyTeam);
		enemyInSight = enemiesInSight.length > 0;
		
		//Continue 
		if (Clock.getRoundNum() % HQ.HQ_COUNT_ROUND - 1 == 0) {
			Node curr = tree.current;
			if (curr instanceof IComBehavior) {
				((IComBehavior)curr).comBehavior();
			}
		}
	}
}
