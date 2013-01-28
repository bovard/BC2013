package team122.utils;

import java.util.ArrayList;
import java.util.Arrays;

import team122.RobotInformation;
import team122.combat.MoveCalculator;
import team122.robot.HQ;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import battlecode.common.TerrainTile;

public class DoNotCapture {

	public boolean determined;
	public boolean setup;
	public MapLocation[] determinedMapLocations;

	private RobotController rc;
	private RobotInformation info;
	private ArrayList<MapLocation> determinedLocations;
	private boolean sensed;
	private MapLocation[] encampments;
	private Direction direction;
	private MapLocation currentLoc;
	private int setupIndex;
	private int count = 0;
	private boolean[][] encampMap;
	private MapLocation hq;
	
	public DoNotCapture(RobotController rc, RobotInformation info) {
		this.rc = rc;
		this.info = info;
		determinedLocations = new ArrayList<MapLocation>();
		direction = info.enemyDir;
		sensed = false;
		currentLoc = info.hq;
		setupIndex = 0;
		encampMap = new boolean[HQ_MIDDLE * 2 + 1][HQ_MIDDLE * 2 + 1];
		hq = info.hq;
		
		determined = false;
		setup = false;
	}
	
	/**
	 * Setup the basic information, the datastructure to search with, ect.
	 * @throws GameActionException
	 */
	public void setupEncampments() throws GameActionException {
		if (setup) {
			return;
		}
		
		if (!sensed) {
			encampments = rc.senseEncampmentSquares(info.hq, ENCAMPMENT_RADIUS, Team.NEUTRAL);
			sensed = true;
		}
		
		//If there are no encapmments then immediately shortcut.
		if (encampments.length == 0) {
			determined = true;
			setup = true;
			determinedMapLocations = new MapLocation[0];
			return;
		}
		

		//Verifies that the bytecodes left are available for using!
		for (; setupIndex < encampments.length && Clock.getBytecodesLeft() > HQ.MINIMUM_BYTECODES_LEFT; setupIndex++) {
			int x = encampments[setupIndex].x - hq.x + HQ_MIDDLE;
			int y = encampments[setupIndex].y - hq.y + HQ_MIDDLE;
			encampMap[y][x] = true;
		}

		if (setupIndex == encampments.length) {
			setup = true;
		}
	}
	
	/**
	 * Determines if there are encampments that need not to be captured.
	 * @throws GameActionException 
	 */
	public void determine() throws GameActionException {
		if (determined) {
			return;
		}
		
		for (;count < DISTANCE_TO_CHECK && Clock.getBytecodesLeft() > HQ.MINIMUM_BYTECODES_LEFT; count++) {

			int sx = currentLoc.x - hq.x + HQ_MIDDLE;
			int sy = currentLoc.y - hq.y + HQ_MIDDLE;
			
			int x = sx + MoveCalculator.directionToXIndex(direction);
			int y = sy + MoveCalculator.directionToYIndex(direction);
			
			
			//Must go left
			if (encampMap[y][x]) {
				int dirX = MoveCalculator.directionToXIndex(direction.rotateLeft());
				int dirY = MoveCalculator.directionToYIndex(direction.rotateLeft());
				x = sx + dirX;
				y = sy + dirY;
				int realX = currentLoc.x + dirX;
				int realY = currentLoc.y + dirY;

				//Ok so there is one left, how about right?
				if (encampMap[y][x] || realX < 0 || realX >= info.width || realY < 0 || realY >= info.height) {

					dirX = MoveCalculator.directionToXIndex(direction.rotateRight());
					dirY = MoveCalculator.directionToYIndex(direction.rotateRight());
					x = sx + dirX;
					y = sy + dirY;
					realX = currentLoc.x + dirX;
					realY = currentLoc.y + dirY;

					if (encampMap[y][x] || realX < 0 || realX >= info.width || realY < 0 || realY >= info.height) {
						currentLoc = currentLoc.add(direction);
						determinedLocations.add(currentLoc);
					} else {
						currentLoc = currentLoc.add(direction.rotateRight());
					}
				} else {

					currentLoc = currentLoc.add(direction.rotateLeft());
				}
			} else {
				currentLoc = currentLoc.add(direction);
			}
		}
		
		if (count == DISTANCE_TO_CHECK) {
			
			determinedMapLocations = determinedLocations.toArray(new MapLocation[determinedLocations.size()]);
			determined = true;
		}
	}
	
	
	
	public static final int ENCAMPMENT_RADIUS = 48;
	public static final int DISTANCE_TO_CHECK = 6;
	public static final int HQ_MIDDLE = 7;
}
