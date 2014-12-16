/**
 * BoundaryLabeling Demo show the algorithms for different types of leaders linking the nodes and labels.
 * 
 * This class give proof of concept of different kinds of labeling algorithms from the 
 * Michael A. Bekos Ph.D. thesis "Map Labeling Algorithm with Application in Graph Drawing and Cartography"
 
 * @author Peng Mi
 * @Time July 23, 2014
 */
import java.awt.BasicStroke; 
import java.awt.BorderLayout;
import java.awt.Color; 
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D; 
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener; 
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
 

import java.util.*;
 
 
public class BoundaryLabelingDemo {
     
    static JFrameWin jFrameWindow;
    static JSplitPane split;
   
    static String [] person_names = 
    	{"Kari	 Watts", "Maureen	 Coleman", "Mandy	 Buchanan", "Salvatore	 Bryan", "Lucille	 Lowe",
    	"Wilson	 Bell", "Olivia	 Lyons", "Katrina	 Castro", "Stewart	 Leonard", "Lucia	 Hill",
    	"Emanuel	 Ross", "Raymond	 Bowers", "Lela	 Armstrong", "Merle	 Moreno", "Jamie	 Padilla",
    	"Pamela	 Maxwell", "Arlene	 Murphy", "Angelo	 Ballard", "Earnest	 Evans", "Ada	 Hart",
    	"Brittany	 Griffin", "Marsha	 Johnston","Valerie	 Park", "Lowell	 Sims", "Marty	 Pope",
    	"Eileen	 Pearson","Vivian	 Santos", "Nicole	 Jones", "Miriam	 Russell", "Reginald	 Hoffman",
    	"Allan	 Tran", "Gloria	 Houston", "Karl	 Diaz","Monica	 Webb", "Charlotte	 Oliver",
    	"Antoinette	 Welch", "Terrance	 Fisher", "Barry	 Huff", "Sheila	 Goodwin", "Jim	 Hardy",
    	"Patsy	 Murray", "Richard	 Hansen", "Sharon	 Fletcher", "Nora	 Carpenter", "Opal	 Farmer",
    	"Iris	 Dunn", "Amanda	 Bradley", "Angelina	 Rodgers", "Gertrude	 Anderson", "Lindsay	 Gray"
    	};
    static ImageIcon[] images;
    static String[] leaderType = {"Octilinear-od","Octilinear-do",  "Rectilinear-po", "Rectilinear-opo", "straightLine"};
    public static Rectangle node_rect[]; //the nodes as rectangles
    public static Rectangle label_rect[]; // the label rectangles
    public static int order[]; // the relationship between the nodes and the labels
    
    
    public static int width = 900;
    public static int height = 675;
    public static int numNodes = 15;
    public static int leader_type = 1;
    public static boolean init = true; 
   
    public static class MyComponent extends JComponent implements MouseListener, MouseMotionListener{

    	int selectID = -1;
    	int selectLabel = -1;
    	int preX = 0;
    	int preY = 0;
    	
		MyComponent()
    	{
			GenerateData();
    		addMouseMotionListener(this);
    	    addMouseListener(this);
    	}
		
		void ChangeLeaderType(int type)
		{
			leader_type = type;
			repaint();
		}
		
		void ChangeData(int _numNodes)
		{
			if(_numNodes >50)
				numNodes = 50;
			else
				numNodes = _numNodes;
			GenerateData();
			repaint();
		}
	 
         
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D graphics2D = (Graphics2D)g;
            int c_width = getWidth();
            int c_height = getHeight();
            
           
            //re-scale the nodes position, label position and size based on the view port size
            float x_scale = 1.0f;
            float y_scale = 1.0f;
            if(!init)
            {	
            	x_scale = (float)c_width/width;
            	y_scale = (float)c_height/height;
            }
            init = false;
            
            for(int i =0; i < numNodes; i++)
			{
				int pos_x = node_rect[i].x;
				int pos_y = node_rect[i].y;
				node_rect[i].setLocation( 20 + (int)(x_scale*(pos_x - 20)), 20 + (int)(y_scale*(pos_y - 20)));
			}
           
            width = c_width;
            height = c_height;
             
            //draw the boundary
            Rectangle2D.Double rectangle 
                    = new Rectangle2D.Double(20, 20, c_width-280, c_height-40);     
            graphics2D.draw(rectangle);
            
            
            //draw the nodes
            graphics2D.setColor(new Color(217, 146, 54));
            for(int i = 0; i < numNodes; i++)
            	graphics2D.fill(node_rect[i]);
            
            //highlight the nodes with big rectangle
            if(selectID >=0)
        	{
            	g.setColor(Color.red);
            	graphics2D.setStroke(new BasicStroke(2));
            	graphics2D.draw(new Rectangle2D.Double(node_rect[selectID].getX()-2,node_rect[selectID].getY()-2,14, 14) );
        	}
            graphics2D.setStroke(new BasicStroke(1));
            
            
	            g.setColor(Color.gray);
	            for(int i = 0; i < numNodes; i++)
	            {
	            	if(selectID == order[i])
	            		g.setColor(new Color(217, 146, 54));
	            	else
	            		g.setColor(Color.gray);
	            	
	            	int r_width = 140;
	            	int r_height = (int)((c_height - 10)/(numNodes));
	            	label_rect[i] = new Rectangle(r_width, (int)(r_height*0.9)); 
	            	label_rect[i].setLocation(c_width - 160, 10 + i*r_height);
	            	graphics2D.fill(label_rect[i]);
	            }
	            g.setColor(Color.BLACK);
	            
	            
	           
            	graphics2D.setFont(new Font("TimesRoman", Font.PLAIN, 20)); 
            	DrawLeaders(graphics2D);
	            
            
        }
        
        void GenerateData()
        {
        	node_rect = new Rectangle [numNodes];
        	label_rect = new Rectangle[numNodes];
        	order = new int[numNodes]; 
        	Random rand = new Random();
        	Map node_id = new HashMap();
        	int pos_int;
        	float pos_x, pos_y;
        	for(int i = 0; i < numNodes; i++)
        	{
        		if(init)
        			pos_x = (float)(rand.nextInt(300)+20);
        		else
        			pos_x = (float)(rand.nextInt(width-360)+40);
        		pos_int = (rand.nextInt(height-140)+40);
        		while(node_id.containsKey(pos_int))
        			pos_int = (rand.nextInt(height-140)+40);
        		node_id.put(pos_int, i);
        		pos_y = (float)pos_int;
        		node_rect[i] = new Rectangle(10, 10);
        		node_rect[i].setLocation((int)pos_x-5, (int)pos_y-5);
        		
        		int r_height = (height - 40)/(numNodes);
        		label_rect[i] = new Rectangle(140, r_height-10);
        	}
        }
        
        float sign(float x1, float y1, float x2, float y2, float x3, float y3)
        {
        	return (x1-x3)*(y2-y3) - (x2-x3)*(y1-y3);
        }
        
        /*
         * Determine a point is in a triangle or not
         * return value: true is point (px, py) is in the triangle {(x1, y1), (x2, y2), (x3,y3)}, or else false 
         * */
        boolean PointInTriangle(float px, float py, float x1, float y1, float x2, float y2, float x3, float y3)
        {
        	boolean b1, b2, b3;
        	b1 = (sign(px, py, x1, y1, x2, y2) < 0.0);
        	b2 = (sign(px, py, x2, y2, x3, y3) < 0.0);
        	b3 = (sign(px, py, x3, y3, x1, y1) < 0.0);
        	return ((b1 == b2)&&(b2 == b3));
        }
        
        /*
         * Three cases determines the node and label relationship 
         * */
        void OctilinearLabelTypeOD(Graphics g)
        {
        	Graphics2D graphics2D = (Graphics2D)g;
        	
        	float point[] = new float[6];
        	
        	Set<Integer> processNode = new HashSet<Integer>(); 
        	for(int i = 0; i < numNodes; i++)
        		processNode.add(i);
        	TreeMap<Integer, Integer> nodeMap = new TreeMap<Integer, Integer>();
        	TreeMap<Integer, Integer> nodeType = new TreeMap<Integer, Integer>();
        	
        	for(int i = numNodes-1; i >=0; i--)
        	{
        		Set<Integer> node_id_b = new HashSet<Integer>();
        		Set<Integer> node_id_a = new HashSet<Integer>();
        		
        		//get the triangle of the R1
        		point[0] = width - 260;
        		point[1] = height - 20;
        		point[2] = width - 260;
        		point[3] = height - 20 - ((height - 20) - label_rect[i].y - label_rect[i].height*0.5f - (label_rect[i].x - (width-260)));
        		point[4] = width - 260 - ((height - 20) - label_rect[i].y - label_rect[i].height*0.5f - (label_rect[i].x - (width-260))); 
        		point[5] = height - 20; 
        		
        		if ((point[3] < height - 20)&&(point[4] < width - 260) ) 
        		{
        			//for nodes in this triangle find the  right most one  case A
        			for(Integer cur_id : processNode)
        			{
        				if(PointInTriangle(node_rect[cur_id].x+5, node_rect[cur_id].y+5, 
        						point[0], point[1], point[2], point[3], point[4], point[5]))
        					node_id_a.add(cur_id);
        			}
        			
        			//find the right most one
        			int x_pos = 0;
        			int select_id = -1;
        			for(Integer cur_id : node_id_a)
        			{
        				if(node_rect[cur_id].x+5 > x_pos)
        				{
        					x_pos = node_rect[cur_id].x+5;
        					select_id = cur_id;
        				}
        			}	
        			if(select_id != -1)
        			{
        				processNode.remove(select_id);
        				nodeMap.put(select_id, i);
        				nodeType.put(select_id, 1);
        				continue;
        			}
        		}
        		
        		point[0] = width - 260;
        		point[1] = 20;
        		point[2] = width - 260 - (label_rect[i].y + label_rect[i].height*0.5f - 20 - (label_rect[i].x - (width - 260))); 
        		point[3] = 20;
        		point[4] = width - 260; 
        		point[5] = 20 + (label_rect[i].y + label_rect[i].height*0.5f - 20 - (label_rect[i].x - (width - 260))); 
        		
        		if((point[2] < width - 260)&&(point[5] > 20) ) 
        		{
        			for(Integer cur_id : processNode)
        			{
        				if(PointInTriangle(node_rect[cur_id].x+5, node_rect[cur_id].y+5, 
        						point[0], point[1], point[2], point[3], point[4], point[5]))
        					node_id_b.add(cur_id);
        			}
        		}
        		
        		if ( node_id_b.size() + node_id_a.size() < processNode.size() )
        		{
        			int y_pos = 0;
        			int select_id = -1;
        			for(Integer cur_id : processNode)
        			{
        				if(node_id_a.contains(cur_id) || node_id_b.contains(cur_id) )
        					continue;
        				if(node_rect[cur_id].y +5 >= y_pos)
        				{
        					y_pos = node_rect[cur_id].y +5;
        					select_id = cur_id;
        				}
        			}
        			
        			if(select_id != -1)
        			{
        				processNode.remove(select_id);
        				nodeMap.put(select_id, i);
        				nodeType.put(select_id, 2);
        				continue;
        			}	
        		}
        		
        		if(node_id_b.size() > 0)
        		{
        			int x_pos = width;
        			int select_id = -1;
        			
        			for(Integer cur_id : node_id_b)
        			{
        				if(x_pos <= node_rect[cur_id].x +5)
        				{
        					x_pos = node_rect[cur_id].x +5;
        					select_id = i;
        				}
        			}
        			
        			if(select_id != -1)
        			{
        				processNode.remove(select_id);
        				nodeMap.put(select_id, i);
        				nodeType.put(select_id, 3);
        				continue;
        			}
        		}	
        	}
        	
        	//after the mapping 
        	for(Map.Entry<Integer, Integer> entry : nodeMap.entrySet())
       	 	{
        		int site_id = entry.getKey();
        		int port_id = entry.getValue();
        		String str = person_names[site_id];
        		int node_type = nodeType.get(site_id);
        		order[port_id] = site_id; 
        		if(node_type == 1)
        		{
        			 if(selectID == site_id)
            			 graphics2D.setColor(Color.red);
            		 else
            			 graphics2D.setColor(new Color(0, 255, 0));
            		
        		 	int y_pos = label_rect[port_id].y + (int)(label_rect[port_id].height*0.5f) + label_rect[port_id].x - node_rect[site_id].x-5; 
        			
            	 	graphics2D.draw(new Line2D.Double(node_rect[site_id].x+5,node_rect[site_id].y+5, node_rect[site_id].x+5, y_pos));
            	 	graphics2D.draw(new Line2D.Double(node_rect[site_id].x+5, y_pos, label_rect[port_id].x, label_rect[port_id].y + label_rect[port_id].height*0.5f));
            		 
            		graphics2D.setColor(Color.white);
            		g.drawString(str, label_rect[port_id].x, label_rect[port_id].y + (int)(label_rect[port_id].height*0.5f));
        		}
        		else if(node_type == 2)
        		{
        			 if(selectID == site_id)
            			 graphics2D.setColor(Color.red);
            		 else
            			 graphics2D.setColor(new Color(0, 255, 0));
        		 	int x_pos = label_rect[port_id].x - Math.abs(label_rect[port_id].y + (int)(label_rect[port_id].height*0.5f) - (node_rect[site_id].y + 5)); 
        			
            	 	graphics2D.draw(new Line2D.Double(node_rect[site_id].x+5,node_rect[site_id].y+5, x_pos, node_rect[site_id].y+5));
            	 	graphics2D.draw(new Line2D.Double(x_pos, node_rect[site_id].y+5, label_rect[port_id].x, label_rect[port_id].y + (int)(label_rect[port_id].height*0.5f)));
            		 
            		graphics2D.setColor(Color.white);
            		g.drawString(str, label_rect[port_id].x, label_rect[port_id].y + (int)(label_rect[port_id].height*0.5f) + 4);
        		}
        		else
        		{
        			 if(selectID == site_id)
            			 graphics2D.setColor(Color.red);
            		 else
            			 graphics2D.setColor(new Color(0, 255, 0));
            		
        		 	int y_pos = label_rect[port_id].y + (int)(label_rect[port_id].height*0.5f) -(label_rect[port_id].x - node_rect[site_id].x-5);
        			
            	 	graphics2D.draw(new Line2D.Double(node_rect[site_id].x+5,node_rect[site_id].y+5, node_rect[site_id].x+5, y_pos));
            	 	graphics2D.draw(new Line2D.Double(node_rect[site_id].x+5, y_pos, label_rect[port_id].x, label_rect[port_id].y +(int)(label_rect[port_id].height*0.5f)));
            		 
            		graphics2D.setColor(Color.white);
            		g.drawString(str, label_rect[port_id].x, label_rect[port_id].y + (int)(label_rect[port_id].height*0.5f));
        		}
       	 	}
        }
        
        /*
         * Three cases determines the node and label relationship 
         * */
        void OctilinearLabelTypePD(Graphics g)
        {
        	Graphics2D graphics2D = (Graphics2D)g;
        	
        	float point[] = new float[6];
        	
        	Set<Integer> processNode = new HashSet<Integer>(); 
        	for(int i = 0; i < numNodes; i++)
        		processNode.add(i);
        	TreeMap<Integer, Integer> nodeMap = new TreeMap<Integer, Integer>();
        	
        	for(int i = numNodes-1; i >=0; i--)
        	{	
        		Set<Integer> node_id_b = new HashSet<Integer>();
        		Set<Integer> node_id_a = new HashSet<Integer>();
        		
        		//get the triangle of the R1 
        		point[0] = width - 260;
        		point[1] = height - 20;
        		point[2] = width - 260;
        		point[3] = height - 20 - ((height - 20) - label_rect[i].y - label_rect[i].height*0.5f - (label_rect[i].x - (width-260)));
        		point[4] = width - 260 - ((height - 20) - label_rect[i].y - label_rect[i].height*0.5f - (label_rect[i].x - (width-260))); 
        		point[5] = height - 20; 
        		
        		if ((point[3] < height - 20)&&(point[4] < width - 260) ) 
        		{
        			for(Integer cur_id : processNode)
        			{
        				if(PointInTriangle(node_rect[cur_id].x+5, node_rect[cur_id].y+5, 
        						point[0], point[1], point[2], point[3], point[4], point[5]))
        					node_id_a.add(cur_id);
        			}
        			
        			//find the right most one
        			int x_pos = 0;
        			int select_id = -1;
        			for(Integer cur_id : node_id_a)
        			{
        				if(node_rect[cur_id].x+5 > x_pos)
        				{
        					x_pos = node_rect[cur_id].x+5;
        					select_id = cur_id;
        				}
        			}
        			if(select_id != -1)
        			{
        				processNode.remove(select_id);
        				nodeMap.put(select_id, i);
        				continue;
        			}
        		}
        		
        		point[0] = width - 260;
        		point[1] = 20;
        		point[2] = width - 260 - (label_rect[i].y + label_rect[i].height*0.5f - 20 - (label_rect[i].x - (width - 260))); 
        		point[3] = 20;
        		point[4] = width - 260; 
        		point[5] = 20 + (label_rect[i].y + label_rect[i].height*0.5f - 20 - (label_rect[i].x - (width - 260))); 
        		
        		if((point[2] < width - 260)&&(point[5] > 20) ) 
        		{
        			for(Integer cur_id : processNode)
        			{
        				if(PointInTriangle(node_rect[cur_id].x+5, node_rect[cur_id].y+5, 
        						point[0], point[1], point[2], point[3], point[4], point[5]))
        					node_id_b.add(cur_id);
        			}
        		}
        		
        		if ( node_id_b.size() + node_id_a.size() < processNode.size() )
        		{
        			int y_pos = 0;
        			int select_id = -1;
        			for(Integer cur_id : processNode)
        			{
        				if(node_id_a.contains(cur_id) || node_id_b.contains(cur_id) )
        					continue;
        				if(node_rect[cur_id].y +5 >= y_pos)
        				{
        					y_pos = node_rect[cur_id].y +5;
        					select_id = cur_id;
        				}
        			}
        			
        			if(select_id != -1)
        			{
        				processNode.remove(select_id);
        				nodeMap.put(select_id, i);
        				continue;
        			}	
        		}
        		
        		if(node_id_b.size() > 0)
        		{
        			int x_pos = width;
        			int select_id = -1;
        			
        			for(Integer cur_id : node_id_b)
        			{
        				if(x_pos <= node_rect[cur_id].x +5)
        				{
        					x_pos = node_rect[cur_id].x +5;
        					select_id = i;
        				}
        			}
        			
        			if(select_id != -1)
        			{
        				processNode.remove(select_id);
        				nodeMap.put(select_id, i);
        				continue;
        			}
        		}	
        	}
        	
        	//after the mapping 
        	for(Map.Entry<Integer, Integer> entry : nodeMap.entrySet())
       	 	{
        		int site_id = entry.getKey();
        		int port_id = entry.getValue();
        		String str = person_names[site_id];
        		order[port_id] = site_id;
        		if(node_rect[site_id].y+5 > label_rect[port_id].y + label_rect[port_id].height*0.5f)
        		{
        			 if(selectID == site_id)
            			 graphics2D.setColor(Color.red);
            		 else
            			 graphics2D.setColor(new Color(0, 255, 0));
            		
        			int x_pos = node_rect[site_id].x + 5  + (node_rect[site_id].y + 5 - (int)(label_rect[port_id].y + label_rect[port_id].height*0.5f));
        			graphics2D.draw(new Line2D.Double(node_rect[site_id].x+5,node_rect[site_id].y+5, x_pos, label_rect[port_id].y + label_rect[port_id].height*0.5f));
        			graphics2D.draw(new Line2D.Double(x_pos,  label_rect[port_id].y + label_rect[port_id].height*0.5f, label_rect[port_id].x, label_rect[port_id].y + label_rect[port_id].height*0.5f));
        		 	 
            		graphics2D.setColor(Color.white);
            		g.drawString(str, label_rect[port_id].x, (int)(label_rect[port_id].y + label_rect[port_id].height*0.5f + 4));
        		}
        		else
        		{
        			 if(selectID == site_id)
            			 graphics2D.setColor(Color.red);
            		 else
            			 graphics2D.setColor(new Color(0, 255, 0));
            		
        			int x_pos = node_rect[site_id].x + 5  + ((int)(label_rect[port_id].y + label_rect[port_id].height*0.5f) - node_rect[site_id].y - 5);
        			graphics2D.draw(new Line2D.Double(node_rect[site_id].x+5,node_rect[site_id].y+5, x_pos, (int)(label_rect[port_id].y + label_rect[port_id].height*0.5f)));
        			graphics2D.draw(new Line2D.Double(x_pos,  (int)(label_rect[port_id].y + label_rect[port_id].height*0.5f), label_rect[port_id].x, (int)(label_rect[port_id].y + label_rect[port_id].height*0.5f))); 
        		 		 
            		graphics2D.setColor(Color.white);
            		g.drawString(str, label_rect[port_id].x, (int)(label_rect[port_id].y + label_rect[port_id].height*0.5f + 4));
        		}
       	 	}
        }
        
        void RectilinearLabelTypePO(Graphics g)
        {
        	Graphics2D graphics2D = (Graphics2D)g;
        	
        	TreeMap<Integer, Integer> sortMap = new TreeMap<Integer, Integer>();
        	for(int i = 0; i < numNodes; i++)
        		sortMap.put(node_rect[i].y, i);
        	
        	int index = 0;
        	for(Map.Entry<Integer, Integer> entry : sortMap.entrySet())
       	 	{
        		order[index] = entry.getValue();
        		index++;
       	 	}
        	
        	for(int i = 1; i < numNodes; i++)
        	{
        		for(int j = i-1; j>=0; j--)
        		{
        			if((label_rect[j].y + label_rect[j].height*0.5f < node_rect[order[j]].y+5) && (node_rect[order[i]].x < node_rect[order[j]].x ))
            		{
            			int tmp = order[i];
            			order[i] = order[j];
            			order[j] = tmp;
            		}
        			
        			else if((node_rect[order[i]].y+5 < label_rect[j].y + label_rect[j].height*0.5f)&&(node_rect[order[i]].x > node_rect[order[j]].x))
        			{
        				int tmp = order[i];
        				order[i] = order[j];
        				order[j] = tmp;
        			}
        		}
        	}
        	
        	int node_id = 0;
        	for(int i = 0; i < numNodes; i++)
        	{
        		
        		node_id = order[i];
        		  
        		if(selectID == node_id)
        			 graphics2D.setColor(Color.red);
        		else
        			 graphics2D.setColor(new Color(0, 255, 0));
        		
        		graphics2D.draw(new Line2D.Double(node_rect[node_id].x+5,node_rect[node_id].y+5, node_rect[node_id].x+5, label_rect[i].y + label_rect[i].height*0.5));
        		graphics2D.draw(new Line2D.Double(node_rect[node_id].x+5, label_rect[i].y + label_rect[i].height*0.5, label_rect[i].x, label_rect[i].y + label_rect[i].height*0.5));
        		 
        		graphics2D.setColor(Color.white);
        		g.drawString(person_names[node_id], label_rect[i].x, (int)(label_rect[i].y + label_rect[i].height*0.5 + 4));
             }
       	 }
        	
      
        /*
    	 * Sort the node based on y position, which determinate the relationship between node id and labels.
    	 * */
        void RectilinearLabeling(Graphics g)
        {
        	Graphics2D graphics2D = (Graphics2D)g;
        	
        	TreeMap<Integer, Integer> sortMap = new TreeMap<Integer, Integer>();
        	//sort nodes base on their y position
        	for(int i = 0; i < numNodes; i++)
        		sortMap.put(node_rect[i].y, i);
        	
        	//different node has different displacement positions  
        	float step_length = 50/numNodes;
        	
        	int index = 0;
        	float pre_x_pos = 0;
        	for(Map.Entry<Integer, Integer> entry : sortMap.entrySet())
        	{
        		 int node_id = entry.getValue();
        		 float x_split = 0;
        		 order[index] = node_id;
        		 if(index >0)
        		 {
        			 if(node_rect[node_id].y < label_rect[index-1].y)
        				 x_split = pre_x_pos - step_length;
        			 else
        				 x_split = width-215 + index*step_length;
        		 }
        		 else
        			 x_split = width-215 + index*step_length;
        			 
        		 if(selectID == node_id)
        			 graphics2D.setColor(Color.red);
        		 else 
        			 graphics2D.setColor(new Color(0, 255, 0));
        		 
        		 graphics2D.draw(new Line2D.Double(node_rect[node_id].x+5,node_rect[node_id].y+5, x_split  ,node_rect[node_id].y+5));
        		 graphics2D.draw(new Line2D.Double(x_split,node_rect[node_id].y+5, x_split, label_rect[index].y+label_rect[index].height*0.5));
        		 graphics2D.draw(new Line2D.Double(x_split,label_rect[index].y+label_rect[index].height*0.5, label_rect[index].x, label_rect[index].y+label_rect[index].height*0.5));
        		 
        		 graphics2D.setColor(Color.white);
        		 g.drawString(person_names[node_id], label_rect[index].x, (int)(label_rect[index].y+label_rect[index].height*0.5 + 4));
              	 index++;
              	 pre_x_pos = x_split;
        	 }
        }
        
        /*
    	 * calculate the cos value from the bottom to the top of the labels, get the smallest one
    	 * assign this node id to the label.  
    	 * */
        void StraightLineLabeling(Graphics g)
        {
        	Graphics2D graphics2D = (Graphics2D)g;
        	
        	boolean [] marked = new boolean[numNodes];	 
        	float disp_x, disp_y;
        	
        	 
	        	for(int i = numNodes-1; i >=0; i--)
	        	{
	        		float cos_value = 2.0f;
	        		int select_mark = -1;
	        		for(int j =0; j < numNodes; j++)
	        		{
	        			if(marked[j])
	        				continue;
	        			disp_x =  label_rect[i].x -5 -node_rect[j].x;
	        			disp_y =  label_rect[i].y + label_rect[i].height*0.5f -5 -node_rect[j].y ;
	        			float norm = (float) Math.sqrt(disp_x*disp_x + disp_y*disp_y);
	        			disp_y = disp_y/norm;
	        			
	        			if(disp_y < cos_value)
	        			{
	        				select_mark = j;
	        				cos_value = disp_y;
	        			}
	        		}
	        		
	        		if(select_mark != -1)
	        		{
	        			marked[select_mark] = true;
	        			order[i] = select_mark;
	        		}	
	        	}
        	 
        	  
        	 for(int i = 0; i < numNodes; i++)
        	 {
        		 int node_id = order[i];
        		 //String str = "    Node ID " + node_id;
        		 if(selectID == node_id)
        			 graphics2D.setColor(Color.red);
        		 else
        			 graphics2D.setColor(new Color(0, 255, 0));
              	 graphics2D.draw(new Line2D.Double(node_rect[node_id].x+5,node_rect[node_id].y+5, label_rect[i].x, label_rect[i].y+label_rect[i].height*0.5 ));
              	 graphics2D.setColor(Color.white);
              	g.drawString(person_names[node_id], label_rect[i].x, (int)(label_rect[i].y+label_rect[i].height*0.5 + 4)); 
        	 }
        }
        
        /*
         * Get the relationship between the nodes and the labels. Different algorithms are based on 
         * different types of leaders.
         * */
        void DrawLeaders(Graphics g)
        {
        	if(leader_type == 5)
        		StraightLineLabeling(g);
        	else if(leader_type == 4)
        		RectilinearLabeling(g);
        	else if(leader_type == 3)	
        		RectilinearLabelTypePO(g);
        	else if(leader_type == 2)
        		OctilinearLabelTypePD(g);
        	else if(leader_type == 1)
        		OctilinearLabelTypeOD(g);
        }

		@Override
		public void mouseDragged(MouseEvent e) {
			if(selectID <0)
				return;
			else if(selectLabel == -1)
			{
				node_rect[selectID].setLocation( preX + e.getX(), preY + e.getY());
				if(preX + e.getX() <= 25)
					node_rect[selectID].setLocation(25, preY + e.getY());
				if(preY + e.getY() <= 25)
					node_rect[selectID].setLocation(preX + e.getX(), 25);
				if(preX + e.getX() >= width-265)
					node_rect[selectID].setLocation( width-265, preY + e.getY());
				if(preY + e.getY() >= height-25)
					node_rect[selectID].setLocation( preX + e.getX(), height-25);
			}
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			selectID = -1;
			for(int i = 0; i < numNodes; i++) //node rectangle mouse event detection
			{
				if(node_rect[i].contains(e.getX(), e.getY()))
				{
					selectLabel = -1;
					selectID = i;
					preX = node_rect[selectID].x - e.getX();
					preY = node_rect[selectID].y - e.getY();	 	
					break;
				}	
			}
		
			//label rectangle mouse event detection
			for(int i = 0; i < numNodes; i++)
			{
				if(label_rect[i].contains(e.getX(), e.getY()))
				{
					selectID = order[i];
					selectLabel = 1;
					break;
				}	
			}
			repaint();
		}
	
		@Override
		public void mousePressed(MouseEvent e) {
			for(int i = 0; i < numNodes; i++)
			{
				if(node_rect[i].contains(e.getX(), e.getY()))
				{
					selectID = i;
					preX = node_rect[selectID].x - e.getX();
					preY = node_rect[selectID].y - e.getY();
					repaint();
					break;
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			if(selectID <0)
				return;
			if(node_rect[selectID].contains(e.getX(), e.getY()))
				node_rect[selectID].setLocation(preX + e.getX(), preY + e.getY());
			selectID = -1;
			repaint();
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
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
		}
    }
    
     
    ///
     
    public static class JFrameWin extends JFrame implements ActionListener {
    	MyComponent myComponent;
    	JTextField text_nodes;
    	ImageIcon[] images;
    	
        public JFrameWin(){
            this.setTitle("BoundaryLabelingDemo_(Peng Mi)");
            this.setSize(width, height);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  
            myComponent = new MyComponent();
            
            
            JPanel panel = new JPanel();
            JPanel panel2 = new JPanel();
            panel2.setLayout(new GridLayout(3,10));
    		panel.setLayout(new GridLayout(7,10));
    		JLabel label2 = new JLabel("Number of Nodes");
    		text_nodes = new JTextField(3);
    		text_nodes.setText("15");
    		text_nodes.addActionListener(this);
    		JLabel label3 = new JLabel("Leader Types");
    		Font labelFont2 = label2.getFont();
    		label2.setPreferredSize(new Dimension(100,20));
    		
    	 
    		
    		label2.setFont(new Font(labelFont2.getName(), Font.BOLD, 20));
    		label3.setFont(new Font(labelFont2.getName(), Font.BOLD, 20));
    		text_nodes.setFont(new Font(labelFont2.getName(), Font.PLAIN, 21));
    		
    		final JComboBox jc = new JComboBox();
    		jc.addItem("Straight-Line  leaders");
    		jc.addItem("Rectilinear leaders-opo");
    		jc.addItem("Rectilinear leaders-po");
    		jc.addItem("Octilinear Leader-pd");
    		jc.addItem("Octilinear Leader-od");
    		panel2.add(label2);
    		panel2.add(text_nodes);
    		panel2.add(label3);
    		 
    		panel.add(panel2);
    		 	
    		 images = new ImageIcon[leaderType.length];
    	        Integer[] intArray = new Integer[leaderType.length];
    	        for (int i = 0; i < leaderType.length; i++) {
    	            intArray[i] = new Integer(i);
    	            images[i] = createImageIcon("images/" + leaderType[i] + ".gif");
    	            if (images[i] != null) {
    	                images[i].setDescription(leaderType[i]);
    	            }
    	        }

    	        //Create the combo box.
    	        final JComboBox petList = new JComboBox(intArray);
    	        ComboBoxRenderer renderer= new ComboBoxRenderer();
    	        renderer.setPreferredSize(new Dimension(100, 90));
    	        petList.setRenderer(renderer);
    	        petList.setMaximumRowCount(5);

    	        panel.add(petList);
    	        
    		
    		jc.setFont(new Font(labelFont2.getName(), Font.PLAIN, 15));
    		
    		panel.setSize(500, 500);
    		
            split = new JSplitPane();
    		split.setLeftComponent(myComponent);
    		split.setRightComponent(panel);
    		split.setOneTouchExpandable(true);
    		split.setContinuousLayout(false);
    		split.setDividerLocation(700);
             
            this.add(split);
            
            System.out.println(panel.getBackground());
            
            petList.addActionListener(new ActionListener(){
            	public void actionPerformed(ActionEvent e)
            	{
            		myComponent.ChangeLeaderType(petList.getSelectedIndex()+1);
            	}
            });
        }
        
        
        /** Returns an ImageIcon, or null if the path was invalid. */
        protected static ImageIcon createImageIcon(String path) {
            java.net.URL imgURL = BoundaryLabelingDemo.class.getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("Couldn't find file: " + path);
                    return null;
            }
        }

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String text = text_nodes.getText();
			
			int node_num = Integer.parseInt(text);
			if(node_num >0 && node_num <100)
				myComponent.ChangeData(node_num);
		}
		
		
		 //customized combox
	    class ComboBoxRenderer extends JLabel  implements ListCellRenderer {
	    	private Font uhOhFont;

	    	public ComboBoxRenderer() 
	    	{
	    		setOpaque(true);
	    		setHorizontalAlignment(CENTER);
	    		setVerticalAlignment(CENTER);
	    	}

	    	/*
	    	 * This method finds the image and text corresponding
	    	 * to the selected value and returns the label, set up
	    	 * to display the text and image.
	    	 */
	    	public Component getListCellRendererComponent(
	    			JList list,
	    			Object value,
	    			int index,
	    			boolean isSelected,
	    			boolean cellHasFocus) {
	    		//Get the selected index. (The index param isn't
	    		//always valid, so just use the value.)
	    		int selectedIndex = ((Integer)value).intValue();

	    		if (isSelected) {
	    			setBackground(list.getSelectionBackground());
	    			setForeground(list.getSelectionForeground());
	    		} else {
	    			setBackground(list.getBackground());
	    			setForeground(list.getForeground());
	    		}

	    		//Set the icon and text.  If icon was null, say so.
	    		ImageIcon icon = images[selectedIndex];
	    		//String pet = petStrings[selectedIndex];
	    		setIcon(icon);
	    		if (icon != null) {
	    			//setText(pet);
	    			setFont(list.getFont());
	    		} else {
	    			//setUhOhText(pet + " (no image available)",
	    			//		list.getFont());
	    		}

	    		return this;
	    	}

	    	//Set the font and text when no image was found.
	    	protected void setUhOhText(String uhOhText, Font normalFont) {
	    		if (uhOhFont == null) { //lazily create this font
	    			uhOhFont = normalFont.deriveFont(Font.ITALIC);
	    		}
	    		setFont(uhOhFont);
	    		setText(uhOhText);
	    	}
	    }
    }
    
     
 
    public static void main(String[] args){
        Runnable doSwingLater = new Runnable(){
            @Override
            public void run() {
                jFrameWindow = new JFrameWin();
                jFrameWindow.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(doSwingLater);
    }
 
}