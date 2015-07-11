
public class Grid {
	
	public MainGUI gui;
	public Simulation env;
	float repulsion_radius;
	Block[][][] blocks;
	int num_width;
	int num_height;
	int num_depth;

	@SuppressWarnings("static-access")
	public Grid(Simulation env) {
		this.env = env;
		int sim_w = (int) (env.width*env.scalefactor);
		int sim_h = (int) (env.height*env.scalefactor);
		int sim_d = (int) (env.depth*env.scalefactor);
		repulsion_radius = 30;
		//float repulsion_radius = env.radius;
		
		num_width = (int) Math.ceil((float)sim_w/repulsion_radius);
		num_height = (int) Math.ceil((float)sim_h/repulsion_radius);
		num_depth = (int) Math.ceil((float)sim_d/repulsion_radius);
		
		blocks = new Block[num_width][num_height][num_depth];
		
		for (int i = 0; i < num_width; i++) {
			for (int j = 0; j < num_height; j++) {
				for (int k = 0; k < num_depth; k++) {
					blocks[i][j][k] = new Block(env, i, j, k);
				}
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
				
			blocks = new Block[num_width][num_height][num_depth];
			
			for (int i = 0; i < num_width; i++) {
				for (int j = 0; j < num_height; j++) {
					for (int k = 0; k < num_depth; k++) {
						blocks[i][j][k] = new Block(env, i, j, k);
					}
				}
			}
			
		}
	}
	
	public Block getBlock(int i, int j, int k) {
		return blocks[i][j][k];
	}

	public void clear() {
		for (int i = 0; i < num_width; i ++) {
			for (int j = 0; j < num_height; j++) {
				for (int k = 0; k < num_depth; k++) {
					blocks[i][j][k].clear();
				}
			}
		}
	}
	
	
	
}
