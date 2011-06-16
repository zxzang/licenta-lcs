package lcsgui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lcs.Environment;
import lcs.Position;

public class RewardPanel extends JPanel {

	/**
	 * Eclipse wants it.
	 */
	private static final long serialVersionUID = 8213435715847065200L;
	
	class Field extends JLabel {
		/**
		 * Eclipse again.
		 */
		private static final long serialVersionUID = 1817060792260992613L;
		Position pos;
		
		public Field(Position pos) {
			super(pos.getName() + " " + pos.getFeedback());
			this.pos = pos;
		}

		public void update() {
			this.setText(pos.getName() + " " + pos.getFeedback());
		}
	}
	
	private Field info[];
	
	public RewardPanel(Environment env) {
		super();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		info = new Field[env.getGraph().getVertexCount()];
		
		int i = 0;
		for (Position pos : env.getGraph().getVertices()) {
			info[i] = new Field(pos);
			this.add(info[i++]);
		}
		
		this.setSize(300, info.length*30);
	}
	
	public void update() {
		for (Field f : info) {
			f.update();
		}
	}
	
}
