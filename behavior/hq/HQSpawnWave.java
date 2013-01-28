package team122.behavior.hq;

import team122.behavior.Behavior;
import team122.behavior.soldier.SoldierSelector;
import team122.robot.HQ;
import team122.utils.GameStrategy;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;

public class HQSpawnWave extends Behavior {
	
	protected HQ robot;
	protected HQSelector parent;
	protected boolean init;
	protected boolean attackReady = true;
	protected int startRound;
	protected Upgrade[] research;

	public HQSpawnWave(HQ robot) {
		super();
		this.robot = robot;
		init = false;
		research = new Upgrade[5];
		research[0] = Upgrade.FUSION;
		research[1] = Upgrade.PICKAXE;
		research[2] = Upgrade.VISION;
		research[3] = Upgrade.DEFUSION;
		research[4] = Upgrade.NUKE;
	}
	
	
	/**
	 * When we first start we need to calculate the encamper spots.
	 */
	@Override
	public void start() throws GameActionException { 
		if (!init) {
			init = true;
			robot.calculateEncamperSpots();
			if (robot.hqUtils.powerTotalFromLastRound < GameStrategy.WAVE_POWER_THRESHHOLD) {
				attackReady = false;
			}
			startRound = Clock.getRoundNum();
		}
	}
	
	int lastEnconBuild = Clock.getRoundNum();
	
	@Override
	public void run() throws GameActionException {

		System.out.println("!Attack: " + robot.encampmentSorter.generatorEncampments[0]);
		if (!attackReady) {
			if (robot.hqUtils.powerTotalFromLastRound > GameStrategy.WAVE_POWER_THRESHHOLD + 5) {
				attackReady = true;
			}
		}

		System.out.println("Attack: " + robot.encampmentSorter.generatorEncampments[0]);
		if (attackReady && Clock.getRoundNum() % 3 == 0 && robot.hqUtils.powerTotalFromLastRound < GameStrategy.WAVE_POWER_THRESHHOLD) {
			robot.attack();
			attackReady = false;
		}
		
		

		System.out.println("robot.rc.isActive: " + robot.encampmentSorter.generatorEncampments[0]);
		if (robot.rc.isActive() && robot.hqUtils.powerTotalFromLastRound > 25) {
			// if the enemy is with 400 units squared, just make guys
			if (robot.enemyAtBase) {
				robot.spawnScout();
			}
			// research fusion
			else if (Clock.getRoundNum() > GameStrategy.WAVE_FUSION_TURN && !robot.rc.hasUpgrade(Upgrade.FUSION)){
				robot.rc.researchUpgrade(Upgrade.FUSION);
			}
			// research pick axe
			else if (Clock.getRoundNum() > GameStrategy.WAVE_PICKAXE_TURN && !robot.rc.hasUpgrade(Upgrade.PICKAXE)){
				robot.rc.researchUpgrade(Upgrade.PICKAXE);
			}
			// research vision
			else if (Clock.getRoundNum() > GameStrategy.WAVE_VISION_TURN && !robot.rc.hasUpgrade(Upgrade.VISION)){
				robot.rc.researchUpgrade(Upgrade.VISION);
			}
			// spawn an econ building every 100 rounds
			else if (Clock.getRoundNum() - lastEnconBuild > 100 && robot.spawnEconBuilding()) {
				lastEnconBuild = Clock.getRoundNum();
			} 
			// keep amassing miners
			else if (robot.hqUtils.minerCount < Clock.getRoundNum()/300 + 1) {
				robot.spawnMiner();
			} 
			// keep amassing artillery
			else if (robot.hqUtils.artilleryCount < Clock.getRoundNum()/300 + 1) {
				robot.spawnArtillery();
			}
			else {
				robot.spawnScout();
			}
			
			
		} else if (robot.rc.isActive()){
			for (int i = 0; i < research.length; i++){
				if (!robot.rc.hasUpgrade(research[i])) {
					robot.rc.researchUpgrade(research[i]);
					break;
				}
			}
		}
	}

	@Override
	public boolean pre() {
		return robot.state.inSpawnWave && !robot.enemyResearchedNuke;
	}
}