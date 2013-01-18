package team122.trees;

import team122.behavior.lib.SoldierSelector;
import team122.robot.Soldier;

public class SoldierTree extends Tree {

	public SoldierTree(Soldier robot) {
		super(robot);
		this.current = new SoldierSelector(robot);
	}
}
