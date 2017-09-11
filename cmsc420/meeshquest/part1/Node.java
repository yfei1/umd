package cmsc420.meeshquest.part1;

public abstract class Node {
	private int x, y;
	PNode parent;
	
	Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX(){return this.x;}
	public int getY(){return this.y;}
	
	public void setParent(PNode r) {
		parent = r;
	}
	
	public abstract boolean isLeaf();
	
	public String toString() {
		return "X:" + x + "; Y:" + y;
	}
}
