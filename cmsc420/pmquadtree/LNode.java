package cmsc420.pmquadtree;

import cmsc420.geometry.Metropole;

public class LNode extends Node {
	LNode(int x, int y) {
		super(x, y);
	}
	
	LNode(int x, int y, Metropole m) {
		super(x,y);
		this.m = m;
	}
	
	Metropole m;
	
	@Override
	public boolean isLeaf() {
		return true;
	}
	
	
}
