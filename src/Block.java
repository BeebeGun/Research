import java.util.ArrayList;


public class Block {

	Simulation env;
	ArrayList<Particle> parts;
	ArrayList<Block> neighbors;
	int block_x;
	int block_y;
	int block_z;
	
	public Block(Simulation env, int i, int j, int k) {
		this.env = env;
		parts = new ArrayList<Particle>();
		neighbors = new ArrayList<Block>();
		
		this.block_x = i;
		this.block_y = j;
		this.block_z = k;
		
	}

	public void clear() {
		parts.clear();
	}
	
	public String toString() {
		String str = "Block: (" + block_x + ", " + block_y + ", " + block_z + ")";
		return str;
	}
	
	public void addNeighbors() {
		if (env.grid != null) {
			neighbors.clear();
			
			neighbors.add(this);
			
			//if the back face is in the grid
			if (block_z + 1 < env.grid.num_depth-1) {
				
				//if the left column is there
				if (block_x-1 > 0) {
					//the middle block in the left column is there
					neighbors.add(env.grid.getBlock(block_x-1, block_y, block_z+1));
					
					//if the top block is there
					if (block_y-1 > 0) {
						neighbors.add(env.grid.getBlock(block_x-1, block_y-1, block_z+1));
					}
					
					//if the bottom block is there
					if (block_y+1 < env.grid.num_height) {
						neighbors.add(env.grid.getBlock(block_x-1, block_y+1, block_z+1));
					}
				}
				
				//if the right column is there
				if (block_x+1 < env.grid.num_width-1) {
					
					//the middle block in the right column is there
					neighbors.add(env.grid.getBlock(block_x+1, block_y, block_z+1));
					
					//if the top block is there
					if (block_y-1 > 0) {
						neighbors.add(env.grid.getBlock(block_x+1, block_y-1, block_z+1));
					}
					
					//if the bottom block is there
					if (block_y+1 < env.grid.num_height) {
						neighbors.add(env.grid.getBlock(block_x+1, block_y+1, block_z+1));
					}
				}
				
				//the middle column is definitely there
				neighbors.add(env.grid.getBlock(block_x, block_y, block_z+1));
				
				//if the top block is there
				if (block_y-1 > 0) {
					neighbors.add(env.grid.getBlock(block_x, block_y-1, block_z+1));
				}
				
				//if the bottom block is there
				if (block_y+1 < env.grid.num_height) {
					neighbors.add(env.grid.getBlock(block_x, block_y+1, block_z+1));
				}
			}
			
			
			
			//if the front face is on the grid
			if (block_z - 1 > 0) {

				//if the left column is there
				if (block_x-1 > 0) {
					//the middle block in the left column is there
					neighbors.add(env.grid.getBlock(block_x-1, block_y, block_z-1));
					
					//if the top block is there
					if (block_y-1 > 0) {
						neighbors.add(env.grid.getBlock(block_x-1, block_y-1, block_z-1));
					}
					
					//if the bottom block is there
					if (block_y+1 < env.grid.num_height) {
						neighbors.add(env.grid.getBlock(block_x-1, block_y+1, block_z-1));
					}
				}
				
				//if the right column is there
				if (block_x+1 < env.grid.num_width-1) {
					
					//the middle block in the right column is there
					neighbors.add(env.grid.getBlock(block_x+1, block_y, block_z-1));
					
					//if the top block is there
					if (block_y-1 > 0) {
						neighbors.add(env.grid.getBlock(block_x+1, block_y-1, block_z-1));
					}
					
					//if the bottom block is there
					if (block_y+1 < env.grid.num_height) {
						neighbors.add(env.grid.getBlock(block_x+1, block_y+1, block_z-1));
					}
				}
				
				//the middle column is definitely there
				neighbors.add(env.grid.getBlock(block_x, block_y, block_z-1));
				
				//if the top block is there
				if (block_y-1 > 0) {
					neighbors.add(env.grid.getBlock(block_x, block_y-1, block_z-1));
				}
				
				//if the bottom block is there
				if (block_y+1 < env.grid.num_height) {
					neighbors.add(env.grid.getBlock(block_x, block_y+1, block_z-1));
				}
				
			}
			
			//the middle of the block is definitely on the grid somewhere
			
			//if the left column is there
			if (block_x-1 > 0) {
				//the middle block in the left column is there
				neighbors.add(env.grid.getBlock(block_x-1, block_y, block_z));
				
				//if the top block is there
				if (block_y-1 > 0) {
					neighbors.add(env.grid.getBlock(block_x-1, block_y-1, block_z));
				}
				
				//if the bottom block is there
				if (block_y+1 < env.grid.num_height) {
					neighbors.add(env.grid.getBlock(block_x-1, block_y+1, block_z));
				}
			}
			
			//if the right column is there
			if (block_x+1 < env.grid.num_width-1) {
				
				//the middle block in the right column is there
				neighbors.add(env.grid.getBlock(block_x+1, block_y, block_z));
				
				//if the top block is there
				if (block_y-1 > 0) {
					neighbors.add(env.grid.getBlock(block_x+1, block_y-1, block_z));
				}
				
				//if the bottom block is there
				if (block_y+1 < env.grid.num_height) {
					neighbors.add(env.grid.getBlock(block_x+1, block_y+1, block_z));
				}
			}
			
			//if the top block is there
			if (block_y-1 > 0) {
				neighbors.add(env.grid.getBlock(block_x, block_y-1, block_z));
			}
			
			//if the bottom block is there
			if (block_y+1 < env.grid.num_height) {
				neighbors.add(env.grid.getBlock(block_x, block_y+1, block_z));
			}
			
			
		}
		
	}
	
}
