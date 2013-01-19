package team122.behavior.gen;
import team122.behavior.Decision;
import team122.behavior.Node;
import team122.robot.Generator;

public class GeneratorSelector extends Decision {

	protected Generator robot;
	
	public GeneratorSelector(Generator robot) {
		super();
		this.robot = robot;
		this.children.add(new GeneratorIdle(robot));
		this.children.get(IDLE).parent = this;
	}
	
	@Override
	public Node select() {
		return children.get(IDLE);
	}

	@Override
	public boolean pre() {
		return true;
	}
	private static final int IDLE = 0; 

}
