package team122.behavior.soldier;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;
import team122.behavior.Behavior;
import team122.communication.Communicator;
import team122.navigation.SoldierMove;
import team122.robot.HQ;
import team122.robot.Soldier;
import team122.utils.GameStrategy;
import team122.utils.MinePlacement;

public class TheJackal extends Behavior {

	public Soldier robot;
	public MapLocation mineSpot;
	private boolean init;
	private boolean attack = false;
	private int attackRound = 0;
	
	public TheJackal(Soldier robot) {
		super();
		this.robot = robot;
		init = false;
		MinePlacement.mapWidth = robot.info.width;
		MinePlacement.mapHeight = robot.info.height;
		MinePlacement.startX = robot.info.hq.x;
		MinePlacement.startY = robot.info.hq.y;
	}
	
	@Override
	public void start() throws GameActionException{
		if (!init) {
			init = true;
			robot.incChannel = Communicator.CHANNEL_SOLDIER_COUNT;
		}
		//we just encoutered an enemy go back to the first ring
		else {
			MinePlacement.reset();
		}
	}
	
	
	@Override
	public void run() throws GameActionException {
		
		if (robot.rc.isActive()) {
			if (attack) {
				
				if (Clock.getRoundNum() - attackRound >= GameStrategy.JACKAL_ATTACK_ROUNDS_TO_WAIT) {
					robot.move.destination = robot.info.enemyHq;
				}
				
				robot.move.move();
				
				return;
			}
		}
		
		// if we receive the attack order, attack!
		if (!attack && Clock.getRoundNum() % HQ.HQ_COMMUNICATION_ROUND == 0 && robot.com.shouldAttack()) {
			robot.move.destination = SoldierSelector.GetInitialRallyPoint(robot.info);
			attack = true;
			attackRound = Clock.getRoundNum();
			robot.move.move();
			return;
		}
		
		
		// if we see an enemy become a scout!
		Robot [] enemies = robot.rc.senseNearbyGameObjects(Robot.class, robot.currentLoc, 200, robot.info.enemyTeam);
		if (enemies.length > 0) {
			robot.move.destination = SoldierSelector.GetInitialRallyPoint(robot.info);
			robot.dec.soldierType = SoldierSelector.SOLDIER_SCOUT;
			robot.move.move();
			return;
		}
		
		
		// as long as we don't see an enemy/mine and de-mine
		if (robot.rc.isActive()) {
			if (robot.move.atDestination()) {
				if (robot.rc.hasUpgrade(Upgrade.PICKAXE)) {
					MapLocation[] all_dir = new MapLocation[5];
					all_dir[0] = robot.currentLoc;
					all_dir[1] = all_dir[0].add(Direction.NORTH);
					all_dir[2] = all_dir[0].add(Direction.EAST);
					all_dir[3] = all_dir[0].add(Direction.SOUTH);
					all_dir[4] = all_dir[0].add(Direction.WEST);
					for (int i = 1; i < all_dir.length; i++) {
						Team t = robot.rc.senseMine(all_dir[i]);
						if (t != null && t != robot.info.myTeam) {
							robot.rc.defuseMine(all_dir[i]);
							return;
						}
					}
				}
				
				if (robot.rc.senseMine(robot.currentLoc) == null) {
					robot.rc.layMine();
					robot.move.destination = null;
				}
				_setDestination();
			} else {
				if (robot.move.destination != null) {
					// check to see if we can sense the square
					if (robot.rc.canSenseSquare(robot.move.destination)) {
						robot.rc.setIndicatorString(0, "canSense " + robot.move.destination + " " + mineSpot.toString());
						// if there is already a mine there, skip it
						if (robot.rc.senseMine(robot.move.destination) == robot.info.myTeam) {
							robot.rc.setIndicatorString(0, "already Mines " + robot.move.destination);
							_setDestination();
						} 
						// if there is any ally there skip it
						else if (robot.rc.senseNearbyGameObjects(Robot.class, robot.move.destination, 1, robot.info.myTeam).length > 0) {
							robot.rc.setIndicatorString(0, "ally there " + robot.move.destination);
							if (MinePlacement.mineSpots.size() >= 2) {
								MinePlacement.mineSpots.add(2, robot.currentLoc);
								
							}
							_setDestination();
						} 
						else {
							robot.rc.setIndicatorString(0, "square is open!");
						}
					}
				} else {
					_setDestination();
				}
			}
		}
		
		if (robot.currentLoc.isAdjacentTo(robot.info.hq)) {
			robot.move.destination = SoldierSelector.GetInitialRallyPoint(robot.info);
		}
		
		if (robot.rc.isActive()) {
			robot.move.move();
		}
	}
	
	private void _setDestination() {
		if (robot.rc.hasUpgrade(Upgrade.PICKAXE)) {
			MinePlacement.hasPickAxe = true;
		}
		mineSpot = MinePlacement.getMineSpot();
		MinePlacement.mineSpots.remove(0);
		robot.move.setDestination(mineSpot);
	}

	@Override
	public boolean pre() {
		return !robot.enemyInMelee;
	}
	
}
