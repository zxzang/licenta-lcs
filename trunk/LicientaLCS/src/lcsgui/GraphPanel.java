package lcsgui;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import lcs.Edge;
import lcs.Environment;
import lcs.EnvironmentFeedback;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * A GUI for the {@link Environment} type.
 */
public class GraphPanel extends JPanel implements KeyListener,
		EnvironmentFeedback {
	/**
	 * Serial Version UID.
	 * Eclipse wouldn't shut up about it.
	 */
	private static final long serialVersionUID = 4323232456487666646L;
	
	/**
	 * The {@link Environment} to be displayed.
	 */
	private Environment env;
	
	/**
	 * The width of the panel.
	 */
	static final int WIDTH = 800;
	
	/**
	 * The height of the panel.
	 */
	static final int HEIGHT = 600;
	
	private Mode mouseType = Mode.PICKING;
	
	/**
	 * The mouse handler.
	 */
	@SuppressWarnings("rawtypes")
	DefaultModalGraphMouse gm;
	
	/**
	 * The {@link VisualizationViewer} the panel uses.
	 */
	private VisualizationViewer<lcs.Position, Edge> vv;
	
	/**
	 * Constructor for the class.
	 * @param e - the {@link Environment} to be displayed.
	 */
	@SuppressWarnings("rawtypes")
	public GraphPanel(final Environment e) {
		this.env = e;
		
		Layout<lcs.Position, Edge> layout =
			new FRLayout2<lcs.Position, Edge>(this.env.getGraph());
		layout.setSize(new Dimension(WIDTH, HEIGHT));
		
		vv = new VisualizationViewer<lcs.Position, Edge>(layout);
		
		vv.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<lcs.Position>());
		vv.getRenderContext().setVertexFillPaintTransformer(new PositionTrans());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		gm = new DefaultModalGraphMouse();
		gm.setMode(mouseType);
		vv.setGraphMouse(gm);
		
		vv.addKeyListener(this);
		this.add(vv);
		this.addKeyListener(this);
		
		this.setBounds(0, 0, WIDTH, HEIGHT);
		this.setVisible(true);
		
		env.addToFeedback(this);
	}
	
	private void changeMouseType() {
		if (mouseType == Mode.PICKING) {
			mouseType = Mode.TRANSFORMING;
		} else {
			mouseType = Mode.PICKING;
		}
		gm.setMode(mouseType);
	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {
		char rel = e.getKeyChar();
		switch (rel) {
		case 'm':
		case 'M':
			changeMouseType();
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void update() {
		vv.repaint();
	}

	@Override
	public void update(lcs.Position src, lcs.Position dst) {
		vv.repaint();
	}

	@Override
	public void change() {
		vv.repaint();
	}
}
