package lcsgui;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import lcs.Environment;

public class AgentPanel extends JPanel {
	
	/**
	 * Eclipse wants it.
	 */
	private static final long serialVersionUID = 3968553806548551131L;
	
	Environment env;
	
	JComboBox agentCombo;
	
	public AgentPanel(Environment env) {
		super();
		this.env = env;
		
		agentCombo = new JComboBox(env.agents);
	}
	//TODO
}
