package team122.trees;

import team122.behavior.lib.Node;

public abstract class Tree {

	public Node current;
	
	/**
	 * This is where you would build up the tree. 
	 */
	public Tree() {
		//example Solider
		//base = new SoldierSelector();
		//base.child.append(MiningBehavior());
		//base.child.append(new ScoutingBehavior();
	}
	
	public boolean run() {
		// check the pres of the current Node, if it fails call stop, fallback
		// keep falling back until we find a node that works
		
		// if we are on a new behavior call run
		
		
		// run the behavior
		current.run();
		
		
		return true;
	}
}
