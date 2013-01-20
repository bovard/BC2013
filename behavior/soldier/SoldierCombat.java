package team122.behavior.soldier;

import team122.behavior.Behavior;
import team122.robot.Soldier;
import battlecode.common.Direction;
import battlecode.common.GameActionException;

public class SoldierCombat extends Behavior{
	protected Soldier robot;
	
	public SoldierCombat(Soldier robot) {
		super();
		this.robot = robot;
	}
	
	public static Direction getDirection(int x, int y) {
		if(x > 0) {
			if (y > 0)
				return Direction.NORTH_EAST;
			if (y == 0)
				return Direction.EAST;
			else
				return Direction.SOUTH_EAST;
		} else if ( x < 0) {
			if (y > 0)
				return Direction.NORTH_WEST;
			if (y == 0)
				return Direction.WEST;
			else
				return Direction.SOUTH_WEST;
		} else {
			if (y > 0)
				return Direction.NORTH;
			else
				return Direction.SOUTH;
		}
	}

	@Override
	public void run() throws GameActionException {
		if (!robot.rc.isActive())
			return;
		
				
	}

	@Override
	public boolean pre() {
		return robot.enemyInMelee;
	}

}
