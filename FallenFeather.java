package FallenFeather;

import javax.swing.JFrame;

public class FallenFeather {
	public FallenFeather() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new Panel());
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		// frame.setLocationRelativeTo();
		frame.setTitle("FallenFeather");
	}

	public static void main(String[] args) {
		new FallenFeather();
	}
}
