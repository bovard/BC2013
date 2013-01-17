package team122.behavior.lib;

public abstract class Behavior extends Node{
	

	/**
	 * Called when starting a behavior. There may be one-time things you will have
	 * to do such as computing a path.
	 */
	public abstract void start();
	
	/**
	 * Called when stopping a behavior. There may be one-time things you want to do 
	 * when exiting
	 */
	public abstract void stop();


}
