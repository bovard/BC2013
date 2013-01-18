package team122.robot;

import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Robot;
import team122.RobotInformation;
import team122.navigation.NavigationMode;
import team122.navigation.NavigationSystem;
import team122.trees.SoldierTree;

public class Soldier extends TeamRobot {
	
	public NavigationSystem navSystem = null;
	public NavigationMode navMode = null;
	public Robot[] enemiesAtTheGates;
	public boolean enemyAtTheGates;
	public Robot[] enemiesInSight;
	public boolean enemyInSight;
	public Robot[] meleeObjects;
	public boolean enemyInMelee;
	public MapLocation currentLoc;
	public MapLocation previousLoc;
	
	public Soldier(RobotController rc, RobotInformation info) {
		super(rc, info);
		navSystem = new NavigationSystem(rc, info);
		navMode = navSystem.navMode;		
		this.tree = new SoldierTree(this);
	}

	@Override
	public void environmentCheck() {
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
			meleeObjects = rc.senseNearbyGameObjects(Robot.class, 8);
			for (Robot object:meleeObjects) {
				if (object.getTeam() == info.enemyTeam) {
					enemyInMelee = true;
					break;
				}
			}
		}
	}
}
