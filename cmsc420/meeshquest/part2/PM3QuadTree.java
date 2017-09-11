package cmsc420.meeshquest.part2;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import cmsc420.drawing.CanvasPlus;


public class PM3QuadTree extends PMQuadTree{
	PointNode root = null;
	CanvasPlus canvas = null;
	TreeMap<City, TreeSet<City>> tm = new TreeMap<City, TreeSet<City>>();
		
	PM3QuadTree(CanvasPlus canvas, int w, int h) {
		super();
		this.canvas = canvas;
		canvas.addRectangle(0, 0, w, h, Color.BLACK, false);
		root = new PointNode(0,0,w, h);
	}
	
	public void insert(City c) {
		if (root.narr[1].isNull()) {
			LeafNode temp = new LeafNode(c);
			root.narr[1] = temp;
			temp.parent = root;
		} else insert(root.narr[1], new LeafNode(c), 1);
	}
		
	public LeafNode findCity(String cityName) {
		return (LeafNode)findCity(cityName, root.narr[1]);
	}
	
	private Node findCity(String cityName, Node r) {
		if (r.isNull()) return null;
		else if (r.isLeaf()) {
			City aaa = ((LeafNode)r).c;
			if (aaa == null) return null;
			else return aaa.getName().equals(cityName)? r:null;
		}
		else {
			Node res;
			
			if ((res = findCity(cityName, ((PointNode)r).narr[0])) != null) return res;
			else if ((res = findCity(cityName, ((PointNode)r).narr[1])) != null) return res;
			else if ((res = findCity(cityName, ((PointNode)r).narr[2])) != null) return res;
			else if ((res = findCity(cityName, ((PointNode)r).narr[3])) != null) return res;
			else return null;
		}
	}
	
	private boolean inSameRegion(double ax, double ay, double bx, double by, double x, double y) {
		boolean ne = ax >= x && ay >= y && bx >= x && by >= y;
		boolean nw = ax <= x && ay >= y && bx <= x && by >= y;
		boolean se = ax >= x && ay <= y && bx >= x && by <= y;
		boolean sw = ax <= x && ay <= y && bx <= x && by <= y;
		
		if (ne || nw || se || sw) return true;
		return false;
	}
	
	public void insertRoad(City c1, City c2) {
		int c1x = c1.getX(), c1y = c1.getY(), c2x = c2.getX(), c2y = c2.getY();
		if (c1x <= root.xlen && c1y <= root.ylen && c1x >= 0 && c1y >= 0) {
			if (this.findCity(c1.getName()) == null) insert(c1);
			if (!tm.containsKey(c1)) {
				TreeSet<City> s;
				tm.put(c1, (s = new TreeSet<City>()));
				s.add(c2);
			} else tm.get(c1).add(c2);
		}
		if (c2x <= root.xlen && c2y <= root.ylen && c2x >= 0 && c2y >= 0) {
			if(this.findCity(c2.getName()) == null) insert(c2);
			if (!tm.containsKey(c2)) {
				TreeSet<City> s;
				tm.put(c2, (s = new TreeSet<City>()));
				s.add(c1);
			} else tm.get(c2).add(c1);
		}
		
		int letsee = c1.getName().compareTo(c2.getName());
		String start = letsee > 0 ? c2.getName() : c1.getName();
		String end = letsee > 0 ? c1.getName() : c2.getName();
		
		if (c1.getX() < c2.getX()) insertRoad(root, c1.getX(), c1.getY(), c2.getX(), c2.getY(),start,end, 0);
		else insertRoad(root, c2.getX(), c2.getY(), c1.getX(), c1.getY(),start,end, 1);
	}
	
	private boolean intersect(PointNode r, double lx, double ly, double rx, double ry, int q) {
		double miny = ry > ly ? ly:ry;
		double maxy = ry > ly ? ry:ly;
		double xp, xn, yp, yn;
		
		if (q % 2 == 0) {xp = r.p.x; xn = r.p.x-r.xlen;}
		else {xp = r.p.x+r.xlen; xn = r.p.x;}
		
		if(q <= 1) {yp = r.p.y+r.ylen; yn = r.p.y;}
		else {yp = r.p.y; yn=r.p.y-r.ylen;}
		
		if (xp >= rx && xn <= lx && miny >= yn && maxy <= yp) return true;//the line is in qth quadrant
		else if (Line2D.linesIntersect(xn, yn, xn, yp, lx, ly, rx, ry) ||
			Line2D.linesIntersect(xp, yn, xp, yp, lx, ly, rx, ry) ||
			Line2D.linesIntersect(xn, yn, xp, yn, lx, ly, rx, ry) ||
			Line2D.linesIntersect(xn, yp, xp, yp, lx, ly, rx, ry)) return true;
		else return false;	
	}
	
	private void insertRoad(PointNode r, double lx, double ly, double rx, double ry, String start, String end, int type) {
		
		for (int i = 0; i < 4; i++) {
			//if the ith quadrant contains the line or the line intersects its boundaries
			if (intersect(r, lx, ly, rx, ry, i)) {
				if (r.narr[i].isLeaf()) {
					String[] s = new String[]{start, end};
					canvas.addLine(lx, ly, rx, ry, Color.BLACK);
					if (type == 0) {
						((LeafNode)r.narr[i]).arr.add(new Road(s,new Line2D.Double(lx, ly, rx, ry)));
					}
					else {
						((LeafNode)r.narr[i]).arr.add(new Road(s, new Line2D.Double(rx, ry, lx, ly)));
					}
					//add the line to the leaf
				} else if (r.narr[i].isNull()) {
					r.narr[i] = new LeafNode();
					r.narr[i].parent = r;
					canvas.addLine(lx, ly, rx, ry, Color.BLACK);

					if (type == 0) 
						((LeafNode)r.narr[i]).arr.add(new Road(new String[]{start, end} , new Line2D.Double(lx, ly, rx, ry)));
					else  
						((LeafNode)r.narr[i]).arr.add(new Road(new String[]{start, end} , new Line2D.Double(rx, ry, lx, ly)));
				} else {
					insertRoad(((PointNode)r.narr[i]), lx, ly, rx, ry, start, end, type);
					//step into this quadrant recursively
				}
			}
		}
	}

	private void insert(Node r, LeafNode node_c, int q) {
		
		if (r.isNull()) {
			canvas.addPoint(node_c.c.getName(), node_c.c.getX(), node_c.c.getY(), Color.BLACK);
			node_c.parent = r.parent;
			node_c.parent.childNum++;
			node_c.parent.narr[q] = node_c;
		} else if (r.isLeaf()){
			PointNode temp = r.parent;
			temp.childNum--;
			LeafNode l = (LeafNode)r;
			
			double parentx = temp.getX();
			double parenty = temp.getY();
			
			double partition_rx = parentx, partition_ry = parenty;
			
			if (l.c == null) {
				l.c = node_c.c;
				l.arr.addAll(node_c.arr);
				canvas.addPoint(node_c.c.getName(), node_c.c.getX(), node_c.c.getY(), Color.BLACK);
			}
			else {
				if(inSameRegion(l.c.getX(),l.c.getY(), node_c.c.getX(), node_c.c.getY(), parentx, parenty)){
					double new_xintercept = temp.xlen/2;
					double new_yintercept = temp.ylen/2;
	
					PointNode new_p = null;
	
					if (node_c.c.getX() >= parentx && node_c.c.getY() >= parenty && q == 1) {
						partition_rx = parentx + new_xintercept;
						partition_ry = parenty + new_yintercept;
						new_p = new PointNode(partition_rx, partition_ry, new_xintercept, new_yintercept);
						temp.childNum++;
						new_p.parent = temp;
						canvas.addLine(partition_rx, partition_ry-new_yintercept, partition_rx, partition_ry+new_yintercept, Color.GRAY);
						canvas.addLine(partition_rx-new_xintercept, partition_ry, partition_rx+new_xintercept, partition_ry, Color.GRAY);
						temp.narr[1] = new_p;
						insert(new_p, ((LeafNode)r).clone(), -1);
						insert(new_p, node_c.clone(), -1);

						for (Road e : ((LeafNode)r).arr) {
							Double d = e.getLine();
							insertRoad(new_p, d.getX1(), d.getY1(), d.getX2(), d.getY2(), e.getStart(), e.getEnd(), 0);
						}
					}
					if(node_c.c.getX() >= parentx && node_c.c.getY() <= parenty && q == 3) {
						partition_rx = parentx + new_xintercept;
						partition_ry = parenty - new_yintercept;
						new_p = new PointNode(partition_rx, partition_ry, new_xintercept, new_yintercept);
						temp.childNum++;
						new_p.parent = temp;
						canvas.addLine(partition_rx, partition_ry-new_yintercept, partition_rx, partition_ry+new_yintercept, Color.GRAY);
						canvas.addLine(partition_rx-new_xintercept, partition_ry, partition_rx+new_xintercept, partition_ry, Color.GRAY);
						temp.narr[3] = new_p;
						insert(new_p, ((LeafNode)r).clone(), -1);
						insert(new_p, node_c.clone(), -1);
						for (Road e : ((LeafNode)r).arr) {
							Double d = e.getLine();
							insertRoad(new_p, d.getX1(), d.getY1(), d.getX2(), d.getY2(), e.getStart(), e.getEnd(), 0);
						}
					}
					if(node_c.c.getX() <= parentx && node_c.c.getY() >= parenty && q == 0) {
						partition_rx = parentx - new_xintercept;
						partition_ry = parenty + new_yintercept;
						new_p = new PointNode(partition_rx, partition_ry, new_xintercept, new_yintercept);
						temp.childNum++;
						new_p.parent = temp;
						canvas.addLine(partition_rx, partition_ry-new_yintercept, partition_rx, partition_ry+new_yintercept, Color.GRAY);
						canvas.addLine(partition_rx-new_xintercept, partition_ry, partition_rx+new_xintercept, partition_ry, Color.GRAY);
						temp.narr[0] = new_p;
						insert(new_p, ((LeafNode)r).clone(), -1);
						insert(new_p, node_c.clone(), -1);
						for (Road e : ((LeafNode)r).arr) {
							Double d = e.getLine();
							insertRoad(new_p, d.getX1(), d.getY1(), d.getX2(), d.getY2(), e.getStart(), e.getEnd(), 0);
						}
					} 
					if(node_c.c.getX() <= parentx && node_c.c.getY() <= parenty && q == 2) {
						partition_rx = parentx - new_xintercept;
						partition_ry = parenty - new_yintercept;
						new_p = new PointNode(partition_rx, partition_ry, new_xintercept, new_yintercept);
						temp.childNum++;
						new_p.parent = temp;
						canvas.addLine(partition_rx, partition_ry-new_yintercept, partition_rx, partition_ry+new_yintercept, Color.GRAY);
						canvas.addLine(partition_rx-new_xintercept, partition_ry, partition_rx+new_xintercept, partition_ry, Color.GRAY);
						temp.narr[2] = new_p;
						insert(new_p, ((LeafNode)r).clone(), -1);
						insert(new_p, node_c.clone(), -1);
						for (Road e : ((LeafNode)r).arr) {
							Double d = e.getLine();
							insertRoad(new_p, d.getX1(), d.getY1(), d.getX2(), d.getY2(), e.getStart(), e.getEnd(), 0);
						}
					}
				}
			}
			
		} else {
			PointNode temp = (PointNode) r;
			if (node_c.c.getX() >= temp.getX() && node_c.c.getY() >= temp.getY()) {
				insert(temp.narr[1], node_c.clone(), 1);
			}
			if (node_c.c.getX() >= temp.getX() && node_c.c.getY() <= temp.getY()) {
				insert(temp.narr[3], node_c.clone(), 3);
			} 
			if (node_c.c.getX() <= temp.getX() && node_c.c.getY() >= temp.getY()) {
				insert(temp.narr[0], node_c.clone(), 0);
			}
			if (node_c.c.getX() <= temp.getX() && node_c.c.getY() <= temp.getY()){
				insert(temp.narr[2], node_c.clone(), 2);
			}	
		}
	}
	
	public void delete(City c) {
		delete(root, c, -1);
	}
	
	private Node oneChildGetter(PointNode p) {
		for (int i = 0; i < 4; i++) if (!p.narr[i].isNull()) return p.narr[i];
		
		return null;
	}
	
	private void delete(Node r, City c, int q) {
		if (r != null) {

			if (r.isLeaf()) {
				City rc = ((LeafNode)r).c;
				if (rc == null) return;
				if (c.getX() == rc.getX() && c.getY() == rc.getY()) {
					PointNode temp = r.parent;
					
					while(canvas.removePoint(c.getName(), c.getX(), c.getY(), Color.BLACK));
					temp.childNum--;
					temp.narr[q] = NullNode.getInstance(temp);
					Node tempchild;
					
					while(temp.childIsUnique() && !temp.equals(root))  {
						tempchild = oneChildGetter(temp);
						
						if(!tempchild.isLeaf()) return;
						canvas.removeLine(temp.getX(), temp.getY()-temp.ylen, temp.getX(), temp.getY()+temp.ylen, Color.GRAY);
						canvas.removeLine(temp.getX()-temp.xlen, temp.getY(), temp.getX()+temp.xlen, temp.getY(), Color.GRAY);
						canvas.draw();
						if (temp.parent.narr[0] == temp) {
							temp.parent.narr[0] = tempchild;
						}
						if (temp.parent.narr[1] == temp) {
							temp.parent.narr[1] = tempchild;
						}
						if (temp.parent.narr[2] == temp) {
							temp.parent.narr[2] = tempchild;
						}
						if (temp.parent.narr[3] == temp){
							temp.parent.narr[3] = tempchild;
						}
						
						tempchild.parent = temp.parent;
						temp = temp.parent;
					}
				}
			} else if (r.isNull()){ 
			} else {
				PointNode temp = (PointNode)r;
				
				if (c.getX() <= temp.getX() && c.getY() >= temp.getY()) {
					delete(temp.narr[0], c, 0);
				} 
				if (c.getX() >= temp.getX() && c.getY() >= temp.getY()) {
					delete(temp.narr[1], c, 1);
				}
				if (c.getX() <= temp.getX() && c.getY() <= temp.getY()){
					delete(temp.narr[2], c, 2);
				}
				if (c.getX() >= temp.getX() && c.getY() <= temp.getY()) {
					delete(temp.narr[3], c, 3);
				}
			}			
		}
		
		return;
	}
		
	public AbstractMap.SimpleEntry<String, Stack<City>> shortestPath(String start, String end) {
		LeafNode start_city = findCity(start);
		LeafNode end_city = findCity(end);
		
		if (start_city != null  && end_city != null) {
			Dijkstra dij = new Dijkstra(start, tm);
			Stack<City> wenhao = dij.shortestPath(start_city.c, end_city.c, tm);
			return new AbstractMap.SimpleEntry<String, Stack<City>>(dij.retrieveLastDist(), wenhao);
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		CanvasPlus c = new CanvasPlus();
		PM3QuadTree tree = new PM3QuadTree(c, 1024, 1024);
		City c1 = new City("A", 100, 100, 0, "black");
		City c2 = new City("B", 100, 1000, 0, "black");
		City c3 = new City("C", 1000, 1000, 0, "black");
		City c4 = new City("D", 1000, 100, 0, "black");
		City c5 = new City("E", 512, 512, 0, "black");
		City c6 = new City("M", 0, 0, 0, "black");
		City c7 = new City("N", 0, 1024, 0, "black");
		City c8 = new City("P", 1024, 0, 0, "black");
		City c9 = new City("O", 1024,1024,0,"black");

			
		tree.insertRoad(c1, c2);
		tree.insertRoad(c2, c3);
		tree.insertRoad(c3, c4);
		tree.insertRoad(c4, c1);
		tree.insertRoad(c1, c5);
		tree.insertRoad(c2, c5);
		tree.insertRoad(c3, c5);
		tree.insertRoad(c4, c5);
		tree.insertRoad(c6, c7);
		tree.insertRoad(c5, c6);
		tree.insertRoad(c8, c4);
		tree.insertRoad(c9, c5);
		tree.insertRoad(c2, c8);
		
		SimpleEntry<String, Stack<City>> s = tree.shortestPath("M", "B");
		System.out.println(s);
		//tree.insert(c6);
		//tree.insert(c7);
		//tree.delete(c1);
		//tree.delete(c2);
		c.draw();
	}

}
