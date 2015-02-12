import java.util.ArrayList;

import processing.core.*;



public class Particle {

	Simulation env;
	private PApplet parent;
	PVector pos;
	PVector v;
	PVector a;
	PVector repulse;
	ArrayList<Spring> springs = new ArrayList<Spring>();
	private static int staticParticleId = 0;
	int particleId;
	
	
  @SuppressWarnings("static-access")
public Particle(Simulation env, float x, float y, float v_x, float v_y) {
	  this.env = env;
	  this.parent = env.gui;
	  pos = new PVector((x * env.width), (y * env.height));
	  if (pos.x > (env.height-1.5)) pos.x -= 1.5;
	  v = new PVector(v_x * env.width, v_y * env.height);
	  a = new PVector(0,0);
	  repulse = new PVector(0, 0);
	  
	  staticParticleId++;
	  this.particleId = staticParticleId;
  }
  

void draw() {
    
	if (pos.x < parent.width && pos.y < parent.height)
		parent.ellipse(pos.x, pos.y, 3, 3);

	for (Spring s : springs) {
		parent.line(s.getP1().pos.x, s.getP1().pos.y, s.getP2().pos.x, s.getP2().pos.y);
	}

  }
  
  public String toString() {
    String str = "Position: (" + pos.x + ", " + pos.y + ")";
    str += "\nVelocity: (" + v.x + ", " + v.y + v.y + ")";
    return str;
  }
  
  @SuppressWarnings("static-access")
void calcVelocity() {
    float new_vx = v.x + (env.delta_t * a.x);
    float new_vy = v.y + (env.delta_t * a.y);
    v.set(new_vx, new_vy);
    
  //if particle hits a vertical wall, negate velocity in the x direction
    if (pos.x <= 1.5 && v.x < 0) {
      new_vx = v.x * -(1-env.inellastic);
      v.set(new_vx, v.y);
    }
    else if (pos.x >= env.width-1.5 && v.x > 0) {
      new_vx = v.x * -(1-env.inellastic);
      v.set(new_vx, v.y);
    }
    //if particle hits a horizontal wall, negate velocity in the y direction
    if (pos.y <= 1.5 && v.y < 0) {
      new_vy = v.y * -(1-env.inellastic);
      v.set(v.x, new_vy); 
    }
    else if (pos.y >= env.height-1.5 && v.y > 0) {
      new_vy = v.y * -(1-env.inellastic);
      v.set(v.x, new_vy);
    }
     
    float new_x = pos.x + (env.delta_t * v.x);
    float new_y = pos.y + (env.delta_t * v.y);
    pos.set(new_x, new_y);
  }
  
  @SuppressWarnings("static-access")
public void update() {
	  a = new PVector(0,0);

	  if (env.gravity) {
		  PVector f_grav = new PVector(0, env.grav_mag * env.height);
		  a.add(f_grav);
	  }

	  //repulsion forces
	  if (env.repulsion) {
		  for (Particle p : env.parts) {
			  float dist = parent.dist(pos.x, pos.y, p.pos.x, p.pos.y);
			  if (dist > 0 && dist <= env.radius) {
				  //REPULSE! 
				  float s_max = (float) ((env.radius-dist)/env.radius); // range of s_max [0,1)
				  PVector pMinusQ = new PVector(pos.x - p.pos.x, pos.y - p.pos.y);
				  float pMinusQMag = pMinusQ.mag();
				  PVector force = PVector.div(pMinusQ, pMinusQMag);
				  force.mult(s_max);
				  force.mult(env.width*3);

				  a.add(force);

			  }
		  }
	  }

	  //calculate forces on particles connected by springs
	  for (int i = 0; i < springs.size(); i++) { //for every spring
	    	Particle part = null;
	    	if (springs.get(i).getP1() == this) //if this is p1 in the spring, get p2
	    		part = springs.get(i).getP2();
	    	else //if this is p2 in the spring, get p1
	    		part = springs.get(i).getP1();
	    	PVector l = new PVector(pos.x - part.pos.x, pos.y - part.pos.y);
	    	float l_mag = l.mag();
	    	PVector unit_l = PVector.div(l, l_mag);
	    	float s_mag = -env.spring_constant*(l_mag-springs.get(i).getRestLength());
	    	PVector l_deriv = new PVector(v.x - part.v.x, v.y - part.v.y);
	    	float temp_l = l.dot(l_deriv);
	    	temp_l = temp_l/l_mag;
	    	//float damping_term = temp_l*env.spring_damp;
	    	//float f_spring_mag = (s_mag+damping_term);
	    	PVector f_spring = PVector.mult(unit_l, s_mag); //f_spring_mag with damping
	    	//f_spring = PVector.mult(f_spring, 5);

		  a.add(f_spring);
		  //System.out.println(f_spring);
	  }

	  PVector f_temp = new PVector((-env.damp*3)*v.x, (-env.damp*3)*v.y);
	  a.add(f_temp);

	  calcVelocity();
  }
  
  public boolean equals(Particle p) {
	  if (particleId == p.particleId)
		  return true;
	  return false;
  }
  
}
