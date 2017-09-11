package cmsc420.meeshquest.part1;

import java.util.Comparator;

public class RadiusComparator implements Comparator<City>{
	int x,y;
	
	public RadiusComparator(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int compare(City a, City b) {
		int ax = ((City)a).x;
		int ay = ((City)a).y;
		int bx = ((City)b).x;
		int by = ((City)b).y;
		
		double rada = Math.pow(ax-x,2) + Math.pow(ay-y,2);
		double radb = Math.pow(bx-x,2) + Math.pow(by-y,2);
		
		if (rada < radb ) return 1; 
		else if (rada == radb) return 0;
		else return -1;
	}

}
