import java.util.*;


public class Simulation {

	public MainGUI gui;
	float time = 0;
	float zoom = 1;
	public float delta_t = (float) 0.01;
	boolean pause;
	public static int width = 400;
	public static int height = 400;
	ArrayList<Particle> parts = new ArrayList<Particle>();
	Random rand = new Random();
	boolean gravity = true;
	float grav_mag;
	float damp = (float) 0.95;
	float inellastic = (float) 0.5;
	int particleCount = 2;
	//repulsion radius
	float radius;
	boolean repulsion = true;
	boolean single_step, user_control;
	Particle closest;
	boolean mousedown = false;
	//SPRING FORCES
	float spring_constant = (float) 5;
	float rest_length = (float) 0.25;
	float spring_damp = (float) 0.5;
	boolean spring_select;
	Particle s_closest, s_connection = null;
	
	// initialize stuff
	public Simulation(MainGUI gui) {
		this.gui = gui;
		//size(width, height);
	
		for (int i = 0; i < particleCount; i++) {
			//create random particles; position is [0,1) and velocity is [-1,1)
			Particle part = new Particle(this, rand.nextFloat(), rand.nextFloat(), (float)((rand.nextFloat()*2-1)*0.0), (float)((rand.nextFloat()*2-1)*0.0));
			parts.add(part);
		}
		/*Particle p0 = new Particle(0.25, 0.5, 0.25, 0);
	  	Particle p1 = new Particle(0.75, 0.49, -0.25, 0);
	  	parts.add(p0);
	  	parts.add(p1);*/
	  
	}
	
	// Draw the scene
	public void draw() {
		//sets the background color to grey
		gui.fill(192,192,192);
		gui.rect(0, 0, width, height);
		//gui.background(192,192,192);
		
		
	  
		//step forward in time
		time += delta_t;
	  
		//draw the test particle
		for (Particle part : parts) {
			if (mousedown && part != closest)
				part.draw();
			else if (!mousedown)
				part.draw();
			//TODO don't draw closest twice
		}
		
		if (mousedown) {
			if (gui.mouseX < Simulation.width && gui.mouseY < Simulation.height && user_control) {
				closest.pos.x = gui.mouseX;
				closest.pos.y = gui.mouseY;
				closest.draw();
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
		//CREATE SPRINGS BETWEEN PARTICLES
		if (gui.mouseX < Simulation.width && gui.mouseY < Simulation.height && spring_select) {
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
		if (s_closest != s_connection && s_connection != null && s_closest != null) {
			s_closest.connected.add(s_connection);
			s_connection.connected.add(s_closest);
			float rest_length_temp = s_closest.pos.dist(s_connection.pos);
			s_closest.rest_lengths.add(rest_length_temp);
			s_connection.rest_lengths.add(rest_length_temp);
		}
		if (s_closest != null && s_connection != null) {
			s_closest = null;
			s_connection = null;
		}
	}
	
	public void mouseReleased() {
		if (gui.mouseX < Simulation.width && gui.mouseY < Simulation.height)
			mousedown = false;
	}
	
	public void mouseDragged() {
		if (gui.mouseX < Simulation.width && gui.mouseY < Simulation.height && user_control) {
			closest.pos.x = gui.mouseX;
			closest.pos.y = gui.mouseY;
		}
	}
	
	public void updateParticleCount(int newCount) {
		if (particleCount < parts.size()) { //delete some particles randomly
			int difference = parts.size() - particleCount;
			for (int i = 0; i < difference; i++) {
				int index = rand.nextInt(parts.size());
				parts.remove(index);
			}
		}
		else if (particleCount > parts.size()) { //add more particles
			int difference = particleCount - parts.size();
			for (int i = 0; i < difference; i++) {
				//create random particles; position is [0,1) and velocity is [-1,1)
				Particle part = new Particle(this, rand.nextFloat(), rand.nextFloat(), (float)((rand.nextFloat()*2-1)*0.0), (float)((rand.nextFloat()*2-1)*0.0));
				parts.add(part);
			}
		}
	}
}
