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
		size(WIDTH, HEIGHT);
		background(90);
		
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
		if (!con.pause)
			sim.draw();

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
	
	public void mouseDragged() {
		sim.mouseDragged();
	}
	
	public void keyPressed() {
		
		sim.keyPressed();
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
	}
}
