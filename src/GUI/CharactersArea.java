package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class CharactersArea extends JPanel {

	private static final long serialVersionUID = 1L;
	private ArrayList<JPanel> slots = new ArrayList<>();

	public CharactersArea() {
		// TODO Auto-generated constructor stub
		//setPreferredSize(new Dimension(400, 400));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	public void getCharacterData(ArrayList<ArrayList<Point>> segmentLines, BufferedImage image)
	{
		for (int i = 0; i < segmentLines.size(); i += 4)
		{
			int x1,y1,x2,y2;
			x1 = (int) segmentLines.get(i).get(0).getX() + 1;
			y1 = (int) segmentLines.get(i).get(0).getY() + 1;
			x2 = (int) segmentLines.get(i+1).get(segmentLines.get(i+1).size() - 1).getX();
			y2 = (int) segmentLines.get(i+1).get(segmentLines.get(i+1).size() - 1).getY();
			addCharacter(x1, y1, x2, y2, image);
		}
		
		validate();
		repaint();
	}
	
	private void addCharacter(int x1, int y1, int x2, int y2, BufferedImage image)
	{
		CharacterSlot panel = new CharacterSlot(image, x1, y1, x2, y2);
		
		JPanel slot = new JPanel();
		slot.setLayout(new BoxLayout(slot, BoxLayout.PAGE_AXIS));
		slot.add(panel);
		JLabel label = new JLabel("x");
		label.setFont(new Font("Serif", Font.PLAIN, 36));
		slot.add(label);
		slots.add(slot);
		
		add(slot);
	}
	
	public void setNumbers(ArrayList<Integer> numbers)
	{
		int x = 0;
		for (JPanel slot : slots)
		{
			((JLabel)slot.getComponent(1)).setText(String.valueOf(numbers.get(x++)));
		}
		
		repaint();
	}
	
	public void removeSlots()
	{
		slots.clear();
		removeAll();
		repaint();
	}
}
