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
		this.children.add(new HQIdle(robot));
		this.children.add(new HQCalculate(robot));
		
		this.children.get(SPAWNING_SELECTOR_HQ).parent = this;
		this.children.get(IDLE_HQ).parent = this;
		this.children.get(CALCULATE_HQ).parent = this;
	}
	
	@Override
	public Node select() {
		
		//Initializes the basic data (strategy and such).
		
		if (!robot.encampmentSorter.finishBaseCalculation) {
			return children.get(CALCULATE_HQ);
		} else if (robot.rc.isActive()) {
			return children.get(SPAWNING_SELECTOR_HQ);
		}
		
		return children.get(IDLE_HQ);		
	}

	@Override
	public boolean pre() {
		return true;
	}

	public static final int SPAWNING_SELECTOR_HQ = 0;
	public static final int IDLE_HQ = 1;
	public static final int CALCULATE_HQ = 2;
}
