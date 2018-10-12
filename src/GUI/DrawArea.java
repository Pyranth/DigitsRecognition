package GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class DrawArea extends JPanel implements MouseListener, MouseMotionListener{

	private static final long serialVersionUID = 1L;
	
	private ArrayList<ArrayList<Point>> lines = new ArrayList<>();
	private ArrayList<ArrayList<Point>> segmentLines = new ArrayList<>();
	private ArrayList<ArrayList<Point>> linesSave = new ArrayList<>();
	
	private BufferedImage image;
	
	public DrawArea() {
		// TODO Auto-generated constructor stub
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setPreferredSize(new Dimension(400, 400));
		setMaximumSize(new Dimension(400, 400));
		setMinimumSize(new Dimension(400, 400));
		setBackground(Color.WHITE);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void removePoints()
	{
		lines.clear();
		segmentLines.clear();
	}
	
	public void segmentate()
	{
		mergeLines();
		int offset = 10;
		
		for (ArrayList<Point> line : lines)
		{
			ArrayList<Double> lineX = new ArrayList<>();
			ArrayList<Double> lineY = new ArrayList<>();
			for (Point p : line)
			{
				if (p == null)
					continue;
				lineX.add(p.getX());
				lineY.add(p.getY());
			}

			int minX = lineX.indexOf(Collections.min(lineX));
			int maxX = lineX.indexOf(Collections.max(lineX));
			int minY = lineY.indexOf(Collections.min(lineY));
			int maxY = lineY.indexOf(Collections.max(lineY));
			
			ArrayList<Point> leftVertical = new ArrayList<>();
			ArrayList<Point> rightVertical = new ArrayList<>();
			ArrayList<Point> topHorizontal = new ArrayList<>();
			ArrayList<Point> bottomHorizontal = new ArrayList<>();
			
			for (int i = lineY.get(minY).intValue() - offset; i <= lineY.get(maxY).intValue() + offset; i++)
			{
				leftVertical.add(new Point(lineX.get(minX).intValue() - offset, i));
			}
			for (int i = lineY.get(minY).intValue() - offset; i <= lineY.get(maxY).intValue() + offset; i++)
			{
				rightVertical.add(new Point(lineX.get(maxX).intValue() + offset, i));
			}
			for (int i = lineX.get(minX).intValue() - offset; i <= lineX.get(maxX).intValue() + offset; i++)
			{
				topHorizontal.add(new Point(i, lineY.get(minY).intValue() - offset));
			}
			for (int i = lineX.get(minX).intValue() - offset; i <= lineX.get(maxX).intValue() + offset; i++)
			{
				bottomHorizontal.add(new Point(i, lineY.get(maxY).intValue() + offset));
			}
			
			segmentLines.add(leftVertical);
			segmentLines.add(rightVertical);
			segmentLines.add(topHorizontal);
			segmentLines.add(bottomHorizontal);
		}
		
		saveImage();
	}
	
	private void mergeLines()
	{
		for (int i = 0; i < lines.size() - 1; i++)
		{
			for (int j = i+1; j < lines.size(); j++)
			{
				for (int k = 1; k < lines.get(j).size(); k++)
				{
					boolean check = false;
					for (int p = 1; p < lines.get(i).size(); p++)
					{
						if (	lines.get(j).get(k) == null || 
								lines.get(i).get(p) == null ||
								lines.get(i).get(p-1) == null ||
								lines.get(j).get(k-1) == null)
							continue;
						if (intersects(	lines.get(j).get(k).getX(),
										lines.get(j).get(k).getY(),
										lines.get(j).get(k-1).getX(),
										lines.get(j).get(k-1).getY(),
										lines.get(i).get(p).getX(),
										lines.get(i).get(p).getY(),
										lines.get(i).get(p-1).getX(),
										lines.get(i).get(p-1).getY()))
						{
							lines.get(i).add(null);
							lines.get(i).addAll(lines.get(j));
							lines.remove(j);
							check = true;
							break;
						}
					}
					if (check)
						break;
				}
			}
		}
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	public ArrayList<ArrayList<Point>> getSegmentLines()
	{
		return segmentLines;
	}
	
	private boolean intersects(double d, double e, double f, double g, double h, double i, double j, double k) { 
		double bx = f - d; 
		double by = g - e; 
		double dx = j - h; 
		double dy = k - i;
		double b_dot_d_perp = bx * dy - by * dx;
		  if (b_dot_d_perp == 0) {
		    return false;
		  }
		  double cx = h - d;
		  double cy = i - e;
		  double t = (cx * dy - cy * dx) / b_dot_d_perp;
		  if (t < 0 || t > 1) {
		    return false;
		  }
		  double u = (cx * by - cy * bx) / b_dot_d_perp;
		  if (u < 0 || u > 1) { 
		    return false;
		  }
		  return true;
		}
	
	public void saveImage() {
		image = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		paint(g2);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);

	    Graphics2D g2 = (Graphics2D) g;

	    g2.setColor(Color.BLACK);
	    g2.setStroke(new BasicStroke((15f),
	                                 BasicStroke.CAP_ROUND,
	                                 BasicStroke.JOIN_ROUND));
	    for(ArrayList<Point> list : lines)
	    	for (int i = 1; i < list.size(); i++)
	    	{
	    		if (list.get(i-1) == null || list.get(i) == null)
	    			continue;
	    		g2.draw(new Line2D.Float(list.get(i-1), list.get(i)));
	    	}
	    
	    // segments
	    
	    g2.setColor(new Color(0, 0, 0));
	    g2.setStroke(new BasicStroke(1f,
	                                 BasicStroke.CAP_ROUND,
	                                 BasicStroke.JOIN_ROUND));
	    for(ArrayList<Point> list : segmentLines)
	    	for (int i = 1; i < list.size(); i++)
	    	{
	    		if (list.get(i-1) == null || list.get(i) == null)
	    			continue;
	    		g2.draw(new Line2D.Float(list.get(i-1), list.get(i)));
	    	}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		lines.add(new ArrayList<Point>());
        repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
			
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		lines.get(lines.size()-1).add(e.getPoint());
        repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
