import java.util.ArrayList;


public class Block {

	Simulation env;
	ArrayList<Particle> parts;
	ArrayList<Block> neighbors;
	int block_x;
	int block_y;
	
	public Block(Simulation env, int i, int j) {
		this.env = env;
		parts = new ArrayList<Particle>();
		neighbors = new ArrayList<Block>();
		
		this.block_x = i;
		this.block_y = j;
		
	}

	public void clear() {
		parts.clear();
	}
	
	public String toString() {
		String str = "Block: (" + block_x + ", " + block_y + ")";
		return str;
	}
	
	public void addNeighbors() {
		if (env.grid != null) {
			neighbors.clear();
			if (block_x == 0) {
				//block is in the top left corner
				if (block_y == 0) {
					neighbors.add(this);
					neighbors.add(env.grid.getBlock(block_x+1, block_y));
					neighbors.add(env.grid.getBlock(block_x, block_y+1));
					neighbors.add(env.grid.getBlock(block_x+1, block_y+1));
				}
				//box is in the bottom left corner
				else if (block_y == env.grid.num_height-1) {
					neighbors.add(this);
					neighbors.add(env.grid.getBlock(block_x, block_y-1));
					neighbors.add(env.grid.getBlock(block_x+1, block_y));
					neighbors.add(env.grid.getBlock(block_x+1, block_y-1));
				}
				//box is in the left column
				else {
					//top row
					neighbors.add(env.grid.getBlock(block_x, block_y-1));
					neighbors.add(env.grid.getBlock(block_x+1, block_y-1));
					//middle row
					neighbors.add(env.grid.getBlock(block_x, block_y));
					neighbors.add(env.grid.getBlock(block_x+1, block_y));
					//bottom row
					neighbors.add(env.grid.getBlock(block_x, block_y+1));
					neighbors.add(env.grid.getBlock(block_x+1, block_y+1));
				}
			}
			else if (block_x == env.grid.num_height-1) {
				//block is in the rightmost column
				//block is in the top right corner
				if (block_y == 0) {
					neighbors.add(env.grid.getBlock(block_x, block_y));
					neighbors.add(env.grid.getBlock(block_x-1, block_y));
					neighbors.add(env.grid.getBlock(block_x, block_y+1));
					neighbors.add(env.grid.getBlock(block_x-1, block_y+1));
				}
				//box is in the bottom right corner
				else if (block_y == env.grid.num_height-1) {
					neighbors.add(env.grid.getBlock(block_x, block_y));
					neighbors.add(env.grid.getBlock(block_x, block_y-1));
					neighbors.add(env.grid.getBlock(block_x-1, block_y));
					neighbors.add(env.grid.getBlock(block_x-1, block_y-1));
				}
				//box is in the right column
				else {
					//top row
					neighbors.add(env.grid.getBlock(block_x-1, block_y-1));
					neighbors.add(env.grid.getBlock(block_x, block_y-1));
					//middle row
					neighbors.add(env.grid.getBlock(block_x-1, block_y));
					neighbors.add(env.grid.getBlock(block_x, block_y));
					//bottom row
					neighbors.add(env.grid.getBlock(block_x-1, block_y+1));
					neighbors.add(env.grid.getBlock(block_x, block_y+1));
				}
			}
			else if (block_y == 0) {
				//block is on the top row and not a corner
				//middle row
				neighbors.add(env.grid.getBlock(block_x-1, block_y));
				neighbors.add(this);
				neighbors.add(env.grid.getBlock(block_x+1, block_y));
				//bottom row
				neighbors.add(env.grid.getBlock(block_x-1, block_y+1));
				neighbors.add(env.grid.getBlock(block_x, block_y+1));
				neighbors.add(env.grid.getBlock(block_x+1, block_y+1));
			}
			else if (block_y == env.grid.num_width-1) {
				//block is on the bottom row and now a corner
				//top row
				neighbors.add(env.grid.getBlock(block_x-1, block_y-1));
				neighbors.add(env.grid.getBlock(block_x, block_y-1));
				neighbors.add(env.grid.getBlock(block_x+1, block_y-1));
				//middle row
				neighbors.add(env.grid.getBlock(block_x-1, block_y));
				neighbors.add(this);
				neighbors.add(env.grid.getBlock(block_x+1, block_y));
			}
			else {
				//block is somewhere in the middle
				//top row
				neighbors.add(env.grid.getBlock(block_x-1, block_y-1));
				neighbors.add(env.grid.getBlock(block_x, block_y-1));
				neighbors.add(env.grid.getBlock(block_x+1, block_y-1));
				//middle row
				neighbors.add(env.grid.getBlock(block_x-1, block_y));
				neighbors.add(this);
				neighbors.add(env.grid.getBlock(block_x+1, block_y));
				//bottom row
				neighbors.add(env.grid.getBlock(block_x-1, block_y+1));
				neighbors.add(env.grid.getBlock(block_x, block_y+1));
				neighbors.add(env.grid.getBlock(block_x+1, block_y+1));
			}
		}
	}
	
}
