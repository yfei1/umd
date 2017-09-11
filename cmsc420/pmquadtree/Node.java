package cmsc420.pmquadtree;

public abstract class Node {
	private int remoteX, remoteY;
	PNode parent;
	
	Node(int x, int y) {
		this.remoteX = x;
		this.remoteY = y;
	}
	
	public int getRemoteX(){return this.remoteX;}
	public int getRemoteY(){return this.remoteY;}
	
	public void setParent(PNode r) {
		parent = r;
	}
	
	public abstract boolean isLeaf();
	
	public String toString() {
		return "remoteX:" + remoteX + "; remoteY:" + remoteY;
	}
}
