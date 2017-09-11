package cmsc420.meeshquest.part2;

import java.awt.geom.Line2D;
import java.util.Comparator;

public class RoadComparator implements Comparator<Road>{
	double x, y;
	
	public RoadComparator(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int compare(Road o1, Road o2) {

		Line2D.Double l1 = o1.getLine();
		Line2D.Double l2 = o2.getLine();
		
		double o1x1 = l1.getX1(), o1x2 = l1.getX2(), o1y1 = l1.getY1(), o1y2 = l1.getY2(),
				o2x1 = l2.getX1(), o2x2 = l2.getX2(), o2y1 = l2.getY1(), o2y2 = l2.getY2();
		
		
		double distl1 = Line2D.ptSegDist(o1x1, o1y1, o1x2, o1y2, x, y);
		double distl2 = Line2D.ptSegDist(o2x1, o2y1, o2x2, o2y2, x, y);

		o1.setDist(distl1);
		o2.setDist(distl2);
		
		if (distl1 < distl2) return 1;
		else if (distl1 > distl2) return -1;
		
		String s1s = o1.getStart(), s1e = o1.getEnd(), s2s = o2.getStart(), s2e = o1.getEnd();
		
		int start_comp = s1s.compareTo(s2s);
		
		if (start_comp != 0) return start_comp;
		else return s1e.compareTo(s2e);
	}

}
