package lcsgui;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.JPanel;

import lcs.Edge;
import lcs.Environment;
import lcs.EnvironmentFeedback;
import lcs.Position;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

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
	
	/*private class PositionPair {
		public Position src;
		public Position dst;
		
		public PositionPair(Position src, Position dst) {
			this.src = src;
			this.dst = dst;
		}
	}*/
	
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
	 * Transformer for the coloring of the positions.
	 */
	private PositionTrans posTrans;
	
	private LinkedList<Edge> toUpdate;
	
	/**
	 * Constructor for the class.
	 * @param e - the {@link Environment} to be displayed.
	 */
	@SuppressWarnings("rawtypes")
	public GraphPanel(final Environment e) {
		this.env = e;
		
		Layout<lcs.Position, Edge> layout =
			new FRLayout2<Position, Edge>(this.env.getGraph());
		layout.setSize(new Dimension(WIDTH, HEIGHT));
		
		vv = new VisualizationViewer<Position, Edge>(layout);
		
		vv.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<lcs.Position>());
		vv.getRenderContext().setVertexFillPaintTransformer(posTrans = new PositionTrans());
		vv.getRenderer().getVertexLabelRenderer().setPosition(
				edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position.CNTR);
		
		vv.getRenderContext().setEdgeStrokeTransformer(new EdgeStroker());
		
		gm = new DefaultModalGraphMouse();
		gm.setMode(mouseType);
		vv.setGraphMouse(gm);
		
		vv.addKeyListener(this);
		this.add(vv);
		this.addKeyListener(this);
		
		this.setBounds(0, 0, WIDTH, HEIGHT);
		this.setVisible(true);
		
		toUpdate = new LinkedList<Edge>();
		
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
	public void update(Position src, Position dst) {
		toUpdate.add(env.getGraph().findEdge(src, dst));
	}
	
	@Override
	public void change() {
		int timeMs = MainGui.timeMs;
		Object o = new Object();
		for (Edge e : toUpdate) {
			e.userVar = o;
		}
		posTrans.active = false;
		vv.repaint();
		
		try {
			Thread.sleep(timeMs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (Edge e : toUpdate) {
			e.userVar = null;
		}
		
		toUpdate.clear();
		posTrans.active = true;
		vv.repaint();
	}

	@Override
	public void clear(Position pos) {
	}
	
}
