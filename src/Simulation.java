import java.util.*;

import processing.core.*;


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
	int particleCount;
	//repulsion radius
	float radius;
	boolean repulsion = true;
	boolean single_step, user_control;
	Particle closest;
	
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
		for (Particle part : parts)
			part.draw();
	
		//flips the y-axis so it starts at the bottom of the screen
		//gui.scale((float)1.0, (float)-1.0);
		//gui.translate(0, -height);
		
	
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
			System.out.println(closest);
		}
			
	}
	
	public void mouseDragged() {
		if (gui.mouseX < Simulation.width && gui.mouseY < Simulation.height && user_control) {
			closest.pos.x = gui.mouseX;
			closest.pos.y = gui.mouseY;
		}
	}
	
	public void updateParticleCount(int newCount) {
		// TODO Update the number of particles by either adding or removing particles
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
