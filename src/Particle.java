import java.util.ArrayList;

import processing.core.*;



public class Particle {

	Simulation env;
	private PApplet parent;
	PVector pos;
	PVector v;
	PVector a;
	String type = "Particle";
	ArrayList<Spring> springs = new ArrayList<Spring>();
	private static int staticParticleId = 0;
	int particleId;
	PVector normal;
	int block_x;
	int block_y;
	
	
  @SuppressWarnings("static-access")
public Particle(Simulation env, float x, float y, float v_x, float v_y) {
	  this.env = env;
	  this.parent = env.gui;
	  pos = new PVector((x * env.width), (y * env.height));
	  if (pos.x > (env.height-1.5)) pos.x -= 1.5;
	  v = new PVector(v_x * env.width, v_y * env.height);
	  a = new PVector(0,0);
	  
	  staticParticleId++;
	  this.particleId = staticParticleId;
  }
  

void draw() {
    
	if (pos.x < parent.width && pos.y < parent.height)
		parent.ellipse(pos.x, pos.y, 5, 5);

	for (Spring s : springs) {
		parent.line(s.getP1().pos.x, s.getP1().pos.y, s.getP2().pos.x, s.getP2().pos.y);
		parent.line((s.getP1().pos.x+s.getP2().pos.x)/2, (s.getP1().pos.y+s.getP2().pos.y)/2, (s.getP1().pos.x+s.getP2().pos.x)/2+s.normal.x*10, (s.getP1().pos.y+s.getP2().pos.y)/2+s.normal.y*10);
	}

	if(normal != null) {
		parent.line(pos.x, pos.y, pos.x+(normal.x*50), pos.y+(normal.y*50));
	}
	
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
	  if (env.repulsion && env.grid!=null) {
		  //for every particle in the current blocks neighboring blocks
		  for(Block block : env.grid.getBlock(block_x, block_y).neighbors) {
			  //if (block != null) {
				  for (Particle p : block.parts) {
					  float dist = parent.dist(pos.x, pos.y, p.pos.x, p.pos.y);
					  if (dist > 0 && dist <= env.radius) {
						  //REPULSE! 
						  float s_max = (float) ((env.radius-dist)/env.radius); // range of s_max [0,1)
						  PVector pMinusQ = new PVector(pos.x - p.pos.x, pos.y - p.pos.y);
						  float pMinusQMag = pMinusQ.mag();
						  PVector force = PVector.div(pMinusQ, pMinusQMag);
						  force.mult(s_max);
						  force.mult(env.repulsion_strength);
						  a.add(force);
					  }
				  }
			  //}
			  //System.out.println(block);
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
	  }
	  
	  //CALCULATE PLANAR FORCE
	  if (env.planar && springs.size()!= 0) {
		  float sumx = 0;
		  float sumy = 0;
		  for (int i = 0; i < springs.size(); i++) { //for every spring
			  Particle part = null;
		    	if (springs.get(i).getP1() == this) //if this is p1 in the spring, get p2
		    		part = springs.get(i).getP2();
		    	else //if this is p2 in the spring, get p1
		    		part = springs.get(i).getP1();
			  sumx += part.pos.x;
			  sumy += part.pos.y;
		  }
		  sumx = sumx/(springs.size());
		  sumy = sumy/(springs.size());
		  
		  PVector avg_loc = new PVector(sumx, sumy);
		  
		  PVector forcevector = new PVector(avg_loc.x-pos.x, avg_loc.y - pos.y);
		  
		  float planar_mag = forcevector.mag();
		  PVector planar_unit = PVector.div(forcevector, planar_mag);
		  
		  PVector planar = PVector.mult(planar_unit, 200);
		  
		  a.add(planar);
  	  }
	  
	  
	//CALCULATE BULGE FORCE
	  if (env.bulge && springs.size() != 0) {
		  //find normal of springs
		  float sumx = 0;
		  float sumy = 0;
		  for (int i = 0; i < springs.size(); i++) {
			  if (springs.get(i).normal != null) {
				  				  
				  sumx += springs.get(i).normal.x;
				  sumy += springs.get(i).normal.y;
			  }
			  //if (this.equals(env.parts.get(0)))
			//	  System.out.println("Normal: " + line);
		  }
		  
		  sumx /= springs.size();
		  sumy /= springs.size();
		  normal = new PVector(sumx, sumy);
		  normal.normalize(); //unnecessary if we normalize before finding the average
		  
		  float tsum = 0;
		  
		  for (int i = 0; i < springs.size(); i++) {
			  Particle part = null;
			  if (springs.get(i).getP1() == this) //if this is p1 in the spring, get p2
				  part = springs.get(i).getP2();
			  else //if this is p2 in the spring, get p1
				  part = springs.get(i).getP1();
			  PVector l = PVector.sub(part.pos, pos);
			  float l_mag = l.mag();
			  float ldotN = PVector.dot(l, normal);
			  float phi = (float) Math.asin(ldotN/l_mag);
			  float r = springs.get(i).getRestLength();
			  
			  if (Math.abs(l_mag*Math.sin(Math.PI-phi)) < r) {
				  
				  float lsinPhi = (float) (l_mag*Math.sin(phi));

				  float theta = (float) (-(Math.asin(lsinPhi/r))-phi+Math.PI);
				  
				  float t = (float) (r*Math.sin(theta)/Math.sin(phi));
				  
				  if (Math.abs(lsinPhi/r) > 1)
					  parent.exit();
				  tsum += t;
			  }
		  }
		  
		  tsum /= springs.size();
		  PVector targetpos = new PVector(pos.x + tsum*normal.x, pos.y + tsum*normal.y);
		  
		  PVector bulgeforce = PVector.sub(targetpos, pos);
		  
		  a.add(bulgeforce);
  	  }
	  
	  

	  PVector f_temp = new PVector((-env.damp*3)*v.x, (-env.damp*3)*v.y);
	  a.add(f_temp);

	  calcVelocity();
	  
	  }
  
  public void assignBlock() {
	  if (env.radius > 0 && pos.x < env.width && pos.y < env.height && pos.x > 0 && pos.y > 0) {
		  //get the new block in the grid
		  block_x = (int) Math.floor(pos.x/env.radius);
		  block_y = (int) Math.floor(pos.y/env.radius);
		  env.grid.getBlock(block_x, block_y).parts.add(this);
	  }
  }
  
  public boolean equals(Particle p) {
	  if (particleId == p.particleId)
		  return true;
	  return false;
  }
  
  public String toString() {
	    String str = "Position: (" + pos.x + ", " + pos.y + ")";
	    str += ("\nBlock: " + block_x + ", " + block_y);
	    return str;
	  }
  
}
