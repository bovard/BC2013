package team122.trees;

import team122.behavior.lib.ArtillerySelector;
import team122.robot.Artillery;

public class ArtilleryTree extends Tree {

	public ArtilleryTree(Artillery robot) {
		super(robot);
		current = new ArtillerySelector(robot);
	}

}
