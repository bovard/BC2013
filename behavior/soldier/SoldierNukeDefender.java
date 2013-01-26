package team122.behavior.soldier;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Team;
import team122.behavior.Behavior;
import team122.behavior.IComBehavior;
import team122.communication.Communicator;
import team122.robot.Soldier;

public class SoldierNukeDefender extends Behavior {
	
	protected Soldier robot;
	private final int MAX_DISTANCE = 900;
	
	public SoldierNukeDefender(Soldier robot) {
		this.robot = robot;
		
	}
	
	public void start() {
		robot.incChannel = Communicator.CHANNEL_NUKE_COUNT;
	}

	@Override
	public void run() throws GameActionException {
		if (!robot.rc.isActive())
			return;
		
		if (robot.enemyAtTheGates) {
			robot.move.destination = robot.info.hq;
			robot.move.move();
		}
		
		if(Clock.getRoundNum() > 530) {
			robot.move.destination = robot.info.hq;
			robot.move.move();
		}
		
		MapLocation loc = robot.currentLoc;

		
		// if we are at our destination, de-mine neutrals then lay a mine
		if(loc.equals(robot.move.destination)) {
			//System.out.println("AT DESTINATION");
			// if there is a mine here, pick a new location
			if(robot.rc.senseMine(loc) == robot.info.myTeam) {
				robot.move.destination = null;
			} 
			
			// de-mine any neutral mines, then lay mines here!
			else {
				//System.out.println("DEFUSING AND MINING DESTINATION");
				if(robot.rc.senseMine(loc.add(Direction.NORTH)) == Team.NEUTRAL) {
					robot.rc.defuseMine(loc.add(Direction.NORTH));
				} else if(robot.rc.senseMine(loc.add(Direction.SOUTH)) == Team.NEUTRAL) {
					robot.rc.defuseMine(loc.add(Direction.SOUTH));
				} else if(robot.rc.senseMine(loc.add(Direction.EAST)) == Team.NEUTRAL) {
					robot.rc.defuseMine(loc.add(Direction.EAST));
				} else if(robot.rc.senseMine(loc.add(Direction.WEST)) == Team.NEUTRAL) {
					robot.rc.defuseMine(loc.add(Direction.WEST));
				} else {
					//System.out.println("MINING DESTINATION");
					robot.rc.layMine();
					robot.move.destination = null;
				}
			}
		}
		
		// if we have a destination but we aren't there
		else if (robot.move.destination != null) {
			// is someone is already there, pick a new square
			if (robot.rc.canSenseSquare(robot.move.destination)) {
				if (robot.rc.senseObjectAtLocation(robot.move.destination) != null) {
					robot.move.destination = null;
					return;
				}
			} 
			// else move to the square
			robot.move.move();
		}
		
		// we need to pick a new destination!
		else {
			//System.out.println("Making new destination!");
			Direction dir = robot.info.enemyDir;
			
			// if we are too far away, try going toward the base first
			if(loc.distanceSquaredTo(robot.info.hq) > MAX_DISTANCE) {
				dir = dir.opposite();
			}
			
			boolean done = false;
			int count = 0;
			while (!done && count < 4) {
				// try to find a new square to mine
				count ++;
				MapLocation left = loc.add(dir).add(dir.rotateLeft());
				MapLocation right = loc.add(dir).add(dir.rotateRight());
				if (left.x > 0 && left.y > 0 && left.x < robot.info.width && left.y < robot.info.height && robot.rc.senseMine(left) != robot.info.myTeam) {
					robot.move.destination = left;
					done = true;
				} else if(right.x > 0 && right.y > 0 && right.x < robot.info.width && right.y < robot.info.height && robot.rc.senseMine(right) != robot.info.myTeam) {
					robot.move.destination = right;
					done = true;
				}
				dir = dir.rotateLeft().rotateLeft();
			}
			
			// if we haven't picked a destination yet!
			if (!done) {
				robot.move.destination = loc.add(dir).add(dir);
			}
			//System.out.println("DESTINATION IS " + robot.move.destination.toString());
		}
		
		
		
		
		
		
		
	}

	@Override
	public boolean pre() throws GameActionException {
		return !robot.enemyInMelee && !robot.isNukeArmed;
	}

}
