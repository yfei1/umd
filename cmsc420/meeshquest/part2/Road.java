package cmsc420.meeshquest.part2;

import java.awt.geom.Line2D;

public class Road implements Comparable<Road>{
	
	private String[] s = new String[2];
	private Line2D.Double l;
	private double dist;
	
	public Road(String[] s, Line2D.Double l) {
		this.s = s;
		this.l = l;
		this.dist = 0;
	}
	
	public void setDist(double dist) {
		this.dist = dist;
	}
	
	public double getDist() {
		return dist;
	}
	
	public Line2D.Double getLine() {
		return l;
	}
	
	public void setLine(Line2D.Double l) {
		this.l = l;
	}
	
	public String getStart() {
		return s[0];
	}
	
	public String getEnd() {
		return s[1];
	}
	
	public void setStart(String s) {
		this.s[0] = s;
	}
	
	public void setEnd(String e) {
		this.s[1] = e;
	}
	
	public String toString() {
		return "From:" + s[0] + " To:" + s[1];
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		
		Road temp = (Road)o;
		
		return this.s[1].equals(temp.s[1]) && this.s[0].equals(temp.s[0]) && this.l.equals(temp.l);
	}
	public int compareTo(Road r) {
		/*if (this.s[1] == null)
			return this.s[0].compareTo(r.s[0]);
		else if (this.s[0] == null) return this.s[1].compareTo(r.s[1]);
		*/
		int firstString = this.s[0].compareTo(r.s[0]);
		if (firstString == 0) {
			int secondString = this.s[1].compareTo(r.s[1]);
			if (secondString == 0) {
				if (l.x1 == r.l.x1) {
					if (l.y1 == r.l.y1) {
						if (l.x2 == r.l.x2) {
							if (l.y2 == r.l.y2) return 0;
							else return l.y2 > r.l.y2 ? 1: -1;
						} else return (l.x2 > r.l.x2) ? 1:-1;
					} else return (l.y1 > r.l.y1) ? 1: -1;
				} else return (l.x1 > r.l.x1) ? 1: -1;
			} else return secondString;
		} else return firstString;
	}
}
