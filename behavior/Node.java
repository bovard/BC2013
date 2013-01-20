package team122.behavior;

import java.util.ArrayList;

import team122.robot.TeamRobot;

/**
 * The base Node.
 * Most every node has children and a parent
 * Every Node can be run and has pre-conditions
 */
public abstract class Node {
	public ArrayList<Node> children;
	public Node parent;
	
	public Node() {
		children = new ArrayList<Node>();
	}
	
	/**
	 * pre checks if the preconditions are met for a behavior to occur
	 * 
	 * For example: A precondition for mining or de-mining is that you aren't in 
	 * firing range of an enemy (you might see one though!)
	 * 
	 * Note: this should only do specialized checks, most state information should 
	 * be computed before pre/post is called
	 * 
	 * @return If the conditions are met for the behavior. If they aren't call the parent!
	 */
	public abstract boolean pre();
	
	
}
