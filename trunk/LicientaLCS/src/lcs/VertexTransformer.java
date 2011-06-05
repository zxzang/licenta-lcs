package lcs;

import java.util.Map;
import java.util.logging.Logger;

import lcsmain.LcsMain;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.io.graphml.NodeMetadata;

/**
 * @author Dumitrescu Iustin
 *
 */
public class VertexTransformer implements Transformer<NodeMetadata, Position> {

	/**
	 * The string with which a vertex has its type defined.
	 */
	private static final String NODETYPE = "nodeType";
	
	/**
	 * The string with which a vertex has its type defined.
	 */
	private static final String ROBOTKEY = "robot";
	
	private org.apache.log4j.Logger logger = LcsMain.logger;

	@Override
	public final Position transform(final NodeMetadata metadata) {
		String nodeType = metadata.getProperty(NODETYPE);
		Map<String, String> props;
		
		Position ret;
		
		if (nodeType.equals("obstacle")) {
			ret = new Position(Position.TYPEOBSTACLE);
		} else if (nodeType.equals("final")) {
			ret = new Position(Position.TYPEFINAL);
		} else {
			ret = new Position(Position.TYPENORMAL);
		}
		
		props = metadata.getProperties();
		
		for (Map.Entry<String, String> entry : props.entrySet())
		{
			String key = entry.getKey();
			if (key.startsWith(ROBOTKEY)) {
				ret.addRobot(entry.getValue());
				
			}
		}


		ret.setName(metadata.getId());
		return ret;
	}

}
