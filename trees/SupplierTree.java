package team122.trees;

import team122.behavior.lib.supplier.SupplierSelector;
import team122.robot.Supplier;

public class SupplierTree extends Tree {

	public SupplierTree(Supplier robot) {
		super(robot);
		current = new SupplierSelector(robot);
	}

}
