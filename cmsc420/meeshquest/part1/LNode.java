package cmsc420.meeshquest.part1;

public class LNode extends Node {
	LNode(int x, int y) {
		super(x, y);
	}
	
	LNode(int x, int y, City c) {
		super(x,y);
		this.c = c;
	}
	
	City c;
	
	@Override
	public boolean isLeaf() {
		return true;
	}
	
	
}
