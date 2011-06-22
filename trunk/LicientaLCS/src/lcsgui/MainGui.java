package lcsgui;

import lcs.Environment;

public class MainGui {
	
	public MainGui(Environment env) {
		JUNGFrame jframe = new JUNGFrame(env);
		jframe.setVisible(true);
	}
	
	public MainGui(Environment env, String ip) {
		new UBIClient(env, ip);
	}
}
