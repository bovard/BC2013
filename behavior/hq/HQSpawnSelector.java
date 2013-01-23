package team122.behavior.hq;

import team122.behavior.Decision;
import team122.behavior.Node;
import team122.robot.HQ;
import battlecode.common.GameActionException;

public class HQSpawnSelector extends Decision {
	
	protected HQ robot;

	public HQSpawnSelector(HQ robot) {
		super();
		this.robot = robot;

		this.children.add(new HQSpawnRush(robot));
		this.children.add(new HQSpawnDarkHorse(robot));
		this.children.add(new HQSpawnForceResearchNuke(robot));
		this.children.add(new HQSpawnVsNukeBot(robot));
		this.children.add(new HQSpawnVsNukeBotMiner(robot));
		this.children.add(new HQSpawnVsNukeBotMinerPickax(robot));
		this.children.get(HQ_SPAWN_RUSH).parent = this;
		this.children.get(HQ_SPAWN_DARK_HORSE).parent = this;
		this.children.get(HQ_SPAWN_FORCE_RESEARCH_NUKE).parent = this;
		this.children.get(HQ_SPAWN_VS_NUKE_BOT).parent = this;
		this.children.get(HQ_SPAWN_VS_NUKE_BOT_AND_MINER).parent = this;
		this.children.get(HQ_SPAWN_VS_NUKE_BOT_MINER_PICKAX).parent = this;
	}

	@Override
	public Node select() throws GameActionException {
		if (robot.forceNukeRush) {
			return this.children.get(HQ_SPAWN_FORCE_RESEARCH_NUKE);
		}
		
		
		if (robot.rush) {
			return this.children.get(HQ_SPAWN_RUSH);
		} else if (robot.darkHorse) {
			return this.children.get(HQ_SPAWN_DARK_HORSE);
		} else if (robot.vsNukeBot) {
			return this.children.get(HQ_SPAWN_VS_NUKE_BOT);
		} else if (robot.vsNukeBotAndMiner) {
			return this.children.get(HQ_SPAWN_VS_NUKE_BOT_AND_MINER);
		} else if (robot.vsNukeBotAndMinerPickax) {
			return this.children.get(HQ_SPAWN_VS_NUKE_BOT_MINER_PICKAX);
		}

		//defaults to rush.
		robot.rush = true;
		robot.darkHorse = false;
		return this.children.get(HQ_SPAWN_RUSH);
	}
	
	@Override
	public boolean pre() {
		return robot.rc.isActive();
	}

	public static final int HQ_SPAWN_RUSH = 0;
	public static final int HQ_SPAWN_DARK_HORSE = 1;
	public static final int HQ_SPAWN_FORCE_RESEARCH_NUKE = 2;
	public static final int HQ_SPAWN_VS_NUKE_BOT = 3;
	public static final int HQ_SPAWN_VS_NUKE_BOT_AND_MINER = 4;
	public static final int HQ_SPAWN_VS_NUKE_BOT_MINER_PICKAX = 5;
}
