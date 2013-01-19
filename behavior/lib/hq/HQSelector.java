package team122.behavior.lib.hq;

import team122.behavior.lib.Decision;
import team122.behavior.lib.Node;
import team122.robot.HQ;

public class HQSelector extends Decision {

	protected HQ robot;
	protected HQCalculate calc;
	
	public HQSelector(HQ robot) {
		super();
		this.robot = robot;
		calc = new HQCalculate(robot);
		
		this.children.add(new HQSpawn(robot));
		this.children.add(new HQIdle(robot));
		this.children.add(calc);
		
		this.children.get(SPAWNING_HQ).parent = this;
		this.children.get(IDLE_HQ).parent = this;
		this.children.get(CALCULATE_HQ).parent = this;
	}
	
	@Override
	public Node select() {
		
		//Initializes the basic data (strategy and such).
		if (calc.calculating) {
			return calc;
		}
		
		if (robot.rc.isActive()) {
			return children.get(SPAWNING_HQ);
		}
		
		return children.get(IDLE_HQ);		
	}

	@Override
	public boolean pre() {
		return true;
	}

	public static final int SPAWNING_HQ = 0;
	public static final int IDLE_HQ = 1;
	public static final int CALCULATE_HQ = 2;
}
