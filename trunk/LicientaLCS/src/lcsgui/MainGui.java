package lcsgui;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import lcs.EnvironmentFeedback;
import lcs.Environment;
import lcs.Position;

public class MainGui implements EnvironmentFeedback {
	
	private Environment env;
	
	private JFrame frame;
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
	
	private void initFrame() {
		graphPanel = new GraphPanel(env);
		rewardPan = new RewardPanel(env);
		frame = new JFrame("LCS");
		
		frame.getContentPane().setLayout(new
				BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.add(graphPanel);
		frame.add(rewardPan);
		
		frame.addWindowListener(new GraphWindowCloser());
		frame.setSize(WIDTH, HEIGHT);
		frame.setVisible(true);
	}
	
	public MainGui(Environment env) {
		this.env = env;
		initFrame();
		env.addToFeedback(this);
	}

}
