package cmsc420.meeshquest.part1;

import java.awt.Color;
import cmsc420.drawing.CanvasPlus;

public class QuadTree {
	PNode root = null;
	CanvasPlus canvas = null;
	
	QuadTree(CanvasPlus canvas, int w, int h) {
		this.canvas = canvas;
		canvas.addRectangle(0, 0, w, h, Color.BLACK, false);
		root = new PNode(0,0,w, h);
	}
	
	public void insert(City c) {
		root = (PNode)insert(root, c, null);
	}
	
	public static int getX(City c) {
		return c.x;
	}
	
	public static int getY(City c) {
		return c.y;
	}
	
	private boolean inSameRegion(int ax, int ay, int bx, int by, int x, int y) {
		boolean ne = ax >= x && ay >= y && bx >= x && by >= y;
		boolean nw = ax < x && ay >= y && bx < x && by >= y;
		boolean se = ax >= x && ay < y && bx >= x && by <y;
		boolean sw = ax < x && ay < y && bx < x &&  by < y;
		
		if (ne || nw || se || sw) return true;
		return false;
	}
	
	private Node insert(Node r, City c, PNode p) {
		if (r == null) {
			r = new LNode(getX(c), getY(c), c);
			canvas.addPoint(c.name, c.x, c.y, Color.BLACK);
			p.childNum++;
			return r; 
		} else if (r.isLeaf()){
			PNode temp = p;
			temp.childNum--;
			Node ret_node = null;
			
			int parentx = temp.getX();
			int parenty = temp.getY();
			
			int partition_rx = parentx, partition_ry = parenty;
			
			
			while(inSameRegion(r.getX(),r.getY(), c.x, c.y, parentx, parenty)){
				int new_xintercept = temp.xintercept/2;
				int new_yintercept = temp.yintercept/2;

				PNode new_p;

				if (r.getX() >= parentx && r.getY() >= parenty) {
					partition_rx = parentx + new_xintercept;
					partition_ry = parenty + new_yintercept;
					new_p = new PNode(partition_rx, partition_ry, new_xintercept, new_yintercept);
					temp.NE = new_p;
				} else if(r.getX() >= parentx && r.getY() < parenty) {
					partition_rx = parentx + new_xintercept;
					partition_ry = parenty - new_yintercept;
					new_p = new PNode(partition_rx, partition_ry, new_xintercept, new_yintercept);
					temp.SE = new_p;
				} else if(r.getX() < parentx && r.getY() >= parenty) {
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
				canvas.addLine(partition_rx, partition_ry-new_yintercept, partition_rx, partition_ry+new_yintercept, Color.black);
				canvas.addLine(partition_rx-new_xintercept, partition_ry, partition_rx+new_xintercept, partition_ry, Color.black);
				temp = new_p;
				if (ret_node == null) ret_node = temp;
				parentx = partition_rx;
				parenty = partition_ry;
			}
			
			insert(temp, c, temp.parent);
			insert(temp, ((LNode)r).c, temp.parent);
			
			return ret_node;
		} else {
			PNode temp = (PNode) r;
			Node child;
			if (getX(c) >= temp.getX() && getY(c) >= temp.getY()) {
				child = insert(temp.NE, c, temp);
				temp.NE = child;
			} else if (getX(c) >= temp.getX() && getY(c) < temp.getY()) {
				child = insert(temp.SE, c, temp);
				temp.SE = child;
			} else if (getX(c) < temp.getX() && getY(c) >= temp.getY()) {
				child = insert(temp.NW, c, temp);
				temp.NW = child;
			} else {
				child = insert(temp.SW, c, temp);
				temp.SW = child;
			}
			child.setParent(temp);
			
			return temp;
		}
	}
	
	public void delete(City c) {
		delete(root, c);
	}
	
	private void delete(Node r, City c) {
		if (r != null) {
			
			if (r.isLeaf()) {
				if (c.x == r.getX() && c.y == r.getY()) {
					canvas.removePoint(c.name, c.x, c.y, Color.BLACK);
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
						canvas.removeLine(temp.getX(), temp.getY()-temp.yintercept, temp.getX(), temp.getY()+temp.yintercept, Color.BLACK);
						canvas.removeLine(temp.getX()-temp.xintercept, temp.getY(), temp.getX()+temp.xintercept, temp.getY(), Color.BLACK);

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
				
				if (c.x >= temp.getX() && c.y >= temp.getY()) {
					delete(temp.NE, c);
				} else if (c.x >= temp.getX() && c.y < temp.getY()) {
					delete(temp.SE, c);
				} else if (c.x < temp.getX() && c.y >= temp.getY()) {
					delete(temp.NW, c);
				} else {
					delete(temp.SW, c);
				}
			}			
		}
		
		return;
	}
	

	public static void main(String[] args) {
		CanvasPlus c = new CanvasPlus("MeeshQuest", 128, 128);
		QuadTree q = new QuadTree(c,128,128);
		City temp1, temp2;
		q.insert(new City("cake",18,47,39,"black"));
		q.insert(new City("cheese",50,19,10,"black"));
		q.insert(new City("run",63,64,39,"black"));
		q.insert(new City("up",64,63,39,"black"));
		q.insert(new City("shoot",65,65,39,"black"));


		c.draw();
		System.out.println("bu".compareTo("ba"));
	}
}
