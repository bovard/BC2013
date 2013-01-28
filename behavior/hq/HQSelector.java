package team122.behavior.hq;

import team122.behavior.Decision;
import team122.behavior.Node;
import team122.robot.HQ;

public class HQSelector extends Decision {

	protected HQ robot;
	
	public HQSelector(HQ robot) {
		super();
		this.robot = robot;
		
		this.children.add(new HQSpawnSelector(robot));
		this.children.add(new HQSpawnStart(robot));
		
		this.children.get(SPAWNING_SELECTOR_HQ).parent = this;
		this.children.get(SPAWN_START_HQ).parent = this;
	}
	
	@Override
	public Node select() {
		
		//Initializes the basic data (strategy and such).
		
		if (robot.state.inStart) {
			return children.get(SPAWN_START_HQ);
		} else {
			
			System.out.println("Are we here!!!!");
			return children.get(SPAWNING_SELECTOR_HQ);
		}
	}

	@Override
	public boolean pre() {
		return true;
	}

	public static final int SPAWNING_SELECTOR_HQ = 0;
	public static final int SPAWN_START_HQ = 1;
}
