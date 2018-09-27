package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class CharacterSlot extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JPanel imagePanel;
	private JPanel characterPanel;

	int size = 100;
	private BufferedImage image;
	private int x1,y1,x2,y2;
	
	public CharacterSlot(BufferedImage image, int x1, int y1, int x2, int y2) {
		// TODO Auto-generated constructor stub
		
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setMinimumSize(new Dimension(size, size));
		setMaximumSize(new Dimension(size, size));
		setPreferredSize(new Dimension(size, size));
		
		scaleAndResizeImage(image, x1, y1, x2, y2);
	}
	
	private void scaleAndResizeImage(BufferedImage image, int x1, int y1, int x2, int y2)
	{
		int max = (x2 - x1) > (y2 - y1) ? (x2 - x1) : (y2 - y1);
		BufferedImage croppedImage = new BufferedImage(max,max, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = croppedImage.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, max, max);

		if (x2 -x1 < y2 - y1)
		{
			int midleft = (max/2) - (x2-x1)/2;
			int midright = (max/2) + (x2-x1)/2;
			g2d.drawImage(image, midleft, 10, midright, max-10, x1, y1, x2, y2, this);
		}
		else
		{
			int midleft = (max/2) - (y2-y1)/2;
			int midright = (max/2) + (y2-y1)/2;
			g2d.drawImage(image, 10, midleft, max-10, midright, x1, y1, x2, y2, this);
		}
		
		
		this.image = croppedImage;
	}
	
	public void saveImage(String name,String type) {
		int w = image.getWidth();  
	    int h = image.getHeight();  
	    BufferedImage dimg = new BufferedImage(28, 28, image.getType());  
	    Graphics2D g = dimg.createGraphics();  
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	    RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
	    g.drawImage(image, 0, 0, 28, 28, 0, 0, w, h, null);  
	    g.dispose(); 
		try{
			File file = new File("slike");
			if (!file.exists())
				file.createNewFile();
			ImageIO.write(dimg, type, new File("slike/" + name+"."+type));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.drawImage(image, 0, 0, size, size, 0, 0, image.getWidth(), image.getHeight(), this);
	}
}
