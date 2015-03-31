import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import processing.core.PVector;


public class ShapeInput {
	
	public ArrayList<PVector> xyz = new ArrayList<PVector>();
	public ArrayList<PVector> springs = new ArrayList<PVector>();

	@SuppressWarnings({ "resource", "unused" })
	public ShapeInput() {
		
		try {
			String file = "";
			int vertices = 0;
			int faces = 0;
			Scanner input = new Scanner(new File("C:/Users/Megan/workspace/Research/src/Shapes/icos.txt"));
			//file += input.nextLine() + "\n";
			while (input.hasNextLine()) {
				String next = input.next();
				if (next.equalsIgnoreCase("Vertex")) {
					vertices = input.nextInt();
					if (vertices != 0) 
						file += vertices;
				}
				if (next.equalsIgnoreCase("face")) {
					faces = input.nextInt();
					if (faces != 0) 
						file += faces;
					
					float x1, x2, x3 = 0;
					
					for (int i = 0; i < vertices; i++) {
						x1 = input.nextFloat();
						x2 = input.nextFloat();
						x3 = input.nextFloat();
						
						PVector temp = new PVector(x1, x2, x3);
						xyz.add(temp);
					}
					
					int one, two, three = 0;
					
					for (int i = 0; i < faces; i++) {
						input.nextInt();
						one = input.nextInt();
						two = input.nextInt();
						three = input.nextInt();
						springs.add(new PVector(one, two, three));
					}
					
				}
				
				//file += next;
				file += input.nextLine() + "\n";
			}
		}
		
		
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
	}
}
