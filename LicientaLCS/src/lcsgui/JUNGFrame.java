package lcsgui;

import javax.swing.JFrame;

import lcs.Environment;

public class JUNGFrame extends JFrame {

	/**
	 * Oh Eclipse you never stop.
	 */
	private static final long serialVersionUID = 4158616077698044493L;

	public Environment env;
	
	private GraphPanel graphPanel;
	
	/**
	 * The width of the frame.
	 */
	static final int WIDTH = 1024;
	
	/**
	 * The height of the frame.
	 */
	static final int HEIGHT = 800;
	
	private void initFrame() {
		graphPanel = new GraphPanel(env);
		
		add(graphPanel);
		
		addWindowListener(new GraphWindowCloser());
		setSize(WIDTH, HEIGHT);
	}
	
	public JUNGFrame(Environment env) {
		super("LCS");
		this.env = env;
		initFrame();
	}
	
}
