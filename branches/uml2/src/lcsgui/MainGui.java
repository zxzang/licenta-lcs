package lcsgui;

import javax.swing.JFrame;

import lcs.Environment;

public class MainGui {
	
	public MainGui(Environment env) {
		JUNGFrame jframe = new JUNGFrame(env);
		jframe.setVisible(true);
		
		JFrame agent = new JFrame();
		AgentPanel aPan = new AgentPanel(env);
		
		agent.setSize(800, 600);
		agent.add(aPan);
		//agent.setVisible(true);
	}
	
	public MainGui(Environment env, String ip) {
		new UBIClient(env, ip);
	}
}
