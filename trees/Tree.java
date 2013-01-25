package team122.trees;

import team122.behavior.Behavior;
import team122.behavior.Decision;
import team122.behavior.Node;

import team122.robot.TeamRobot;


public abstract class Tree {

	public TeamRobot robot;
	public Node current;
	
	/**
	 * This is where you would build up the tree. 
	 */
	public Tree(TeamRobot robot) {
		this.robot = robot;
	}
	
	public void run() {
		boolean newB = true;
		while (true) {
			try {
				// at the start of the round, update with an environment check
				robot.environmentCheck();
				
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
				
				
			} catch (Exception e) {
				e.printStackTrace();	
			}

			
			robot.load();
			robot.rc.yield();
			robot.rc.setIndicatorString(0, "-");
		}
	}
}
