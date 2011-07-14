package lcsgui;

import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lcs.Environment;
import lcs.EnvironmentFeedback;
import lcs.Position;

public class RewardPanel extends JPanel implements EnvironmentFeedback {

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
	
	private HashMap<Position, Field> info;
	
	private Environment env;
	
	public RewardPanel(Environment env) {
		super();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.env = env;
		info = new HashMap<Position, RewardPanel.Field>(env.getGraph().getVertexCount());
		
		for (Position pos : env.getGraph().getVertices()) {
			Field f = new Field(pos);
			info.put(pos, f);
			this.add(f);
		}
		
		this.setSize(300, info.size()*30);
		env.addToFeedback(this);
	}
	
	public void update() {
		for (Position pos : env.getGraph().getVertices()) {
			info.get(pos).update();
		}
	}
	
	public void update(Position pos) {
		info.get(pos).update();
	}

	@Override
	public void update(Position src, Position dst) {
		update();
	}

	@Override
	public void change() {
	}

	@Override
	public void clear(Position pos) {
	}
	
}
