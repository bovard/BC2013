package team122.robot;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Robot;
import battlecode.common.Team;
import team122.RobotInformation;
import team122.behavior.IComBehavior;
import team122.behavior.Node;
import team122.combat.MoveCalculator;
import team122.communication.Communicator;
import team122.communication.SoldierDecoder;
import team122.navigation.SoldierMove;
import team122.trees.SoldierTree;

public class Soldier extends TeamRobot {
	
	public GameObject[] enemiesAtTheGates;
	public boolean enemyAtTheGates;
	public Robot[] enemiesInSight;
	public boolean enemyInSight;
	public Robot[] meleeObjects;
	public boolean enemyInMelee;
	public boolean combatMode;
	public MapLocation currentLoc;
	public MapLocation previousLoc;
	public boolean isNew = true;
	public MoveCalculator mCalc;
	public boolean loadDone = false;
	public boolean isNukeArmed = false;
	public MapLocation[] neutral_mines;
	public MapLocation[] allied_mines;
	public MapLocation[] enemy_mines;
	public SoldierMove move;
	public SoldierDecoder dec;
	public int incChannel = -1;
	
	public Soldier(RobotController rc, RobotInformation info) {
		super(rc, info);
		this.move = new SoldierMove(this);
		
		this.tree = new SoldierTree(this);
		mCalc = new MoveCalculator(this);
	}
	
	@Override
	public void load() {
		
	}

	@Override
	public void environmentCheck() throws GameActionException {
		enemyInMelee = false;
		combatMode = false;
		previousLoc = currentLoc;
		currentLoc = rc.getLocation();
		neutral_mines = rc.senseMineLocations(currentLoc, 3, Team.NEUTRAL);
		allied_mines = rc.senseMineLocations(currentLoc, 3, info.myTeam);
		
		
		// check to see if there is an enemy near our base!
		enemiesAtTheGates = rc.senseNearbyGameObjects(Robot.class, info.hq, 36, info.enemyTeam);
		enemyAtTheGates = enemiesAtTheGates.length > 0;

		// check to see if we can see any enemies
		enemiesInSight = rc.senseNearbyGameObjects(Robot.class, 32, info.enemyTeam);
		enemyInSight = enemiesInSight.length > 0;
		
		if (enemyInSight) {
			enemyInMelee = true;
			enemy_mines = rc.senseMineLocations(currentLoc, 3, info.enemyTeam);
			// check to see if there is anyone in range that can shoot us
			meleeObjects = rc.senseNearbyGameObjects(Robot.class, 15);
			Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 15, info.enemyTeam);
			if (enemies.length > 0) {
				combatMode = true;
			}
		}
		//Continue 
		if ((Clock.getRoundNum() + 1) % HQ.HQ_COMMUNICATION_ROUND == 0 && incChannel > -1) {
			com.increment(incChannel, Clock.getRoundNum() + 1);
			com.setTotalPower((int)rc.getTeamPower());
		} else if (Clock.getRoundNum() % HQ.HQ_COMMUNICATION_ROUND == 0) {
			
			//Check for nuke
			isNukeArmed = com.isNukeArmed();
		}
		
		
	}
}
