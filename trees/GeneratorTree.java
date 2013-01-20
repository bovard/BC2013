package team122.trees;

import team122.behavior.gen.GeneratorSelector;
import team122.robot.Generator;

public class GeneratorTree extends Tree {

	public GeneratorTree(Generator robot) {
		super(robot);
		current = new GeneratorSelector(robot);
	}

}
