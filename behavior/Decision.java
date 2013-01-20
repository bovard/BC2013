package team122.behavior;

import battlecode.common.GameActionException;

public abstract class Decision extends Node {

	/**
	 * This will choose the appropriate behavior to run
	 * In a selector it will choose a random function.
	 * In a sequence it will choose the one in sequence.
	 * @return the Node to run
	 */
	public abstract Node select() throws GameActionException;
}
