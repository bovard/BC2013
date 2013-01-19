package team122.behavior.lib.supplier;

import team122.behavior.lib.Decision;
import team122.behavior.lib.Node;
import team122.robot.HQ;
import team122.robot.Supplier;

public class SupplierSelector extends Decision {

	protected Supplier robot;
	
	public SupplierSelector(Supplier robot) {
		super();
		this.robot = robot;
		this.children.add(new SupplierIdle(robot));
		this.children.get(IDLE_SUPPLIER).parent = this;
	}
	
	@Override
	public Node select() {
		return children.get(IDLE_SUPPLIER);		
	}

	@Override
	public boolean pre() {
		return true;
	}

	public static final int IDLE_SUPPLIER = 0;
}
