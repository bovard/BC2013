package team122.behavior.soldier;


import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import team122.RobotInformation;
import team122.behavior.Behavior;
import team122.behavior.Decision;
import team122.behavior.Node;
import team122.communication.SoldierDecoder;
import team122.robot.Soldier;

public class SoldierSelector extends Decision {
	public Soldier robot;

	public SoldierSelector(Soldier soldier) {
		this.robot = soldier;
		children.add(new SoldierDefenseMiner(this.robot));
		children.add(new SoldierSwarm(this.robot));
		children.add(new SoldierEncamper(this.robot));
		children.add(new SoldierCombat(this.robot));
		children.add(new SoldierDefender(this.robot));
		children.add(new SoldierNukeDefender(this.robot));
		children.add(new SoldierBackDoor(this.robot));
		children.add(new SoldierEncampHunter(this.robot));
		children.add(new SoldierNukeIsArmed(this.robot));
		children.add(new SoldierHQDefender(this.robot));
		children.get(SOLDIER_MINER).parent = this;
		children.get(SOLDIER_SWARMER).parent = this;
		children.get(SOLDIER_ENCAMPER).parent = this;
		children.get(SOLDIER_COMBAT).parent = this;
		children.get(SOLDIER_DEFENDER).parent = this;
		children.get(SOLDIER_NUKE_DEFENDER).parent = this;
		children.get(SOLDIER_BACK_DOOR).parent = this;
		children.get(SOLDIER_ENCAMP_HUNTER).parent = this;
		children.get(SOLDIER_NUKE_IS_ARMED).parent = this;
		children.get(SOLDIER_HQ_DEFENDER).parent = this;
	}
	
	@Override
	public Node select() throws GameActionException {
		if (robot.isNew) {
			robot.dec = robot.com.receiveNewSoldier();
			robot.isNew = false;
		}
		
		
		if (robot.enemyInMelee) {
			
			return children.get(SOLDIER_COMBAT);
		} else if (robot.isNukeArmed) {
			
			return children.get(SOLDIER_NUKE_IS_ARMED);
		} else if (robot.enemyAtTheGates) {
			
			return children.get(SOLDIER_HQ_DEFENDER);
		}

		return children.get(robot.dec.soldierType);
	}
	
	@Override
	public boolean pre() {
		return true;
	}

	/**
	 * Gets the rally point for a swarmers initial point.
	 * @return
	 */
	public static MapLocation GetInitialRallyPoint(RobotInformation info) {
		int distanceOut = (int)Math.ceil(Math.sqrt(info.enemyHqDistance) / 4);
		System.out.println(distanceOut);
		MapLocation loc = info.hq;
		
		if (info.enemyDir.isDiagonal()) {
			distanceOut = (int) (distanceOut / 1.4);
		}
		
		for (int i = 0; i < distanceOut; i++) {
			loc = loc.add(info.enemyDir);
		}

		return loc;
	}
	
	public static final int SOLDIER_MINER = 0;
	public static final int SOLDIER_SWARMER = 1;
	public static final int SOLDIER_ENCAMPER = 2;
	public static final int SOLDIER_COMBAT = 3;
	public static final int SOLDIER_DEFENDER = 4;
	public static final int SOLDIER_NUKE_DEFENDER = 5;
	public static final int SOLDIER_BACK_DOOR = 6;
	public static final int SOLDIER_ENCAMP_HUNTER = 7;
	public static final int SOLDIER_NUKE_IS_ARMED = 8;
	public static final int SOLDIER_HQ_DEFENDER = 9;
}
