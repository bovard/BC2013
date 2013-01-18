package team122.trees;

import team122.behavior.lib.Behavior;
import team122.behavior.lib.Decision;
import team122.behavior.lib.Node;

import team122.robot.Robot;


public abstract class Tree {

	public Robot robot;
	public Node current;
	
	/**
	 * This is where you would build up the tree. 
	 */
	public Tree(Robot robot) {
		this.robot = robot;
	}
	
	public void run() {
		boolean newB = true;
		robot.environmentCheck();
		
		while (true) {
			try {
				// TODO: doesn't support a sequence
				// first check the pres and navigate to a Node that pre returns true
				if (newB) {
					if (current instanceof Behavior) {
						((Behavior) current).start();
					}
				}
				if (!current.pre()) {					
					if (current instanceof Behavior) {
						((Behavior)current).stop();
					}
					//cascade back up the tree
					while(!current.pre()) {
						current = current.parent;
					}
				}
				
				// if we are on a behavior Node 
				// if it's new call start
				// call run
				if (current instanceof Behavior) {
					((Behavior)current).run();
				}
				// if we are on a decision Node
				// call select and set the thing it returns to the current, then loop again (continue)
				else {
					current = ((Decision)current).select();
					newB = true;
					continue;
				}
				
				newB = false;
				robot.rc.yield();
				
				// at the start of the round, update with an environment check
				robot.environmentCheck();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
