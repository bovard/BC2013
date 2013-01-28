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
	 * 
	 */
	@Override
	public void start() throws GameActionException { 
		if (!init) {
			init = true;
			if (robot.hqUtils.powerTotalFromLastRound < GameStrategy.WAVE_POWER_THRESHHOLD + robot.hqUtils.soldierCount/2) {
				attackReady = false;
			}
			startRound = Clock.getRoundNum();
		}
	}
	
	int lastEnconBuild = Clock.getRoundNum();
	
	@Override
	public void run() throws GameActionException {
		robot.rc.setIndicatorString(0, "WAVE");

		if (!attackReady) {
			if (robot.hqUtils.powerTotalFromLastRound > GameStrategy.WAVE_POWER_THRESHHOLD + 5) {
				attackReady = true;
			}
		}

		// TODO: Michael Add attack based on number of soldiers
		if (attackReady && Clock.getRoundNum() % HQ.HQ_COMMUNICATION_ROUND == 0 && robot.hqUtils.powerTotalFromLastRound < GameStrategy.WAVE_POWER_THRESHHOLD) {
			robot.attack();
			attackReady = false;
		}
		
		
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
			// TODO: michael
			// research defusion if we ever sense more than X enemy mines on the map
			// TODO: Michael
			// make this change based on distance between hq, the greater the distance
			// the more frequent we spawn
			// spawn an econ building every WAVE_ECON_BUILD_COOLDOWN rounds
			else if (Clock.getRoundNum() - lastEnconBuild > GameStrategy.WAVE_ECON_BUILD_COOLDOWN && robot.spawnEconBuilding()) {
				lastEnconBuild = Clock.getRoundNum();
			} 
			// keep amassing miners
			else if (robot.hqUtils.minerCount < Clock.getRoundNum()/GameStrategy.WAVE_MINER_COOLDOWN + 1) {
				robot.spawnMiner();
			} 
			// keep amassing artillery
			else if (robot.hqUtils.artilleryCount < Clock.getRoundNum()/GameStrategy.WAVE_ARTILLERY_COOLDOWN + 1) {
				robot.spawnArtillery();
			}
			// TODO: Michael
			// get some backdoor soldiers spawning
			// get some encampment hunters spawning
			// if we have PICKAXE get some corner miners
			
			
			// otherwise get a lot of scouts out there!
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
		return robot.state.inSpawnWave && !robot.enemyResearchedNuke && (Clock.getRoundNum() - robot.enemyHPChangedRound < GameStrategy.SWITCH_TO_ECON);
	}
}