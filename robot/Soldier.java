package team122.robot;

import battlecode.common.RobotController;
import team122.RobotInformation;
import team122.navigation.NavigationMode;
import team122.navigation.NavigationSystem;
import team122.trees.SoldierTree;

public class Soldier extends Robot {
	
	public NavigationSystem navSystem = null;
	public NavigationMode navMode = null;
	
	public Soldier(RobotController rc, RobotInformation info) {
		super(rc, info);
		navSystem = new NavigationSystem(rc, info);
		navMode = navSystem.navMode;		
		this.tree = new SoldierTree(this);
	}

	@Override
	public void environmentCheck() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		tree.run();
	}
	
	

}
