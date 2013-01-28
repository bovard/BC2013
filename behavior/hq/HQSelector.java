package team122.behavior.hq;

import team122.behavior.Decision;
import team122.behavior.Node;
import team122.robot.HQ;

public class HQSelector extends Decision {

	protected HQ robot;
	protected HQSpawnStart start;
	
	public HQSelector(HQ robot) {
		super();
		this.robot = robot;
		
		start = new HQSpawnStart(robot);
		this.children.add(new HQSpawnSelector(robot));
		this.children.add(start);
		
		this.children.get(SPAWNING_SELECTOR_HQ).parent = this;
		this.children.get(OPENING_STRATEGY_HQ).parent = this;
	}
	
	@Override
	public Node select() {
		
		//Initializes the basic data (strategy and such).
		
		if (!start.done) {
			return children.get(OPENING_STRATEGY_HQ);
		} else {
			return children.get(SPAWNING_SELECTOR_HQ);
		}	
	}

	@Override
	public boolean pre() {
		return true;
	}

	public static final int SPAWNING_SELECTOR_HQ = 0;
	public static final int OPENING_STRATEGY_HQ = 1;
}
