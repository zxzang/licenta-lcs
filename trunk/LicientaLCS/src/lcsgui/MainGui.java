package lcsgui;

import javax.swing.JFrame;

import lcs.Environment;

public class MainGui {
	
	protected final static int timeMs = 1000;
	
	private RewardPanel rewardPan;
	private JFrame rewardFrame;

	private void initRewFrame(Environment env) {
		rewardPan = new RewardPanel(env);
		rewardFrame = new JFrame("Reward");
		
		rewardFrame.add(rewardPan);
		
		rewardFrame.setSize(rewardPan.getSize());
		rewardFrame.addWindowListener(new GraphWindowCloser());
		rewardFrame.setVisible(true);
	}
	
	public MainGui(Environment env) {
		JUNGFrame jframe = new JUNGFrame(env);
		jframe.setVisible(true);
		initRewFrame(env);
	}
	
	public MainGui(Environment env, String ip) {
		new UBIClient(env, ip);
		initRewFrame(env);
	}
}
