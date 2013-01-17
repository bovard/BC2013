package team122.behavior.lib;

import team122.robot.Soldier;

public class SoldierSelector extends Decision{
	public Soldier robot;

	public SoldierSelector(Soldier soldier) {
		this.robot = soldier;
		children.add(new SoldierSwarm(this.robot));
		children.get(0).parent = this;
	}
	
	@Override
	public Node select() {
		// TODO: when we have more than one child the decision code should be in here
		
		return children.get(0);
	}

	@Override
	public boolean pre() {
		return true;
	}

}
