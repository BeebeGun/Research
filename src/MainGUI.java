import processing.core.PApplet;


public class MainGUI extends PApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Controls con;
	public Simulation sim;
	public static final int WIDTH = 700, HEIGHT = 500;
	boolean load;
	boolean single_step_last;
	
	public void setup() {
		size(WIDTH, HEIGHT, P3D); //OPENGL
		ortho(0, width, 0, height, 0, 10000);
		background(90);
		//camera();
		
		sim = new Simulation(this);
		con = new Controls(MainGUI.this);
		
	}
	
	public void draw() {
		updateValues();
		
		//translate(400, 0);
		con.draw();
		
		if (load) {
			sim.draw();
			load = false;
		}
		
		if (single_step_last != con.single_step) 
			sim.draw();
			
		single_step_last = con.single_step;
		
		translate(0, 0);
		if (!con.pause) {
			//pushMatrix();
			//rotateY(radians(con.rotation_angle));
			sim.draw();
			//popMatrix();
		}

		if(con.pause && con.single_step) {
			//this.redraw();
			load = false;
			con.single_step = false;
			con.cp5.getController("Single Step").setValue(0);
		}
		
	}
	
	public void mousePressed() {
		sim.mousePressed();
	}
	
	public void keyPressed() {
		
		sim.keyPressed();
	}
	
	public void mouseReleased() {
		sim.mouseReleased();
	}
	
	public void updateValues() {
		sim.inellastic = con.inellasticCollision;
		sim.gravity = con.gravity;
		sim.radius = con.radius_float;
		sim.particleCount = con.particleCount;
		sim.repulsion = con.repulsion;
		sim.damp = con.damp_float;
		sim.grav_mag = con.gravity_mag;
		sim.pause = con.pause;
		sim.user_control = con.user_control_bool;
		sim.spring_select = con.spring_select;
		sim.spring_constant = con.spring_constant;
		sim.spring_damp = con.spring_damp;
		sim.break_springs = con.spring_delete;
		sim.repulsion_strength = con.repulsion_strength;
		sim.planar = con.planar;
		sim.bulge = con.bulge;
		sim.rotation_angle = con.rotation_angle;
	}
}
