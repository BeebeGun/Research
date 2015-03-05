
public class Grid {
	
	public MainGUI gui;
	public Simulation env;
	float repulsion_radius;
	Block[][] blocks;
	int num_width;
	int num_height;

	@SuppressWarnings("static-access")
	public Grid(Simulation env) {
		this.env = env;
		int sim_w = env.width;
		int sim_h = env.height;
		repulsion_radius = 30;
		//float repulsion_radius = env.radius;
		
		num_width = (int) Math.ceil((float)sim_w/repulsion_radius);
		num_height = (int) Math.ceil((float)sim_h/repulsion_radius);
		
		blocks = new Block[num_width][num_height];
		
		for (int i = 0; i < num_width; i++) {
			for (int j = 0; j < num_height; j++) {
				blocks[i][j] = new Block(env, i, j);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	public void updateGrid() {
		if (repulsion_radius != env.radius && env.radius > 0) {
			int sim_w = env.width;
			int sim_h = env.height;
			repulsion_radius = env.radius;
			
			num_width = (int) Math.ceil((float)sim_w/repulsion_radius);
			num_height = (int) Math.ceil((float)sim_h/repulsion_radius);
				
			blocks = new Block[num_width][num_height];
				
			for (int i = 0; i < num_width; i++) {
				for (int j = 0; j < num_height; j++) {
					blocks[i][j] = new Block(env, i, j);
				}
			}
			
		}
	}
	
	public Block getBlock(int i, int j) {
		return blocks[i][j];
	}

	public void clear() {
		for (int i = 0; i < num_width; i ++) {
			for (int j = 0; j < num_height; j++) {
				blocks[i][j].clear();
			}
		}
	}
	
}
