package lcsgui;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import lcs.Environment;
import lcs.EnvironmentFeedback;
import lcs.Position;

public class JUNGFrame extends JFrame implements EnvironmentFeedback {

	/**
	 * Oh Eclipse you never stop.
	 */
	private static final long serialVersionUID = 4158616077698044493L;

	public Environment env;
	
	private GraphPanel graphPanel;
	private RewardPanel rewardPan;
	
	/**
	 * The width of the frame.
	 */
	static final int WIDTH = 1024;
	
	/**
	 * The height of the frame.
	 */
	static final int HEIGHT = 800;

	@Override
	public void update() {
		rewardPan.update();
	}

	@Override
	public void update(Position src, Position dst) {
		rewardPan.update();
	}
	 
	@Override
	public void change() {
		// TODO Auto-generated method stub
		
	}

	private void initFrame() {
		graphPanel = new GraphPanel(env);
		rewardPan = new RewardPanel(env);
		
		getContentPane().setLayout(new
				BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		add(graphPanel);
		add(rewardPan);
		
		addWindowListener(new GraphWindowCloser());
		setSize(WIDTH, HEIGHT);
	}
	
	public JUNGFrame(Environment env) {
		super("LCS");
		this.env = env;
		initFrame();
		env.addToFeedback(this);
	}

}
