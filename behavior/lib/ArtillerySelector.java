package team122.behavior.lib;

import team122.robot.Artillery;

public class ArtillerySelector extends Decision {

	protected Artillery robot;
	private static final int IDLE = 0;
	private static final int SHOOT = 1; 
	
	public ArtillerySelector(Artillery robot) {
		super();
		this.robot = robot;
		this.children.add(new ArtilleryIdle(robot));
		this.children.get(IDLE).parent = this;
		this.children.add(new ArtilleryShoot(robot));
		this.children.get(SHOOT).parent = this;
	}
	
	@Override
	public Node select() {
		if (robot.canShoot && robot.enemyNearby) {
			return children.get(SHOOT);
		}
		else {
			return children.get(IDLE);
		}
	}

	@Override
	public boolean pre() {
		return true;
	}

}
