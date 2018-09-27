package GUI;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import drawing.DrawingController;
import sun.font.GlyphLayout;
import sun.security.provider.VerificationProvider;

public class ButtonArea extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public JButton clear = new JButton("Clear");
	public JButton segmentate = new JButton("Segmentate");
	public JButton recognize = new JButton("Recognize");
	
	public JButton train = new JButton("Train");

	public ButtonArea() {
		// TODO Auto-generated constructor stub
		clear.setActionCommand("clear");
		segmentate.setActionCommand("segmentate");
		recognize.setActionCommand("recognize");
		
		train.setActionCommand("train");
		
		setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		JPanel topPanel = new JPanel();
		
		panel.add(clear);
		panel.add(segmentate);
		panel.add(recognize);
		
		topPanel.add(train);
		
		add(topPanel, BorderLayout.CENTER);
		add(panel, BorderLayout.SOUTH); 
	}
}
