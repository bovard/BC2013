package team122.behavior.soldier;

import java.util.ArrayList;

import team122.behavior.Behavior;
import team122.behavior.IComBehavior;
import team122.communication.Communicator;
import team122.robot.Soldier;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class SoldierDefenseMiner 
		extends Behavior
		implements IComBehavior {
	
	public Soldier robot;
	public ArrayList<MapLocation> mineSpots = new ArrayList<MapLocation>();
	public boolean init;
	public boolean reset_mines = true;
	public int start_ring_num = 1,
	           end_ring_num = 20;

	public SoldierDefenseMiner(Soldier robot2) {
		super();
		this.robot = robot2;
		init = false;
	}

	@Override
	public void start() {
		if (!init) {
			init = true;
			_setMiningLocations(start_ring_num, end_ring_num);
		}
	}

	@Override
	public void stop() {
		// nothing needs to be done here
		
	}
	
	@Override
	public void comBehavior() throws GameActionException {
		robot.com.increment(Communicator.CHANNEL_MINER_COUNT);
	}

	/**
	 * 
	 */
	@Override
	public void run() throws GameActionException {
		if (robot.rc.isActive()) {
			if (mineSpots.size() == 0 || (robot.rc.hasUpgrade(Upgrade.PICKAXE) && reset_mines)) {
				_setMiningLocations(start_ring_num, end_ring_num);
			}
			
			if (robot.navSystem.navMode.atDestination) {
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
				}
				_setDestination();
			} else {
				if (robot.navSystem.navMode.hasDestination) {
					robot.navSystem.navMode.move();
				} else {
					_setDestination();
				}
			}
		}
	}
	
	/**
	 * Sets the destination of the robot and removes one of the
	 * mines.
	 */
	private void _setDestination() {
		robot.navSystem.navMode.setDestination(mineSpots.get(0));
		mineSpots.remove(0);
	}

	@Override
	public boolean pre() {
		return !robot.enemyInMelee;
	}
	
	private void insertMine(int x, int y) {
		int map_width = robot.info.width,
	        map_height = robot.info.height;
		MapLocation created;
		if (x >= 0 && x < map_width && y >= 0 && y < map_height) {
			created = new MapLocation(x, y);
			mineSpots.add(created);
		}
	}
	
	private void _assignRing(int start_ring, int end_ring, int right_nodes, int top_nodes, int left_nodes, int bottom_nodes) {
		int hq_x = robot.info.hq.x;
		int hq_y = robot.info.hq.y;
		
		int x = hq_x,
			y = hq_y;
		
		for (int i = start_ring; i <= end_ring; i++) {
			// insert right nodes
			for (int j = 1; j <= right_nodes; j++) {
				if (i == 1 && j == 1) {
					x += 1;
				} else if (j == 1) {
					x += 3;
				} else if (j <= (right_nodes + 3) / 2) {
					x += 1;
					y -= 2;
				} else {
					x -= 2;
					y -= 2;
				}
				insertMine(x, y);
			}
			// insert top nodes
			for (int k = 1; k <= top_nodes; k++) {
				if (i == 1 && k == 1) {
					x += 1;
					y -= 2;
				} else if (k == 1) {
					x -= 2;
					y -= 2;
				} else {
					x -= 3;
				}
				insertMine(x, y);
			}
			// insert left nodes
			for (int l = 1; l <= left_nodes; l++) {
				if (l <= (left_nodes + 1) / 2) {
					x -= 1;
					y += 2;
				} else {
					x += 2;
					y +=2;
				}
				insertMine(x, y);
			}
			// insert bottom nodes
			for (int m = 1; m <= bottom_nodes; m++) {
				if (m == 1) {
					x += 2;
					y += 2;
				} else {
					x += 3;
				}
				insertMine(x, y);
			}
			right_nodes += 2;
			top_nodes += 1;
			left_nodes += 2;
			bottom_nodes += 1;
		}
	}
	
	private void _setMiningLocations(int start_ring, int end_ring) {
		if (robot.rc.hasUpgrade(Upgrade.PICKAXE)) {
			if (reset_mines) {
				reset_mines = false;
				mineSpots.clear();
				
				int right_nodes = 1,
				    top_nodes = 2,
				    left_nodes = 1,
				    bottom_nodes = 1;
				
				for (int i = 1; i < start_ring; i++) {
					right_nodes += 2;
					top_nodes += 1;
					left_nodes += 2;
					bottom_nodes += 1;
				}
				_assignRing(start_ring, end_ring, right_nodes, top_nodes, left_nodes, bottom_nodes);
			}
		} else {
			int hq_x = robot.info.hq.x;
			int hq_y = robot.info.hq.y;
			// lay mines one at a time
			int right_nodes = 1,
				top_nodes = 3,
				left_nodes = 1,
				bottom_nodes = 3,
				x = hq_x,
				y = hq_y;
			
			for (int i = 0; i <= 12; i++) {
				// insert right nodes
				for (int j = 0; j < right_nodes; j++) {
					if (j == 0) {
						x += 1;
					} else {
						y -= 1;
					}
					insertMine(x, y);
				}
				// insert top nodes
				for (int k = 0; k < top_nodes; k++) {
					if (k == 0) {
						y -= 1;
					} else {
						x -= 1;
					}
					insertMine(x, y);
				}
				// insert left nodes
				for (int l = 0; l < left_nodes; l++) {
					y += 1;
					insertMine(x, y);
				}
				// insert bottom nodes
				for (int m = 0; m < bottom_nodes; m++) {
					if (m == 0) {
						y += 1;
					} else {
						x += 1;
					}
					insertMine(x, y);
				}
				right_nodes += 2;
				top_nodes += 2;
				left_nodes += 2;
				bottom_nodes += 2;
			}
		}
	}
}
