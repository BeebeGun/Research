
import java.util.*;


public class Simulation {

	public MainGUI gui;
	float time = 0;
	float zoom = 1;
	public float delta_t = (float) 0.01; //0.01
	boolean pause;
	public static int width = 400;
	public static int height = 400;
	ArrayList<Particle> parts = new ArrayList<Particle>();
	Random rand = new Random();
	boolean gravity = true;
	boolean planar = true;
	boolean bulge = true;
	float grav_mag;
	float damp = (float) 0.95;
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
	boolean circle = false;
	Grid grid;
	
	// initialize stuff
	public Simulation(MainGUI gui) {
		this.gui = gui;
		grid = new Grid(this);
		//size(width, height);
		if (!circle) {
			for (int i = 0; i < particleCount; i++) {
				//create random particles; position is [0,1) and velocity is [-1,1)
				Particle part;
				part = new Particle(this, rand.nextFloat(), rand.nextFloat(), (float)((rand.nextFloat()*2-1)*0.0), (float)((rand.nextFloat()*2-1)*0.0));
				parts.add(part);
			}
		}
		else {
			//code to make a star
			for (int i = 0; i < 5; i++) {
				int degree = 360/5*i;
				float r = (float) 0.2;
				Particle part = new Particle(this, (float) (0.5 + Math.cos(Math.toRadians(degree))*r), (float) (0.5 + Math.sin(Math.toRadians(degree))*r), (float) 0.0, (float) 0.0);
				parts.add(part);
			}
			for (int i = 0; i < 5; i++) {
				int degree = 360/5*i + 36; //equilibrium at 36
				float r = (float) 0.1;
				Particle part = new Particle(this, (float) (0.5 + Math.cos(Math.toRadians(degree))*r), (float) (0.5 + Math.sin(Math.toRadians(degree))*r), (float) 0.0, (float) 0.0);
				parts.add(part);
			}
			Spring spr1 = new Spring(parts.get(9), parts.get(0));
			parts.get(0).springs.add(spr1);
			parts.get(9).springs.add(spr1);
			Spring spr2 = new Spring(parts.get(0), parts.get(5));
			parts.get(0).springs.add(spr2);
			parts.get(5).springs.add(spr2);
			Spring spr3 = new Spring(parts.get(5), parts.get(1));
			parts.get(5).springs.add(spr3);
			parts.get(1).springs.add(spr3);
			Spring spr4 = new Spring(parts.get(1), parts.get(6));
			parts.get(1).springs.add(spr4);
			parts.get(6).springs.add(spr4);
			Spring spr5 = new Spring(parts.get(6), parts.get(2));
			parts.get(6).springs.add(spr5);
			parts.get(2).springs.add(spr5);
			Spring spr6 = new Spring(parts.get(2), parts.get(7));
			parts.get(2).springs.add(spr6);
			parts.get(7).springs.add(spr6);
			Spring spr7 = new Spring(parts.get(7), parts.get(3));
			parts.get(7).springs.add(spr7);
			parts.get(3).springs.add(spr7);
			Spring spr8 = new Spring(parts.get(3), parts.get(8));
			parts.get(3).springs.add(spr8);
			parts.get(8).springs.add(spr8);
			Spring spr9 = new Spring(parts.get(8), parts.get(4));
			parts.get(8).springs.add(spr9);
			parts.get(4).springs.add(spr9);
			Spring spr10 = new Spring(parts.get(4), parts.get(9));
			parts.get(4).springs.add(spr10);
			parts.get(9).springs.add(spr10);
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
					grid.blocks[i][j].addNeighbors();
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
		
		if (mousedown) {
			//if the user currently has control of this particle
			if (gui.mouseX < Simulation.width && gui.mouseY < Simulation.height && user_control) {
				closest.pos.x = gui.mouseX;
				closest.pos.y = gui.mouseY;
				closest.draw();
			}
		}
	
		for (Particle part : parts) {
			//if not the particle the user is currently controlling, draw it
			if (mousedown && part != closest) {
				gui.fill(255, 255, 255);
				part.draw();
			}
			//if the mouse is not down, draw all of them
			else if (!mousedown) {
				gui.fill(255, 255, 255);
				part.draw();
			}
		}
		
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
		if (!circle) {
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
					part = new Particle(this, rand.nextFloat(), rand.nextFloat(), (float)((rand.nextFloat()*2-1)*0.0), (float)((rand.nextFloat()*2-1)*0.0));
					parts.add(part);
				}
			}
		}
		else {
			//if circle, do nothing
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
}
