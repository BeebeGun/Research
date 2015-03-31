
import java.util.*;

import processing.core.PVector;


public class Simulation {

	public MainGUI gui;
	float time = 0;
	float zoom = 1;
	public float delta_t = (float) 0.01;
	boolean pause;
	public static int width = 400;
	public static int height = 400;
	public static int depth = 400;
	ArrayList<Particle> parts = new ArrayList<Particle>();
	Random rand = new Random();
	boolean gravity = true;
	boolean planar = false;
	boolean bulge = false;
	float grav_mag;
	float damp = (float) 5.0; //0.95
	float inellastic = (float) 0.5;
	int particleCount = 500;
	//repulsion radius
	float radius;
	float repulsion_strength;
	boolean repulsion = true;
	boolean single_step, user_control;
	Particle closest;
	boolean mousedown = false;
	//SPRING FORCES
	float spring_constant = (float) 5;
	float rest_length = (float) 0.25;
	float spring_damp = (float) 0.5;
	boolean spring_select, break_springs;
	Particle s_closest, s_connection = null;
	float likeRepulse;
	Grid grid;
	ShapeInput si;
	boolean shapeFile = true;
	
	// initialize stuff
	public Simulation(MainGUI gui) {
		this.gui = gui;
		grid = new Grid(this);
		//size(width, height);
		if (!shapeFile) {
			for (int i = 0; i < particleCount; i++) {
				//create random particles; position is [0,1) and velocity is [-1,1)
				Particle part;
				part = new Particle(this, rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), (float)(rand.nextFloat()*2-1), (float)(rand.nextFloat()*2-1), (float)(rand.nextFloat()*2-1));
				parts.add(part);
			}
		}
		if (shapeFile) {
			si = new ShapeInput();
			for (int i = 0; i < si.xyz.size(); i++) {
				PVector coord = si.xyz.get(i);
				parts.add(new Particle(this, (float) ((coord.x+1)/8+0.375), (float) ((coord.y+1)/8+0.375), (float) ((coord.z+1)/8+0.5), 0, 0, 0));
			}
			for (int j = 0; j < si.springs.size(); j++) {
				//the index of the three points in the face
				int one = (int) si.springs.get(j).x;
				int two = (int) si.springs.get(j).y;
				int three = (int) si.springs.get(j).z;
				
				calcNormal(si);
				
				Spring s1 = new Spring(parts.get(one), parts.get(two));
				parts.get(one).springs.add(s1);
				parts.get(two).springs.add(s1);
				Spring s2 = new Spring(parts.get(two), parts.get(three));
				parts.get(two).springs.add(s2);
				parts.get(three).springs.add(s2);
				Spring s3 = new Spring(parts.get(three), parts.get(one));
				parts.get(three).springs.add(s3);
				parts.get(one).springs.add(s3);
			}
		}
		
	}
	
	// Draw the scene
	public void draw() {
		//sets the background color to grey
		gui.fill(192,192,192);
		gui.rect(0, 0, width, height);
		//gui.background(192,192,192);
		
		
	  
		//step forward in time
		time += delta_t;
	  
		if (grid.repulsion_radius != radius) {
			grid.updateGrid();
			for (int i = 0; i < grid.num_width; i++) {
				for (int j = 0; j < grid.num_height; j++) {
					for (int k = 0; k < grid.num_depth; k++) {
						grid.blocks[i][j][k].addNeighbors();
					}
				}
			}
		}
		else {
			grid.clear();
		}
		
		for (Particle p : parts) {
			p.assignBlock();
		}

		for (Particle p : parts) {
			p.update();
		}
		
		sort();
		calcNormal(si);
		
		if (mousedown) {
			//if the user currently has control of this particle
			if (gui.mouseX < Simulation.width && gui.mouseY < Simulation.height && user_control) {
				closest.pos.x = gui.mouseX;
				closest.pos.y = gui.mouseY;
				closest.draw();
			}
		}
	
		Particle newpart = new Particle(this, 0, 0, 0, 0, 0, 0);
		for (Particle part : parts) {
			//if not the particle the user is currently controlling, draw it
			if (mousedown && part != closest) {
				gui.fill(255, 255, 255);
				part.draw();
			}
			//if the mouse is not down, draw all of them
			else if (!mousedown) {
				if (part.particleId == 12) {
					gui.fill(255, 00, 00);
					//newpart = part.divide();
					//part.draw();
				}
				else {
					int temp = (int) (part.pos.z/depth*255);
					gui.fill(temp, temp, temp);
					part.draw();
				}
			}
		}
		//parts.add(newpart);
		
	}
	
	//keyboard commands
	public void keyPressed() {
	}
	
	@SuppressWarnings("static-access")
	public void mousePressed() {
		//USER CONTROL
		//if mouse is clicked within the simulation only
		if (gui.mouseX < Simulation.width && gui.mouseY < Simulation.height && user_control) {
			closest = parts.get(0);
			//find closest particle
			for (Particle p : parts) {
				float dist = gui.dist(gui.mouseX, gui.mouseY, p.pos.x, p.pos.y);
				if (dist < gui.dist(gui.mouseX, gui.mouseY, closest.pos.x, closest.pos.y))
					closest = p;
			}
			mousedown = true;
		}
		
		if ((gui.mouseX > Simulation.width || gui.mouseY > Simulation.height || gui.mouseX < 0 || gui.mouseY < 0) && user_control) {
			mousedown = false;
		}
		
		
		//SPRINGS BETWEEN PARTICLES
		if (gui.mouseX < Simulation.width && gui.mouseY < Simulation.height) {
			if (s_closest == null) {
				s_closest = parts.get(0);
				for (Particle p : parts) {
					float dist = gui.dist(gui.mouseX, gui.mouseY, p.pos.x, p.pos.y);
					if (dist < gui.dist(gui.mouseX, gui.mouseY, s_closest.pos.x, s_closest.pos.y))
						s_closest = p;
				}
			}
			
			else if (s_closest != null) {
				s_connection = parts.get(0);
				for (Particle p : parts) {
					float dist = gui.dist(gui.mouseX, gui.mouseY, p.pos.x, p.pos.y);
					if (dist < gui.dist(gui.mouseX, gui.mouseY, s_connection.pos.x, s_connection.pos.y))
						s_connection = p;
				}
			}
		}
		
		//CREATE SPRING BETWEEN PARTICLES
		if (s_closest != s_connection && s_connection != null && s_closest != null && spring_select && !break_springs) {
			Spring spr = new Spring(s_closest, s_connection);
			s_closest.springs.add(spr);
			s_connection.springs.add(spr);
		}
		
		//BREAK SPRINGS BETWEEN PARTICLES
		if (s_closest != s_connection && s_connection != null && s_closest != null && break_springs && !spring_select) {
			breakSprings(s_closest, s_connection);
		}
		
		//CLEAR TWO SELECTED PARTICLES
		if (s_closest != null && s_connection != null) {
			s_closest = null;
			s_connection = null;
		}
		
	}
	
	public void mouseReleased() {
		if (gui.mouseX < Simulation.width && gui.mouseY < Simulation.height && gui.mouseX > 0 && gui.mouseY > 0)
			mousedown = false;
	}
	
	public void updateParticleCount(int newCount) {
		if (!shapeFile) {
			if (particleCount < parts.size()) { //delete some particles randomly
				int difference = parts.size() - particleCount;
				for (int i = 0; i < difference; i++) {
					int index = rand.nextInt(parts.size());
					Particle removeMe = parts.get(index);
			
					if (removeMe.springs.size() > 0) { //if the particle we're removing has springs
						for (Spring s : removeMe.springs) { //go through every spring and remove it from the other particle
							if (s.getP1().equals(removeMe)) {//if this is p1, remove from p2's list
								//get P2's springs and find the one that has the one we're removing
								for (int j = 0; j < s.getP2().springs.size(); j ++) {
									if (s.equals(s.getP2().springs.get(j))) {
										s.getP2().springs.remove(j);
									}
								}
							}
							else if (s.getP2().equals(removeMe)) {
								for (int j = 0; j < s.getP1().springs.size(); j ++) {
									if (s.equals(s.getP1().springs.get(j))) {
										s.getP1().springs.remove(j);
									}
								}
							}
						}
					}
					parts.remove(index);
				}
			}
			else if (particleCount > parts.size()) { //add more particles
				int difference = particleCount - parts.size();
				for (int i = 0; i < difference; i++) {
					Particle part;
					part = new Particle(this, rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), (float)(rand.nextFloat()*2-1), (float)(rand.nextFloat()*2-1), (float)(rand.nextFloat()*2-1));
					parts.add(part);
				}
			}
		}
	}
	
	public void breakSprings(Particle p1, Particle p2) {
		for (int i = 0; i < p1.springs.size(); i++) { //delete the spring with p2 from p1's spring list
			Spring temp = p1.springs.get(i);
			if (temp.getP1().equals(p2) || temp.getP2().equals(p2)) {
				p1.springs.remove(i);
			}
		}
		for (int i = 0; i < p2.springs.size(); i++) {
			Spring temp = p2.springs.get(i);
			if (temp.getP1().equals(p1) || temp.getP2().equals(p1)) {
				p2.springs.remove(i);
			}
		}
	}
	
	public void sort() {
		Collections.sort(parts, new Comparator<Particle>() {
	    @Override
	    public int compare(Particle  p1, Particle  p2) {
	            return  p1.compareTo(p2);
	        }
	    });
	}
	
	public void calcNormal(ShapeInput si) {
		for (Particle p : parts) {
			p.normal = new PVector(0, 0, 0);
		}
		for (int j = 0; j < si.springs.size(); j++) {
			//the index of the three points in the face
			int one = (int) si.springs.get(j).x;
			int two = (int) si.springs.get(j).y;
			int three = (int) si.springs.get(j).z;
			
			PVector v1 = new PVector(parts.get(one).pos.x - parts.get(two).pos.x, parts.get(one).pos.y - parts.get(two).pos.y, parts.get(one).pos.z - parts.get(two).pos.z);
			PVector v2 = new PVector(parts.get(three).pos.x - parts.get(two).pos.x, parts.get(three).pos.y - parts.get(two).pos.y, parts.get(three).pos.z - parts.get(two).pos.z);
			PVector face_norm = v1.cross(v2); //normal of the face
			
			face_norm = PVector.div(face_norm, face_norm.mag());
			
			parts.get(one).normal.add(face_norm);
			parts.get(two).normal.add(face_norm);
			parts.get(three).normal.add(face_norm);
		}
		
		for (Particle p : parts) {
			p.normal = PVector.div(p.normal, p.normal.mag());
		}
		
	}
	
}
