

public class Spring {
	
	private Particle p1;
	private Particle p2;
	private float rest_length;

	public Spring(Particle p1, Particle p2) {
		this.p1 = p1;
		this.p2 = p2;
		rest_length = p1.pos.dist(p2.pos);
	}

	public Particle getP1() {
		return p1;
	}

	public Particle getP2() {
		return p2;
	}

	public float getRestLength() {
		return rest_length;
	}

}