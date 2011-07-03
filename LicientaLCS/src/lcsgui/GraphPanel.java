package lcsgui;

import java.awt.Dimension;

import javax.swing.JPanel;

import lcs.Edge;
import lcs.Environment;
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
public class GraphPanel extends JPanel {
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
	
	/**
	 * Constructor for the class.
	 * @param e - the {@link Environment} to be displayed.
	 */
	public GraphPanel(final Environment e) {
		this.env = e;
		
		Layout<lcs.Position, Edge> layout =
			new FRLayout2<lcs.Position, Edge>(this.env.getGraph());
		layout.setSize(new Dimension(WIDTH, HEIGHT));
		
		VisualizationViewer<lcs.Position, Edge> vv = new
			VisualizationViewer<lcs.Position, Edge>(layout);
		vv.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<lcs.Position>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		@SuppressWarnings("rawtypes")
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(Mode.PICKING);
		vv.setGraphMouse(gm);
		
		this.add(vv);
		
		this.setBounds(0, 0, WIDTH, HEIGHT);
		this.setVisible(true);
	}
	
	// TODO add mouse listeners
	// feature to click a node and get info on it

}
