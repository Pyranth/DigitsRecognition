package drawing;

import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import GUI.ButtonArea;
import GUI.CharacterSlot;
import GUI.CharactersArea;
import GUI.DrawArea;

public class DrawingController implements ActionListener{
	private DrawArea drawArea;
	private CharactersArea charactersArea;
	private ButtonArea buttonArea;
	
	public DrawingController(DrawArea drawArea, CharactersArea charactersArea, ButtonArea buttonArea) {
		// TODO Auto-generated constructor stub
		this.drawArea = drawArea;
		this.charactersArea = charactersArea;
		this.buttonArea = buttonArea;
		
		buttonArea.clear.addActionListener(this);
		buttonArea.segmentate.addActionListener(this);
		buttonArea.recognize.addActionListener(this);
		
		buttonArea.train.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand() == "clear")
		{
			drawArea.removePoints();
			drawArea.repaint();
			charactersArea.removeSlots();
		}	
		if (e.getActionCommand() == "segmentate")
		{
			drawArea.segmentate();
			drawArea.repaint();
			
			charactersArea.removeSlots();
			charactersArea.removeAll();
			charactersArea.repaint();
			
			charactersArea.getCharacterData(drawArea.getSegmentLines(), drawArea.getImage());
		}	
		if (e.getActionCommand() == "recognize")
		{
			File index = new File("slike");
			
			if (!index.exists())
				index.mkdir();
			else
			{
				String[]entries = index.list();
				
				if (entries != null)
				{
					for(String s: entries){
					    File currentFile = new File(index.getPath(),s);
					    currentFile.delete();
					}
				}
			}
			
			int x = 1;
			for (Component slot : charactersArea.getComponents())
			{
				((CharacterSlot)((JPanel)slot).getComponent(0)).saveImage("char" + x, "png");
				x++;
			}
			
			File file = new File(System.getProperty("user.dir"));
			String path;
			ProcessBuilder pb;
			
			if(System.getProperty("os.name").contains("Windows"))
			{
				path = file.getPath() + "\\scripts\\prepoznavanje_karaktera.py";
				pb = new ProcessBuilder("py", path);
			}
			else
			{
				path = file.getPath() + "/scripts/prepoznavanje_karaktera.py";
				pb = new ProcessBuilder("python", path);
			}	
			
			ArrayList<Integer> numbers = new ArrayList<>();
			try {
				Process p = pb.start();
				
	            BufferedReader stdInput = new BufferedReader(new 
	                 InputStreamReader(p.getInputStream()));
	
	            BufferedReader stdError = new BufferedReader(new 
	                 InputStreamReader(p.getErrorStream()));
	
	            // read the output from the command
	            String s = null;
	            while ((s = stdInput.readLine()) != null) {
	            	if (s.length() > 1)
	            	{
	            		System.out.println(s);
	            		if (s.contains("Model ne postoji!"))
	            			throw new IOException("Model ne postoji");
	            		continue;
	            	}
	                numbers.add(Integer.parseInt(s));
	            }
	            System.out.println("Here is the standard error of the command (if any):\n");
	            while ((s = stdError.readLine()) != null) {
	                System.out.println(s);
	            }
	            
	            charactersArea.setNumbers(numbers);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getActionCommand() == "train")
		{
			charactersArea.add(new JLabel("Obucavanje u toku..."));
			
			charactersArea.repaint();
			charactersArea.validate();
			charactersArea.paintImmediately(0, 0, 300, 300);
						
			File file = new File(System.getProperty("user.dir"));
			String path;
			ProcessBuilder pb;
			
			if(System.getProperty("os.name").contains("Windows"))
			{
				path = file.getPath() + "\\scripts\\obucavanje_mreze.py";
				pb = new ProcessBuilder("py", path);
			}
			else
			{
				path = file.getPath() + "/scripts/obucavanje_mreze.py";
				pb = new ProcessBuilder("python", path);
			}	
			
			try {
				Process p = pb.start();
				
				BufferedReader stdInput = new BufferedReader(new 
		                 InputStreamReader(p.getInputStream()));
		
	            BufferedReader stdError = new BufferedReader(new 
	                 InputStreamReader(p.getErrorStream()));
	
	            charactersArea.removeAll();
	            // read the output from the command
	            String s = null;
	            while ((s = stdInput.readLine()) != null) {
	            	charactersArea.add(new JLabel(s));
	            }
	            
	            charactersArea.repaint();
	            charactersArea.validate();
	            
	            System.out.println("Here is the standard error of the command (if any):\n");
	            while ((s = stdError.readLine()) != null) {
	                System.out.println(s);
	            }
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}
	}

}
