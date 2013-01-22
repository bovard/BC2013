package team122.navigation;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Team;
import team122.robot.Soldier;

public class SoldierMove {

	protected Soldier robot;
	public MapLocation destination;
	
	public SoldierMove(Soldier robot) {
		this.robot = robot;
	}

	public void move() throws GameActionException {
		if (!robot.rc.isActive())
			return;
			
		Direction toMove = robot.currentLoc.directionTo(destination);
		
		if (toMove == Direction.NONE || toMove == Direction.OMNI)
			return;
		
		MapLocation ahead, left, right;
		ahead = robot.currentLoc.add(toMove);
		left = robot.currentLoc.add(toMove.rotateLeft());
		right = robot.currentLoc.add(toMove.rotateRight());

		
		Team mineAheadTeam, mineLeftTeam, mineRightTeam, mineHereTeam;
		mineAheadTeam = robot.rc.senseMine(ahead);
		mineLeftTeam = robot.rc.senseMine(left);
		mineRightTeam = robot.rc.senseMine(right);
		mineHereTeam = robot.rc.senseMine(robot.currentLoc);
		
		if (mineHereTeam == robot.info.enemyTeam || mineHereTeam == Team.NEUTRAL) {
			// run away!
			// get off the mine square!
		}
		
		// if there are no mines just try moving somewhere
		else if (mineAheadTeam == null && mineLeftTeam == null && mineRightTeam == null) {
			if (robot.rc.canMove(toMove)) {
				robot.rc.move(toMove);
			} else if (robot.rc.canMove(toMove.rotateLeft())) {
				robot.rc.move(toMove.rotateLeft());
			} else if (robot.rc.canMove(toMove.rotateRight())) {
				robot.rc.move(toMove.rotateRight());
			}
		}
		
				
		// defuse enemy mines if we see them
		else if (mineAheadTeam == robot.info.enemyTeam) {
			robot.rc.defuseMine(ahead);
		} else if (mineLeftTeam == robot.info.enemyTeam) {
			robot.rc.defuseMine(left);
		} else if (mineRightTeam == robot.info.enemyTeam) {
			robot.rc.defuseMine(right);
		}
		
		// try to move to a spot with no neutral mines
		else if (mineAheadTeam != Team.NEUTRAL && robot.rc.canMove(toMove)) {
			robot.rc.move(toMove);
		}
		else if (mineLeftTeam != Team.NEUTRAL && robot.rc.canMove(toMove.rotateLeft())) {
			robot.rc.move(toMove.rotateLeft());
		}
		else if (mineRightTeam != Team.NEUTRAL && robot.rc.canMove(toMove.rotateRight())) {
			robot.rc.move(toMove.rotateRight());
		}
		
		// can't move, just try defusing
		else if (mineAheadTeam == Team.NEUTRAL) {
			robot.rc.defuseMine(ahead);
		}
		else if (mineRightTeam == Team.NEUTRAL) {
			robot.rc.defuseMine(right);
		}
		else if (mineLeftTeam == Team.NEUTRAL) {
			robot.rc.defuseMine(left);
		}
		
			
		// still can't move?  Lets just add intentional error move to get somewhere at least

		int tries = 0;
		while (tries < 10 && robot.rc.isActive()) {
			Direction dir = Direction.values()[(int) (Math.random() * 8)];
			
			if (robot.rc.canMove(dir)) {
				robot.rc.move(dir);
			}
			tries++;
		}
	}
	
}
