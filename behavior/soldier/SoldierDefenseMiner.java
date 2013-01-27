package team122.behavior.soldier;

import java.util.ArrayList;

import team122.behavior.Behavior;
import team122.robot.Soldier;
import team122.utils.MinePlacement;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class SoldierDefenseMiner 
		extends Behavior{
	
	public Soldier robot;
	public ArrayList<MapLocation> mineSpots = new ArrayList<MapLocation>();
	public boolean init;
	public boolean reset_mines = true;
	public int start_ring_num = 1,
	           end_ring_num = 1;

	public SoldierDefenseMiner(Soldier robot2) {
		super();
		this.robot = robot2;
		init = false;
	}

	@Override
	public void start() throws GameActionException{
		if (!init) {
			init = true;
			_setMiningLocations();
		}
		//we just encoutered an enemy go back to the two first rings
		else {
			start_ring_num = 1;
			end_ring_num = 1;
			_setMiningLocations();
		}
	}
	
	@Override
	public void run() throws GameActionException {
		
		if (robot.rc.isActive()) {
			if (mineSpots.size() == 0) {
				start_ring_num += 1;
				end_ring_num += 1;
				_setMiningLocations();
			}
			if (robot.move.atDestination()) {
				MapLocation[] all_dir = new MapLocation[5];
				all_dir[0] = robot.rc.getLocation();
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
				if (robot.rc.senseMine(all_dir[0]) == null) {
					robot.rc.layMine();
					robot.move.destination = null;
				}
				_setDestination();
			} else {
				if (robot.move.destination != null) {
					// check to see if we can sense the square
					if (robot.rc.canSenseSquare(robot.move.destination)) {
						robot.rc.setIndicatorString(0, "canSense " + robot.move.destination + " " + mineSpots.toString());
						// if there is already a mine there, skip it
						if (robot.rc.senseMine(robot.move.destination) == robot.info.myTeam) {
							robot.rc.setIndicatorString(0, "already Mines " + robot.move.destination);
							_setDestination();
						} 
						// if there is any ally there skip it
						else if (robot.rc.senseNearbyGameObjects(Robot.class, robot.move.destination, 1, robot.info.myTeam).length > 0) {
							robot.rc.setIndicatorString(0, "ally there " + robot.move.destination);
							mineSpots.add(2, robot.currentLoc);
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
	
	/**
	 * Sets the destination of the robot and removes one of the
	 * mines.
	 */
	private void _setDestination() {
		if (mineSpots.size() > 0) {
			robot.move.setDestination(mineSpots.get(0));
			mineSpots.remove(0);
		}
	}

	@Override
	public boolean pre() {
		return !robot.enemyInMelee;
	}
	
	private void _setMiningLocations() {
		int startX = robot.info.hq.x,
			startY = robot.info.hq.y,
			mapWidth = robot.info.width,
			mapHeight = robot.info.height;
		mineSpots = MinePlacement.getMiningLocations(mapWidth, mapHeight, startX, startY, 
				start_ring_num, end_ring_num, robot.rc.hasUpgrade(Upgrade.PICKAXE));
	}
}
