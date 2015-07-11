import processing.core.PVector;


public class Face {

	Particle p1;
	Particle p2;
	Particle p3;
	PVector normal;
	
	public Face(Particle p1, Particle p2, Particle p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		
		//System.out.println("P1: " + p1);
		//System.out.println("P2: " + p2);
		//System.out.println("P3: " + p3);

		PVector v1 = PVector.sub(p1.pos, p2.pos);
		PVector v2 = PVector.sub(p3.pos, p2.pos);
		normal = v1.cross(v2);
		normal = PVector.div(normal, normal.mag());
	}
	
	public void update() {
		PVector v1 = PVector.sub(p1.pos, p2.pos);
		PVector v2 = PVector.sub(p3.pos, p2.pos);
		normal = v1.cross(v2);
		normal = PVector.div(normal, normal.mag());
	}
	
	public String toString() {
		String string = "Partile 1: " + p1.particleId + " ; Particle 2: " + p2.particleId + " ; Particle 3: " + p3.particleId;
		return string;
	}
	
	public void setP1(Particle p1) {
		this.p1 = p1;
		update();
	}
	
	public void setP2(Particle p2) {
		this.p2 = p2;
		update();
	}
	
	public void setP3(Particle p3) {
		this.p3 = p3;
		update();
	}
	
	public boolean equals(Face f) {
		return (contains(f.p1) && contains(f.p2) && contains(f.p3));
	}
	
	public boolean contains(Particle part) {
		return (p1.equals(part) || p2.equals(part) || p3.equals(part));
	}
}
