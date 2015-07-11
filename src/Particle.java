import java.awt.Color;
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
	int block_z;
	int r_max = 10;
	int r_min = 2;
	Color color = new Color(0, 0, 0);
	
	
  @SuppressWarnings("static-access")
public Particle(Simulation env, float x, float y, float z, float v_x, float v_y, float v_z) {
	  this.env = env;
	  this.parent = env.gui;
	  pos = new PVector((x * env.width), (y * env.height), (z* env.depth));
	  v = new PVector(v_x * env.width, v_y * env.height, v_z * env.depth);
	  a = new PVector(0,0,0);
	  normal = new PVector(0, 0, 0);
	  
	  staticParticleId++;
	  this.particleId = staticParticleId;
  }
  

	@SuppressWarnings("static-access")
	void draw() {
		
		for (Spring s : springs) {
			parent.line(s.getP1().pos.x, s.getP1().pos.y, s.getP2().pos.x, s.getP2().pos.y);
			//parent.line((s.getP1().pos.x+s.getP2().pos.x)/2, (s.getP1().pos.y+s.getP2().pos.y)/2, (s.getP1().pos.x+s.getP2().pos.x)/2+s.normal.x*10, (s.getP1().pos.y+s.getP2().pos.y)/2+s.normal.y*10);
		}
	    
		if (pos.x < env.width && pos.x > 0 && pos.y < env.height && pos.y > 0) {
			float p_radius = pos.z/env.depth*(r_max - r_min) + r_min;
			parent.ellipse(pos.x, pos.y, p_radius, p_radius);
		}
		
		System.out.println("Pos: " + pos.x + ", " + pos.y);
		
		/*if(normal != null) {
			parent.line(pos.x, pos.y, pos.x+(normal.x*50), pos.y+(normal.y*50));
		}*/
		
	}
  
  @SuppressWarnings("static-access")
void calcVelocity() {
    float new_vx = v.x + (env.delta_t * a.x);
    float new_vy = v.y + (env.delta_t * a.y);
    float new_vz = v.z + (env.delta_t * a.z);
    v.set(new_vx, new_vy, new_vz);
    
  //if particle hits a vertical wall, negate velocity in the x direction
    if (pos.x <= 1.5 - env.width/env.scalefactor && v.x < 0) {
      new_vx = v.x * -(1-env.inellastic);
      v.set(new_vx, v.y, v.z);
    }
    else if (pos.x >= env.width-1.5 + env.width/env.scalefactor && v.x > 0) { //*scalefactor
      new_vx = v.x * -(1-env.inellastic);
      v.set(new_vx, v.y, v.z);
    }
    //if particle hits a horizontal wall, negate velocity in the y direction
    if (pos.y <= 1.5 - env.height/env.scalefactor && v.y < 0) {
      new_vy = v.y * -(1-env.inellastic);
      v.set(v.x, new_vy, v.z); 
    }
    else if (pos.y >= env.height-1.5 + env.height/env.scalefactor && v.y > 0) { //*scalefactor
      new_vy = v.y * -(1-env.inellastic);
      v.set(v.x, new_vy, v.z);
    }
    //if particle reaches maximum depth
    if (pos.z <= 1.5 - env.depth/env.scalefactor && v.z < 0) {
    	new_vz = v.z * -(1-env.inellastic);
    	v.set(v.x, v.y, new_vz);
    }
    else if (pos.z >= env.depth + env.depth/env.scalefactor && v.z > 0) { //*scalefactor
    	new_vz = v.z * -(1-env.inellastic);
    	v.set(v.x, v.y, new_vz);
    }
     
    float new_x = pos.x + (env.delta_t * v.x);
    float new_y = pos.y + (env.delta_t * v.y);
    float new_z = pos.z + (env.delta_t * v.z);
    pos.set(new_x, new_y, new_z);
  }
  
  @SuppressWarnings("static-access")
public void update() {
	  a = new PVector(0,0,0);

	  //GRAVITY
	  if (env.gravity) {
		  PVector f_grav = new PVector(0, env.grav_mag * env.height, 0);
		  a.add(f_grav);
	  }

	  //REPULSION
	  if (env.repulsion && env.grid != null) {
		  //for every particle in the current blocks neighboring blocks
		  for(Block block : env.grid.getBlock(block_x, block_y, block_z).neighbors) {
			  for (Particle p : block.parts) {
				  float dist = parent.dist(pos.x, pos.y, pos.z, p.pos.x, p.pos.y, p.pos.z);
				  if (dist > 0 && dist <= env.radius) {
					  //REPULSE! 
					  float s_max = (float) ((env.radius-dist)/env.radius); // range of s_max [0,1)
					  PVector pMinusQ = new PVector(pos.x - p.pos.x, pos.y - p.pos.y, pos.z - p.pos.z);
					  float pMinusQMag = pMinusQ.mag();
					  PVector force = PVector.div(pMinusQ, pMinusQMag);
					  force.mult(s_max);
					  force.mult(env.repulsion_strength);
					  a.add(force);
				  }
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
	    	PVector l = new PVector(pos.x - part.pos.x, pos.y - part.pos.y, pos.z - part.pos.z);
	    	float l_mag = l.mag();
	    	PVector unit_l = PVector.div(l, l_mag);
	    	float s_mag = -env.spring_constant*(l_mag-springs.get(i).getRestLength());
	    	PVector l_deriv = new PVector(v.x - part.v.x, v.y - part.v.y, v.z - part.v.z);
	    	float temp_l = l.dot(l_deriv);
	    	temp_l = temp_l/l_mag;
	    	float damping_term = -temp_l*env.spring_damp;
	    	float f_spring_mag = (s_mag+damping_term);
	    	PVector f_spring = PVector.mult(unit_l, f_spring_mag); //f_spring_mag with damping; s_mag without
	    	//f_spring = PVector.mult(f_spring, 5);

		  a.add(f_spring);
	  }
	  
	  
	  
	  //CALCULATE PLANAR FORCE
	  if (env.planar && springs.size()!= 0) {
		  float sumx = 0;
		  float sumy = 0;
		  float sumz = 0;
		  for (int i = 0; i < springs.size(); i++) { //for every spring
			  Particle part = null;
		    	if (springs.get(i).getP1() == this) //if this is p1 in the spring, get p2
		    		part = springs.get(i).getP2();
		    	else //if this is p2 in the spring, get p1
		    		part = springs.get(i).getP1();
			  sumx += part.pos.x;
			  sumy += part.pos.y;
			  sumz += part.pos.z;
		  }
		  sumx = sumx/(springs.size());
		  sumy = sumy/(springs.size());
		  sumz = sumz/(springs.size());
		  
		  PVector avg_loc = new PVector(sumx, sumy, sumz);
		  
		  PVector forcevector = new PVector(avg_loc.x-pos.x, avg_loc.y - pos.y, avg_loc.z - pos.z);
		  
		  float planar_mag = forcevector.mag();
		  PVector planar_unit = PVector.div(forcevector, planar_mag);
		  
		  PVector planar = PVector.mult(planar_unit, 2500);
		  
		  a.add(planar);
  	  }
	  
	  /*
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
	  
	  */

	  PVector f_temp = new PVector((-env.damp*3)*v.x, (-env.damp*3)*v.y, (-env.damp*3)*v.z);
	  a.add(f_temp);

	  calcVelocity();
	  
	  }
  
  @SuppressWarnings("static-access")
  public void assignBlock() {
	  if (env.radius > 0 && pos.x < env.width && pos.y < env.height && pos.x > 0 && pos.y > 0 && pos.z < env.depth && pos.z > 0) {
		  //get the new block in the grid
		  block_x = (int) Math.floor(pos.x/env.radius);
		  block_y = (int) Math.floor(pos.y/env.radius);
		  block_z = (int) Math.floor(pos.z/env.radius);
		  env.grid.getBlock(block_x, block_y, block_z).parts.add(this);
	  }
  }
  
  public boolean equals(Particle p) {
	  if (particleId == p.particleId)
		  return true;
	  return false;
  }
  
  public String toString() {
	    String str = "Position: (" + pos.x + ", " + pos.y + ", " + pos.z + ")";
	    //str += ("\nBlock: " + block_x + ", " + block_y);
	    return str;
	  }
  

  //returns the farthest back particle
	public int compareTo(Particle p1) {
		if (p1 == null)
			System.out.println("Particle is NULL");
		if (pos.z == p1.pos.z)
			return 0;
		else if (pos.z < p1.pos.z)
			return -1;
		else
			return 1;
	}
	
	@SuppressWarnings("static-access")
	public Particle divide() {
		calcNormal();
		
		Particle normpart = new Particle(env, (pos.x + 50*normal.x)/env.width, (pos.y + 50*normal.y)/env.height, (pos.z + 50*normal.z)/env.depth, 0, 0, 0);
		//random seed for testing purposes
		parent.randomSeed(15);
		Particle randpart;
		do {
			float x = parent.random(-20, 20);
			float y = parent.random(-20, 20);
			float z = parent.random(-20, 20);
			
			randpart = new Particle(env, (pos.x + x)/env.width, (pos.y + y)/env.height, (pos.z + z)/env.depth, 0, 0, 0);
		
		} while (PVector.dist(pos, randpart.pos) >= 20);
		
		
		PVector v1 = PVector.sub(normpart.pos, pos);
		PVector v2 = PVector.sub(randpart.pos, pos);
		
		PVector planeNormal = v1.cross(v2); //normal of the plane
		planeNormal = PVector.div(planeNormal, planeNormal.mag());
		
		//find d
		float a = planeNormal.x;
		float b = planeNormal.y;
		float c = planeNormal.z;
		float d = -(a*pos.x + b*pos.y + c*pos.z);
		/*System.out.println("a " + a);
		System.out.println("b " + b);
		System.out.println("c " + c);
		System.out.println("d " + d);*/
		
		Particle dlp1 = null; //dotted line particle 1
		Particle dlp2 = null; //dotted line particle 2
		
		//find faces with this particle
		ArrayList<Face> partfaces = new ArrayList<Face>();
		for (int i = 0; i < env.faces.size(); i++) {
			env.faces.get(i).update();
			//System.out.println(env.faces.get(i));
			if (env.faces.get(i).p1.particleId == this.particleId) {
				partfaces.add(env.faces.get(i));
				Particle part2 = env.faces.get(i).p2;
				Particle part3 = env.faces.get(i).p3;
				float p2val = a*part2.pos.x + b*part2.pos.y + c*part2.pos.z + d;
				float p3val = a*part3.pos.x + b*part3.pos.y + c*part3.pos.z + d;
				
				//System.out.println(i + " P1");
				//System.out.println("P2VAL = " + p2val);
				//System.out.println("P3VAL = " + p3val);
				
				if (p2val > 0 && p3val < 0) { //p2 is positive and p3 is negative
					if (dlp1 == null) 
						dlp1 = part2;
					else
						dlp2 = part2;
				}
				else if (p2val < 0 && p3val > 0) { //p2 is negative and p3 is positive
					if (dlp1 == null) 
						dlp1 = part3;
					else
						dlp2 = part3;
				}
			}
			else if (env.faces.get(i).p2.particleId == this.particleId) {
				partfaces.add(env.faces.get(i));
				Particle part2 = env.faces.get(i).p1;
				Particle part3 = env.faces.get(i).p3;
				float p2val = a*part2.pos.x + b*part2.pos.y + c*part2.pos.z + d;
				float p3val = a*part3.pos.x + b*part3.pos.y + c*part3.pos.z + d;
				//System.out.println(i + " P2");
				//System.out.println("P2VAL = " + p2val);
				//System.out.println("P3VAL = " + p3val);
				
				if (p2val > 0 && p3val < 0) { //p2 is positive and p3 is negative
					if (dlp1 == null) 
						dlp1 = part2;
					else
						dlp2 = part2;
				}
				else if (p2val < 0 && p3val > 0) { //p2 is negative and p3 is positive
					if (dlp1 == null) 
						dlp1 = part3;
					else
						dlp2 = part3;
				}
			}
			else if (env.faces.get(i).p3.particleId == this.particleId) {
				partfaces.add(env.faces.get(i));
				Particle part2 = env.faces.get(i).p1;
				Particle part3 = env.faces.get(i).p2;
				float p2val = a*part2.pos.x + b*part2.pos.y + c*part2.pos.z + d;
				float p3val = a*part3.pos.x + b*part3.pos.y + c*part3.pos.z + d;
				
				//System.out.println(i + " P3");
				//System.out.println("P2VAL = " + p2val);
				//System.out.println("P3VAL = " + p3val);
				
				if (p2val > 0 && p3val < 0) { //p2 is positive and p3 is negative
					if (dlp1 == null) 
						dlp1 = part2;
					else
						dlp2 = part2;
				}
				else if (p2val < 0 && p3val > 0) { //p2 is negative and p3 is positive
					if (dlp1 == null) 
						dlp1 = part3;
					else
						dlp2 = part3;
				}
			}
		}
		
		if (dlp1 == null && dlp2 == null) {
			System.out.println("Could not divide");
			return null;
		}
		
		if (dlp1 == dlp2) { //only one dotted line point
			//System.out.println("One point");
			return null;
		}
		
		//check all of the faces to see if dlp1 and dlp2 are a part of the same face
		if (dlp1 != null && dlp2 != null) {
			for (Face f : env.faces) {
				if (f.contains(dlp1) && f.contains(dlp2)) {
					return null;
				}
			}
		}
		
		//find the particles to which the springs should be broken
		ArrayList<Particle> disconnects = new ArrayList<Particle>();
		
		float springlen = 0;
		for (int i = 0; i < springs.size(); i++) {
			springlen = springs.get(i).getRestLength();
			Particle part = null; //the other particle in the Spring connected to this particle
	    	if (springs.get(i).getP1() == this) //if this is p1 in the spring, get p2
	    		part = springs.get(i).getP2();
	    	else //if this is p2 in the spring, get p1
	    		part = springs.get(i).getP1();
	    	
	    	float pval = a*part.pos.x + b*part.pos.y + c*part.pos.z + d;
	    	if (pval < 0) { //if on the negative side of the plane, disconnect the spring
	    		//if (!disconnects.contains(part))
	    			disconnects.add(part);
	    		springs.remove(i);
	    		for (int j = 0; j < part.springs.size(); j++) {
	    			if (part.springs.get(j).getP1() == this || part.springs.get(j).getP2() == this) {
	    				part.springs.remove(j);
	    			} //find the same spring in the other Particles list and remove it
	    		}
	    		i--;
	    	}
		}
		
		System.out.println("Disconnects size: " + disconnects.size());
		
		if (disconnects.size() < 4 || disconnects.size() >= 8) {
			System.out.println("Too low of valence");
			//TODO reconnect springs
			for (Particle p : disconnects) {
				Spring reconnect = new Spring(p, this, springlen);
				p.springs.add(reconnect);
				springs.add(reconnect);
			}
			return null;
		}
		
		//System.out.println("Size of disconnects: " + disconnects.size());
	
		Particle newpart; //make sure position is [0, 1]
		PVector average_location = new PVector();
		average_location = PVector.add(average_location, pos);
		for (int k = 0; k < disconnects.size(); k++) {
			average_location = PVector.add(average_location, disconnects.get(k).pos);
		}
		average_location = PVector.div(average_location, disconnects.size()+1);
		newpart = new Particle(env, average_location.x/env.width, average_location.y/env.height, average_location.z/env.depth, 0, 0, 0);
			
		//find the faces that need to be changed
		ArrayList<Face> change = new ArrayList<Face>();
		for (Face f : partfaces) {
			for (int i = 0; i < disconnects.size(); i++) {
				if (f.contains(this) && f.contains(disconnects.get(i)) && !change.contains(f)) { //if this face has the initial part and a part that was disconnected
					change.add(f);
				}
			}
			
		}
		
		for (int i = 0; i < env.faces.size(); i++) {
			Face f = env.faces.get(i);
			for (int j = 0; j < change.size(); j++) {
				if (env.faces.get(i).equals(change.get(j))) {
					//this face is the one that needs to be changed
					if (f.p1.equals(this)) { //change p1 to the new particle
						f.p1 = newpart;
					}
					else if (f.p2.equals(this)) {
						f.p2 = newpart;
					}
					else {
						f.p3 = newpart;
					}
					
				}
			}
		}
		
		
		//add two more triangles
		Face newface1 = new Face(this, dlp1, newpart);
		//Face newface1 = new Face(newpart, dlp1, this);
		env.faces.add(newface1);

		if (dlp2 != null) {
			Face newface2 = new Face(this, newpart, dlp2);
			//Face newface2 = new Face(dlp2, newpart, this);
			env.faces.add(newface2);
		}
		
		//ADD SPRINGS
		
		//add the spring from this particle -> newpart
		Spring s1 = new Spring(this, newpart, springlen);
		springs.add(s1);
		newpart.springs.add(s1);
		
		//add springs to dotted line particles
		Spring s2 = new Spring(newpart, dlp1, springlen);
		newpart.springs.add(s2);
		dlp1.springs.add(s2);
		
		if (dlp2 != null) {
			Spring s3 = new Spring(newpart, dlp2, springlen);
			newpart.springs.add(s3);
			dlp2.springs.add(s3);
		}
		
		//add the springs to the disconnected particles
		for (int i = 0; i < disconnects.size(); i++) {
			Spring temp = new Spring(newpart, disconnects.get(i));
			newpart.springs.add(temp);
			disconnects.get(i).springs.add(temp);
		}
		color = new Color(255, 00, 00); //the red particle is the divided one
			
		return newpart;
	}
  
	void calcNormal() {
		PVector average = new PVector(0,0,0);
		int count = 0;
		for (int i = 0; i < env.faces.size(); i++) {
			Face face = env.faces.get(i);
			face.update();
			if (face.p1 == this || face.p2 == this || face.p3 == this) {
				average = PVector.add(average, face.normal);
				count++;
			}
		}
		if (count == 0) 
			System.out.println("Error, this particle has no adjacent faces.");
		this.normal = PVector.div(average, count);
		this.normal = PVector.div(normal, normal.mag());
	}
	
}
