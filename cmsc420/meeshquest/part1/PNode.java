package cmsc420.meeshquest.part1;

public class PNode extends Node{
	PNode(int x, int y, int xintercept, int yintercept) {
		super(x, y);
		this.xintercept = xintercept;
		this.yintercept = yintercept;

	}
	Node NW, NE, SW, SE;
	int xintercept, yintercept, childNum;
	
	@Override
	public boolean isLeaf() {
		return false;
	}
	
	
}
