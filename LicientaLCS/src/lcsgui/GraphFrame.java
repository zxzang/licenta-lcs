package lcsgui;

import java.awt.Dimension;

import javax.swing.JFrame;

import lcs.Edge;
import lcs.Environment;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class GraphFrame extends JFrame{
	/**
	 * Serial Version UID.
	 * Eclipse wouldn't shut up about it.
	 */
	private static final long serialVersionUID = 4323232456487666646L;
	
	private Environment env;
	static final int width = 800;
	static final int height = 600;
	
	public GraphFrame(Environment e) {
		super("LCS System");
		this.env = e;
		
		Layout<lcs.Position, Edge> layout =
			new ISOMLayout<lcs.Position, Edge>(this.env.getGraph());
		layout.setSize(new Dimension(width, height));
		
		BasicVisualizationServer<lcs.Position, Edge> vv = 
			new BasicVisualizationServer<lcs.Position, Edge>(layout);
		vv.setPreferredSize(new Dimension(width, height));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<lcs.Position>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Edge>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		this.getContentPane().add(vv); 
		
		this.setBounds(0, 0, width, height);
		this.addWindowListener(new GraphWindowCloser());
		this.setVisible(true);
	}
	
	// TODO add mouse listeners
	// feature to click a node and get info on it

}
