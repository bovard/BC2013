package team122.behavior.soldier;

import team122.behavior.Behavior;
import team122.communication.Communicator;
import team122.robot.Soldier;
import team122.utils.MapQuadrantUtils;
import team122.utils.MinePlacement;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class SoldierCornerMiner extends Behavior{
	
	public Soldier robot;
	public MapLocation mineSpot;
	public boolean init;
	private int enemyQuad;
	private int ourQuad;

	public SoldierCornerMiner(Soldier robot) {
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
			// TODO: Michael add a channel to these guys plz.
			//robot.incChannel = Communicator.CHANNEL_MINER_COUNT;
			
			enemyQuad = MapQuadrantUtils.getMapQuadrant(robot.info.enemyHq.x, robot.info.enemyHq.y);
			ourQuad = MapQuadrantUtils.getMapQuadrant(robot.info.hq.x, robot.info.hq.y);
			
			// bases are on the Y axis, split into 2,1 and 3, 4 randomly
			if (Math.abs(robot.info.hq.x - robot.info.enemyHq.x) < 10  && Math.abs(robot.info.hq.x + robot.info.enemyHq.x - robot.info.width) < 3) {
				if (Clock.getRoundNum() % 2 == 0) {
					MinePlacement.startX = 0;
					MinePlacement.startY = robot.info.height / 2;
				} else {
					MinePlacement.startX = robot.info.width - 1;
					MinePlacement.startY = robot.info.height / 2;
				}
				
			}
			// bases are on the X axis, split into 1,4 and 2,3 randomly
			else if (Math.abs(robot.info.hq.y - robot.info.enemyHq.y) < 10 && Math.abs(robot.info.hq.y + robot.info.enemyHq.y - robot.info.height) < 3) {
				
				if (Clock.getRoundNum() % 2 == 0) {
					MinePlacement.startX = robot.info.width / 2;
					MinePlacement.startY = robot.info.height - 2;
				} else {
					MinePlacement.startX = robot.info.width / 1;
					MinePlacement.startY = 0;
				}
				
			}
			// bases are in the same quadrant (this would be bad) or opposite quadants (normal)
			else {
				MapLocation loc;
				for (int i = 1; i <= 4; i++){
					if(i != enemyQuad && i != ourQuad) {
						// TODO: Bovard this will only hit one corner use the group to hit the right one
						if (i == 1) {
							loc = MapQuadrantUtils.getMapCornerForQuadrant(1);
							MinePlacement.startX = loc.x;
							MinePlacement.startY = loc.y;
						} else if (i == 2) {
							loc = MapQuadrantUtils.getMapCornerForQuadrant(2);
							MinePlacement.startX = loc.x;
							MinePlacement.startY = loc.y;
						} else if (i == 3) {
							loc = MapQuadrantUtils.getMapCornerForQuadrant(3);
							MinePlacement.startX = loc.x;
							MinePlacement.startY = loc.y;
						} else {
							loc = MapQuadrantUtils.getMapCornerForQuadrant(4);
							MinePlacement.startX = loc.x;
							MinePlacement.startY = loc.y;
						}
					}
				}
			}
			
			
		}
		//we just encoutered an enemy go back to the first ring
		else {
			MinePlacement.reset();
		}
	}
	
	@Override
	public void run() throws GameActionException {
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
