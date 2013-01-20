package team122.robot;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Robot;
import team122.RobotInformation;
import team122.behavior.IComBehavior;
import team122.behavior.Node;
import team122.combat.MoveCalculator;
import team122.communication.Communicator;
import team122.navigation.NavigationSystem;
import team122.trees.SoldierTree;

public class Soldier extends TeamRobot {
	
	public NavigationSystem navSystem = null;
	public GameObject[] enemiesAtTheGates;
	public boolean enemyAtTheGates;
	public Robot[] enemiesInSight;
	public boolean enemyInSight;
	public Robot[] meleeObjects;
	public boolean enemyInMelee;
	public MapLocation currentLoc;
	public MapLocation previousLoc;
	public boolean isNew = true;
	public int initialData;
	public int initialMode;
	public MoveCalculator mCalc;
	public boolean loadDone = false;
	
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
		int time = Clock.getBytecodeNum();
		mCalc = new MoveCalculator(this);
		System.out.println("init takes " + (Clock.getBytecodeNum()-time));
	}
	
	@Override
	public void load() {
		while(!loadDone && Clock.getBytecodesLeft() > 3000) {
			System.out.println("LOADING....");
			int time = Clock.getBytecodeNum();
			loadDone = mCalc.load();
			System.out.println("one load takes " + (Clock.getBytecodeNum()-time));	
		}
	}

	@Override
	public void environmentCheck() throws GameActionException {
		enemyInMelee = false;
		previousLoc = currentLoc;
		currentLoc = rc.getLocation();
		
		// check to see if there is an enemy near our base!
		enemiesAtTheGates = rc.senseNearbyGameObjects(Robot.class, info.hq, 36, info.enemyTeam);
		enemyAtTheGates = enemiesAtTheGates.length > 0;

		// check to see if we can see any enemies
		// TODO: worry about if these are soldiers or not?
		enemiesInSight = rc.senseNearbyGameObjects(Robot.class, 32, info.enemyTeam);
		enemyInSight = enemiesInSight.length > 0;
		
		if (enemyInSight) {
			// check to see if there is anyone in range that can shoot us
			// TODO: change this to detect only soldiers
			meleeObjects = rc.senseNearbyGameObjects(Robot.class, 15);
			for (Robot object:meleeObjects) {
				if (object.getTeam() == info.enemyTeam) {
					enemyInMelee = true;
					break;
				}
			}
		}
		//Continue 
		if (Clock.getRoundNum() % HQ.HQ_COUNT_ROUND - 1 == 0) {
			Node curr = tree.current;
			if (curr instanceof IComBehavior) {
				((IComBehavior)curr).comBehavior();
			}
		}
		
		
		
	}
}
