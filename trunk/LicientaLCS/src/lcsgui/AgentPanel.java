package lcsgui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import lcs.Environment;
import lcs.Robot;

public class AgentPanel extends JPanel {
	
	/**
	 * Eclipse wants it.
	 */
	private static final long serialVersionUID = 3968553806548551131L;
	
	Environment env;
	
	JComboBox agentCombo;
	
	private void initCombo() {
		agentCombo = new JComboBox(env.agents);
		
		agentCombo.setEditable(false);
		agentCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				agentComboAction(e);
			}
		});
		
		add(agentCombo);
	}
	
	public AgentPanel(Environment env) {
		super();
		this.env = env;
		initCombo();
	}
	
	protected void agentComboAction(ActionEvent e) {
		Robot r = (Robot) agentCombo.getSelectedItem();
	}
}
