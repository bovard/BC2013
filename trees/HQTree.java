package team122.trees;

import team122.behavior.lib.hq.HQSelector;
import team122.robot.HQ;

public class HQTree extends Tree {

	public HQTree(HQ robot) {
		super(robot);
		current = new HQSelector(robot);
	}
}
