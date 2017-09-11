package cmsc420.meeshquest.part2;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class PointNode extends Node{
	Point2D.Double p;
	Node[] narr = new Node[4];
	double xlen, ylen, childNum;
	
	public PointNode(double x, double y, double xlen, double ylen) {
		p = new Point2D.Double(x, y);
		this.xlen = xlen;
		this.ylen = ylen;
		for(int i = 0; i < 4; i++) narr[i] = NullNode.getInstance(this);
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public boolean isNull() {
		return false;
	}
	
	public int getX() {
		return (int)p.getX();
	}
	
	public int getY() {
		return (int)p.getY();
	}
	
	public boolean childIsUnique() {
		int n = -1;
 		
		for (int i = 0; i < 4; i++) {
			if (this.narr[i].isLeaf() && n == -1) n = i;
		}
				
		for (int i = 0; i < 4; i++) {
			if (!this.narr[i].isNull() && n != i && !this.narr[n].equals(this.narr[i])) return false;
		}
		
		return true;
	}
	
	public String toString() {
		return "Point:(" + p.x + "," + p.y +");";
	}
}
