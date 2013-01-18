package team122.behavior.lib;

import java.util.ArrayList;
import java.util.HashMap;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import team122.robot.Soldier;

public class SoldierEncamper extends Behavior {
	
	public Soldier robot;
	public HashMap<MapLocation, Boolean> alliedEncampment;
	public ArrayList<MapLocation> nearHQ;
	public boolean encamp = true;
	public RobotType encampmentType;
	public boolean capturingNearHQ = false;
	public int encampmentToTry = 0;
	
	public SoldierEncamper(Soldier robot) {
		this.robot = robot;
		alliedEncampment = new HashMap<MapLocation, Boolean>();
		nearHQ = new ArrayList<MapLocation>();
	}
	
	/**
	 * When we start we determine if there is any allied encampments
	 */
	public void start() throws GameActionException {
		MapLocation[] allies = robot.rc.senseAlliedEncampmentSquares();

		for (int i = 0, len = allies.length; i < len; i++) {
			if (!alliedEncampment.containsKey(allies[i])) {
				alliedEncampment.put(allies[i], true);
			}
		}
		
		//now we determine what encampment to get.
		MapLocation[] encamps = robot.info.encampments = robot.rc.senseAllEncampmentSquares();
		
		if (robot.info.encampments.length > 0 && 
			alliedEncampment.size() < robot.info.encampments.length / 2) {

			//Some importance are encampments that are between us and our enemy.
			int near = 64;
			for (int i = 0, len = encamps.length; i < len; i++) {
				if (encamps[i].distanceSquaredTo(robot.info.hq) <= near) {
					nearHQ.add(encamps[i]);
				}
			}
			
			_setDestination();
		} else {
			_setDestination();
		}
	}

	@Override
	public void run() throws GameActionException {
		if (robot.rc.isActive()) {
			//Moves closer to the destination if it has one.
			if (robot.navSystem.navMode.hasDestination) {
				
				if (robot.navSystem.navMode.attemptsExausted()) {
					_setDestination();
				}
				robot.navSystem.navMode.move();
				
			//It is at the destination.
			} else if (robot.navSystem.navMode.atDestination) {
				
				//Capture encampment.
				if (robot.navSystem.navMode.destination.equals(robot.rc.getLocation())) {
					robot.rc.captureEncampment(encampmentType);
					
				//Else it cannot capture encampment, someone is there.
				} else {
					
					_setDestination();
				}
			}
		}
	}

	@Override
	public boolean pre() {
		
		System.out.println("Encamper: " + encamp + " :: " + encampmentToTry + " :: " + robot.info.encampments.length);
		return encamp;
	}
	
	/**
	 * Sets the destination of this encampment.
	 */
	private void _setDestination() {
		if (nearHQ.size() > 0) {
			robot.navSystem.navMode.setDestination(nearHQ.remove(0));
			capturingNearHQ = true;
			encampmentType = RobotType.GENERATOR;
			
		} else if (encampmentToTry < robot.info.encampments.length) {
			//DO NOT KNOW WHAT TO DO...
			encampmentType = rand.nextInt() % 3 == 0 ? RobotType.GENERATOR : RobotType.GENERATOR;
			robot.navSystem.navMode.setDestination(robot.info.encampments[encampmentToTry++]);
		} else {
			encamp = false;
		}
	}
}
