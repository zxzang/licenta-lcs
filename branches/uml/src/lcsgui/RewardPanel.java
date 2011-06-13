package lcsgui;

import javax.swing.JPanel;
import javax.swing.JTextField;

import lcs.Environment;
import lcs.Position;

/**
 * @author  Jay
 */
public class RewardPanel extends JPanel {

	/**
	 * Eclipse wants it.
	 */
	private static final long serialVersionUID = 8213435715847065200L;
	
	/**
	 * @author  Jay
	 */
	class Field extends JTextField {
		/**
		 * Eclipse again.
		 */
		private static final long serialVersionUID = 1817060792260992613L;
		/**
		 * @uml.property  name="pos"
		 * @uml.associationEnd  
		 */
		Position pos;
		
		public Field(Position pos) {
			super(pos.getName() + " " + pos.getFeedback());
			this.pos = pos;
		}

		public void update() {
			this.setText(pos.getName() + " " + pos.getFeedback());
		}
	}
	
	/**
	 * @uml.property  name="info"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private Field info[];
	
	public RewardPanel(Environment env) {
		super();
		
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
