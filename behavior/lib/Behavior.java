package team122.behavior.lib;
import battlecode.common.GameActionException;


public abstract class Behavior extends Node{
	


	/**
	 * Called when starting a behavior. There may be one-time things you will have
	 * to do such as computing a path. Default to empty
	 */
	public void start() {
		
	}
	
	/**
	 * Called when stopping a behavior. There may be one-time things you want to do 
	 * when exiting. Default to empty
	 */
	public void stop() {
		
	}

	/**
	 * Executes the node
	 */
	public abstract void run() throws GameActionException;
}
