package team122.behavioral;

import team122.RobotInformation;
import team122.behavior.lib.SoldierSelector;
import team122.communication.Communicator;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Upgrade;

public class HQBasicBehavior extends Behavior {
	
	int spawnCount = 0;
	int miners = 0;
	int encamps = 0;
	int swarm = 0;
	Communicator com;
	int generatorCounts = 0;
	
	public HQBasicBehavior(RobotController rc, RobotInformation info) {
		super(rc, info);
		com = new Communicator(rc, info);
	}
	
	/**
	 * This will be the behavioral loop for the robots life.
	 */
	public void behave() {
		while (true) {
			try {
				if (rc.isActive()) {
					_counts();
					
					if (miners < 2) {
						_spawn(SoldierSelector.SOLDIER_MINER);
						miners++;
					} else if (!rc.hasUpgrade(Upgrade.PICKAXE)) {
						rc.researchUpgrade(Upgrade.PICKAXE);
					} else if (encamps < 5) {
						
						System.out.println("Spawning: " + encamps + " : " + generatorCounts);
						if (encamps == generatorCounts) {
							_spawn(SoldierSelector.SOLDIER_ENCAMPER);
							encamps++;
						} else {
							if (!rc.hasUpgrade(Upgrade.VISION)) {
								rc.researchUpgrade(Upgrade.VISION);
							}
						}
					} else {
						_spawn(SoldierSelector.SOLDIER_SWARMER);
					}
				}			
				
				rc.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void _spawn(int type) throws GameActionException {

		int tries = 0;
		while (tries < 8) {
			Direction dir = Direction.values()[(int)(Math.random() * 8)];
			if (rc.canMove(dir)) {
				rc.spawn(dir);	
				com.communicate(Communicator.CHANNEL_NEW_SOLDIER_MODE, type);
				break;
			}
		}
	}
	
	/**
	 * Getse the counts.
	 */
	private void _counts() throws GameActionException {
		//Stores this rounds counts
		generatorCounts = com.receive(Communicator.CHANNEL_GENERATOR_COUNT, 0);
		
		//Erases so counts will be accurate.
		com.communicate(Communicator.CHANNEL_GENERATOR_COUNT, 0);
	}
}
