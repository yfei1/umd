package cmsc420.pmquadtree;

import cmsc420.geometry.Metropole;

public class QuadTree {
	PNode root = null;
	
	public QuadTree(int rW, int rH, int lW, int lH, int order) {
		root = new PNode(0,0,rW, rH);
	}
	
	public Metropole findMetropoleByLocation(int remoteX, int remoteY) {
		LNode ln = findMetropoleByLocation(remoteX, remoteY, root);
		
		return ln == null ? null : ln.m;
	}
	
	public Node getRoot() {
		return root.NE;
	}
	
	public void clear() {
		root = null;
	}
	
	private LNode findMetropoleByLocation(int x, int y, Node root) {
		if (root == null) {
			return null;
		} else if (root.isLeaf()) {
			LNode leaf = (LNode) root;
			return (leaf.getRemoteX() == x && leaf.getRemoteY() == y) ? leaf:null;
		} else {
			PNode pointer = (PNode) root;
			
			if(x >= pointer.getRemoteX() && y >= pointer.getRemoteY()) return findMetropoleByLocation(x, y, pointer.NE);
			else if (x >= pointer.getRemoteX() && y < pointer.getRemoteY()) return findMetropoleByLocation(x, y, pointer.SE);
			else if (x < pointer.getRemoteX() && y < pointer.getRemoteY()) return findMetropoleByLocation(x, y, pointer.SW);
			else return findMetropoleByLocation(x, y, pointer.NW);
		}
	}
	
	public void insert(Metropole m) {
		root = (PNode)insert(root, m, null);
	}
	
	public static int getX(Metropole m) {
		return m.getRemoteX();
	}
	
	public static int getY(Metropole m) {
		return m.getRemoteY();
	}
	
	private boolean inSameRegion(int ax, int ay, int bx, int by, int x, int y) {
		boolean ne = ax >= x && ay >= y && bx >= x && by >= y;
		boolean nw = ax < x && ay >= y && bx < x && by >= y;
		boolean se = ax >= x && ay < y && bx >= x && by <y;
		boolean sw = ax < x && ay < y && bx < x &&  by < y;
		
		if (ne || nw || se || sw) return true;
		return false;
	}
	
	private Node insert(Node r, Metropole m, PNode p) {
		if (r == null) {
			r = new LNode(getX(m), getY(m), m);
			p.childNum++;
			return r; 
		} else if (r.isLeaf()){
			PNode temp = p;
			temp.childNum--;
			Node ret_node = null;
			
			int parentx = temp.getRemoteX();
			int parenty = temp.getRemoteY();
			
			int partition_rx = parentx, partition_ry = parenty;
			
			
			while(inSameRegion(r.getRemoteX(),r.getRemoteY(), m.getRemoteX(), m.getRemoteY(), parentx, parenty)){
				int new_xintercept = temp.xintercept/2;
				int new_yintercept = temp.yintercept/2;

				PNode new_p;

				if (r.getRemoteX() >= parentx && r.getRemoteY() >= parenty) {
					partition_rx = parentx + new_xintercept;
					partition_ry = parenty + new_yintercept;
					new_p = new PNode(partition_rx, partition_ry, new_xintercept, new_yintercept);
					temp.NE = new_p;
				} else if(r.getRemoteX() >= parentx && r.getRemoteY() < parenty) {
					partition_rx = parentx + new_xintercept;
					partition_ry = parenty - new_yintercept;
					new_p = new PNode(partition_rx, partition_ry, new_xintercept, new_yintercept);
					temp.SE = new_p;
				} else if(r.getRemoteX() < parentx && r.getRemoteY() >= parenty) {
					partition_rx = parentx - new_xintercept;
					partition_ry = parenty + new_yintercept;
					new_p = new PNode(partition_rx, partition_ry, new_xintercept, new_yintercept);
					temp.NW = new_p;
				} else {
					partition_rx = parentx - new_xintercept;
					partition_ry = parenty - new_yintercept;
					new_p = new PNode(partition_rx, partition_ry, new_xintercept, new_yintercept);
					temp.SW = new_p;
				}
				
				temp.childNum++;
				new_p.parent = temp;
				temp = new_p;
				if (ret_node == null) ret_node = temp;
				parentx = partition_rx;
				parenty = partition_ry;
			}
			
			insert(temp, m, temp.parent);
			insert(temp, ((LNode)r).m, temp.parent);
			
			return ret_node;
		} else {
			PNode temp = (PNode) r;
			Node child;
			if (getX(m) >= temp.getRemoteX() && getY(m) >= temp.getRemoteY()) {
				child = insert(temp.NE, m, temp);
				temp.NE = child;
			} else if (getX(m) >= temp.getRemoteX() && getY(m) < temp.getRemoteY()) {
				child = insert(temp.SE, m, temp);
				temp.SE = child;
			} else if (getX(m) < temp.getRemoteX() && getY(m) >= temp.getRemoteY()) {
				child = insert(temp.NW, m, temp);
				temp.NW = child;
			} else {
				child = insert(temp.SW, m, temp);
				temp.SW = child;
			}
			child.setParent(temp);
			
			return temp;
		}
	}
	
	public void delete(Metropole m) {
		delete(root, m);
	}
	
	private void delete(Node r, Metropole m) {
		if (r != null) {
			
			if (r.isLeaf()) {
				if (m.getRemoteX() == r.getRemoteX() && m.getRemoteY() == r.getRemoteY()) {
					r.parent.childNum--;
					if (r.parent.NE == r) {
						r.parent.NE = null;
					} else if (r.parent.NW == r) {
						r.parent.NW = null;
					} else if (r.parent.SE == r) {
						r.parent.SE = null;
					} else r.parent.SW = null;
					
					PNode temp = r.parent;
					Node tempchild;
					
					while(temp.childNum == 1 && !temp.equals(root))  {
						if (temp.NE != null) tempchild = temp.NE;
						else if (temp.NW != null) tempchild = temp.NW;
						else if (temp.SE != null) tempchild = temp.SE;
						else tempchild = temp.SW;
						
						if(!tempchild.isLeaf()) return;

						if (temp.parent.NE == temp) {
							temp.parent.NE = tempchild;
						} else if (temp.parent.SE == temp) {
							temp.parent.SE = tempchild;
						} else if (temp.parent.NW == temp) {
							temp.parent.NW = tempchild;
						} else {
							temp.parent.SW = tempchild;
						}
						
						tempchild.setParent(temp.parent);
						temp = temp.parent;
					}
				}
			} else {
				PNode temp = (PNode)r;
				
				if (m.getRemoteX() >= temp.getRemoteX() && m.getRemoteY() >= temp.getRemoteY()) {
					delete(temp.NE, m);
				} else if (m.getRemoteX() >= temp.getRemoteX() && m.getRemoteY() < temp.getRemoteY()) {
					delete(temp.SE, m);
				} else if (m.getRemoteX() < temp.getRemoteX() && m.getRemoteY() >= temp.getRemoteY()) {
					delete(temp.NW, m);
				} else {
					delete(temp.SW, m);
				}
			}			
		}
		
		return;
	}
	

	public static void main(String[] args) {
	
	}
}
