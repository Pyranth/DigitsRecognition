package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import GUI.ButtonArea;
import GUI.CharactersArea;
import GUI.DrawArea;
import drawing.DrawingController;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public MainFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(new Dimension(800, 600));
		setLayout(new BorderLayout());
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JPanel left = new JPanel();
		JPanel right = new JPanel();
		
		left.setBorder(BorderFactory.createLineBorder(Color.GREEN));
		left.setPreferredSize(new Dimension(400, 600));
		left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
		right.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
		//right.setPreferredSize(new Dimension(300, 600));
		right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
		
		DrawArea drawArea = new DrawArea();
		ButtonArea buttonArea = new ButtonArea();
		
		left.add(drawArea);
		left.add(buttonArea);
		
		CharactersArea characterArea = new CharactersArea();
		
		right.add(characterArea);
		
		DrawingController dc = new DrawingController(drawArea, characterArea, buttonArea);
		
		add(left, BorderLayout.LINE_START);
		add(right, BorderLayout.CENTER);
		
		setVisible(true);
	}
}
