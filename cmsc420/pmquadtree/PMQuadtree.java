package cmsc420.pmquadtree;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import cmsc420.command.Command;
import cmsc420.command.Command.Err;
import cmsc420.drawing.CanvasPlus;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.geometry.Airport;
import cmsc420.geometry.City;
import cmsc420.geometry.Geometry;
import cmsc420.geometry.PointWise;
import cmsc420.geometry.Road;
import cmsc420.geometry.RoadAdjacencyList;
import cmsc420.geometry.RoadNameComparator;
import cmsc420.geometry.Terminal;

public abstract class PMQuadtree {

	public TreeSet<City> citySet;
	
	/** stores all mapped roads in the PM Quadtree */
	final protected TreeSet<Road> allRoads;
	
	/** road map to represent relations between cities and roads*/
	public final RoadAdjacencyList roads = new RoadAdjacencyList();
	
	/** stores how many roads are connected to each city */
	final protected HashMap<String, Integer> numRoadsForCity;
	
	/** number of isolated cities */
	//protected int numIsolatedCities;
	
	/** root of the PM Quadtree */
	protected Node root;

	/** spatial width of the PM Quadtree */
	final protected int spatialWidth;

	/** spatial height of the PM Quadtree */
	final protected int spatialHeight;

	/** spatial origin of the PM Quadtree (i.e. (0,0)) */
	final protected Point2D.Float spatialOrigin;

	/** validator for the PM Quadtree */
	final protected Validator validator;

	/** singleton white node */
	final protected White white = new White();

	/** order of the PM Quadtree (one of: {1,2,3}) */
	final protected int order;

	public abstract class Node {
		/** Type flag for an empty PM Quadtree leaf node */
		public static final int WHITE = 0;

		/** Type flag for a non-empty PM Quadtree leaf node */
		public static final int BLACK = 1;

		/** Type flag for a PM Quadtree internal node */
		public static final int GRAY = 2;

		/** type of PR Quadtree node (either empty, leaf, or internal) */
		protected final int type;

		/**
		 * Constructor for abstract Node class.
		 * 
		 * @param type
		 *            type of the node (either empty, leaf, or internal)
		 */
		protected Node(final int type) {
			this.type = type;
		}

		public Node clone() {
			if (this.type == BLACK) return new Black((Black) this);
			else if (this.type == GRAY) return new Gray((Gray) this);
			else return white;
		}
		/**
		 * Gets the type of this PM Quadtree node. One of: BLACK, WHITE, GRAY.
		 * 
		 * @return type of this PM Quadtree node
		 */
		public int getType() {
			return type;
		}

		/**
		 * Adds a road to this PM Quadtree node.
		 * 
		 * @param g
		 *            road to be added
		 * @param origin
		 *            origin of the rectangular bounds of this node
		 * @param width
		 *            width of the rectangular bounds of this node
		 * @param height
		 *            height of the rectangular bounds of this node
		 * @return this node after the city has been added
		 * @throws InvalidPartitionThrowable
		 *             if the map if partitioned too deeply
		 * @throws RoadIntersectAnotherRoadThrowable 
		 *             if this road intersects with another road
		 */
		public Node add(final Geometry g, final Point2D.Float origin,
				final int width, final int height, Command.Err e) {
			throw new UnsupportedOperationException();
		}
		
		public Node delete(final Geometry g) {
			throw new UnsupportedOperationException();
		}

		/**
		 * Returns if this node follows the rules of the PM Quadtree.
		 * 
		 * @return <code>true</code> if the node follows the rules of the PM
		 *         Quadtree; <code>false</code> otherwise
		 */
		public boolean isValid() {
			throw new UnsupportedOperationException();
		}

		public Collection<? extends Geometry> getGeometry() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * White class represents an empty PM Quadtree leaf node.
	 */
	public class White extends Node {
		/**
		 * Constructs and initializes an empty PM Quadtree leaf node.
		 */
		public White() {
			super(WHITE);
		}

		/**
		 * Adds a road to this PM Quadtree node.
		 * 
		 * @param g
		 *            road to be added
		 * @param origin
		 *            origin of the rectangular bounds of this node
		 * @param width
		 *            width of the rectangular bounds of this node
		 * @param height
		 *            height of the rectangular bounds of this node
		 * @return this node after the city has been added
		 * @throws InvalidPartitionThrowable
		 *             if the map if partitioned too deeply
		 * @throws RoadIntersectAnotherRoadThrowable 
		 * @throws IntersectingRoadsThrowable
		 *             if this road intersects with another road
		 */
		public Node add(final Geometry g, final Point2D.Float origin,
				final int width, final int height, Err e) {
			final Black blackNode = new Black();

			return blackNode.add(g, origin, width, height, e);
		}

		public Node delete(Geometry g) {
			return this;
		}
		
		/**
		 * Returns if this node follows the rules of the PM Quadtree.
		 * 
		 * @return <code>true</code> if the node follows the rules of the PM
		 *         Quadtree; <code>false</code> otherwise
		 */
		public boolean isValid() {
			return true;
		}

		public String toString() {
			return "white";
		}
	}

	/**
	 * Black class represents a non-empty PM Quadtree leaf node. Black nodes are
	 * capable of storing both cities (points) and roads (line segments).
	 * <p>
	 * Each black node stores cities and roads into its own sorted geometry
	 * list.
	 * <p>
	 * Black nodes are split into a gray node if they do not satisfy the rules
	 * of the PM Quadtree.
	 */
	public class Black extends Node {

		/** list of cities and roads contained within black node */
		final protected LinkedList<Geometry> geometry;

		/** number of cities contained within this black node */
		protected int numPoints;

		/**
		 * Constructs and initializes a non-empty PM Quadtree leaf node.
		 */
		public Black() {
			super(BLACK);
			geometry = new LinkedList<Geometry>();
			numPoints = 0;
		}

		public Black(Black node) {
			super(BLACK);
			geometry = (LinkedList<Geometry>) node.getGeometry().clone();
			numPoints = node.numPoints;
		}
		/**
		 * Gets a linked list of the cities and roads contained by this black
		 * node.
		 * 
		 * @return list of cities and roads contained within this black node
		 */
		public LinkedList<Geometry> getGeometry() {
			return geometry;
		}

		/**
		 * Gets the index of the road in this black node's geometry list.
		 * 
		 * @param g
		 *            road to be searched for in the sorted geometry list
		 * @return index of the search key, if it is contained in the list;
		 *         otherwise, (-(insertion point) - 1)
		 */
		private int getIndex(final Geometry g) {
			return Collections.binarySearch(geometry, g);
		}

		public Node delete(Geometry g) {
			if (g.isRoad()) {
				Road r = (Road)g;
				PointWise start = r.getStart();
				PointWise end = r.getEnd();
				Road r2 = new Road(r.getEnd(), r.getStart());
				
				if (numRoadsForCity.containsKey(start.getName()) && 
						numRoadsForCity.get(start.getName()) == 1 && geometry.remove(r.getStart())) numPoints--;
				if (numRoadsForCity.containsKey(end.getName()) && 
						numRoadsForCity.get(end.getName()) == 1 && geometry.remove(r.getEnd())) numPoints--;
				
				geometry.remove(r2);
				geometry.remove(g);
			} else {
				if (geometry.remove(g)) numPoints--;
			}
			
			
			if (isValid()) {
				return this;
			} else return white;
		}
		
		/**
		 * Adds a road to this black node. After insertion, if the node becomes
		 * invalid, it will be split into a Gray node.
		 * @throws InvalidPartitionThrowable 
		 * @throws RoadIntersectAnotherRoadThrowable 
		 */
		public Node add(final Geometry g, final Point2D.Float origin,
				final int width, final int height, Err e) {
			
			if (g.isRoad()) {
				// g is a road
				Road r = (Road)g;
				/* create region rectangle */
				final Rectangle2D.Float rect = new Rectangle2D.Float(origin.x,
						origin.y, width, height);
			
				/* check if start point intersects with region */
				if (Inclusive2DIntersectionVerifier.intersects(r.getStart().getLocalPt(), rect)) {
					addGeometryToList(r.getStart());
				}
	
				/* check if end point intersects with region */
				if (Inclusive2DIntersectionVerifier.intersects(r.getEnd().getLocalPt(), rect)) {
					addGeometryToList(r.getEnd());
				}
			}

			/* add the road or isolated city to the geometry list */
			addGeometryToList(g);
			//][
			/* check if this node is valid */
			if (isValid()) {
				/* valid so return this black node */
				return this;
			} else {
				/* invalid so partition into a Gray node */
				return partition(origin, width, height, e);				
			}
		}

		/**
		 * Adds a road to this node's geometry list.
		 * 
		 * @param g
		 *            road to be added
		 */
		private boolean addGeometryToList(final Geometry g) {
			/* search for the non-existent item */
			final int index = getIndex(g);

			/* add the non-existent item to the list */
			if (index < 0) {
				geometry.add(-index - 1, g);

				if (g.isCity()) {
					// g is a city
					numPoints++;
				}
				return true;
			}
			return false;
		}

		/**
		 * Returns if this node follows the rules of the PM Quadtree.
		 * 
		 * @return <code>true</code> if the node follows the rules of the PM
		 *         Quadtree; <code>false</code> otherwise
		 */
		public boolean isValid() {
			return validator.valid(this);
		}

		/**
		 * Gets the number of cities contained in this black node.
		 * 
		 * @return number of cities contained in this black node
		 */
		public int getNumPoints() {
			return numPoints;
		}

		/**
		 * Partitions an invalid back node into a gray node and adds this black
		 * node's roads to the new gray node.
		 * 
		 * @param origin
		 *            origin of the rectangular bounds of this node
		 * @param width
		 *            width of the rectangular bounds of this node
		 * @param height
		 *            height of the rectangular bounds of this node
		 * @return the new gray node
		 * @throws InvalidPartitionThrowable
		 *             if the quadtree was partitioned too deeply
		 * @throws RoadIntersectAnotherRoadThrowable 
		 *             if two roads intersect
		 */
		private Node partition(final Point2D.Float origin, final int width, final int height, Err e) 
		{
			//][			
			/* create new gray node */
			if (width < 2 || height < 2) {
				e.intersect = true;
				e.pmrule = true;
				return this;
			}
			
			Node gray = new Gray(origin, width, height);

			// add isolated cities only; endpoints of roads are added in recursive calls
			// to black.add()
			for (int i = 0; i < numPoints; i++) {
				final Geometry g = geometry.get(i);
				if (isAirport(g)) {
					gray = gray.add(g, origin, width, height, e);
				}
			}			
			// add roads
			for (int i = numPoints; i < geometry.size(); i++) {
				final Geometry g = geometry.get(i);
				gray = gray.add(g, origin, width, height, e);
			}
			return gray;
		}

		/**
		 * Returns a string representing this black node and its road list.
		 * 
		 * @return a string representing this black node and its road list
		 */
		public String toString() {
			return "black: " + geometry.toString();
		}

		/**
		 * Returns if this black node contains a city.
		 * 
		 * @return if this black node contains a city
		 */
		public boolean containsCity() {
			return (numPoints > 0);
		}

		/**
		 * @return true if this black node contains at least a road
		 */
		public boolean containsRoad() {
			return (geometry.size() - numPoints) > 0;
		}

		/**
		 * If this black node contains a city, returns the city contained within
		 * this black node. Else returns <code>null</code>.
		 * 
		 * @return the city if it exists, else <code>null</code>
		 */
		public PointWise getPoint() {
			final Geometry g = geometry.getFirst();
			return g.isCity() ? (PointWise)g : null;
		}		
	}

	/**
	 * Gray class represents an internal PM Quadtree node.
	 */
	public class Gray extends Node {
		/** this gray node's 4 child nodes */
		final protected Node[] children;

		/** regions representing this gray node's 4 child nodes */
		final protected Rectangle2D.Float[] regions;

		/** origin of the rectangular bounds of this node */
		final protected Point2D.Float origin;

		/** the origin of rectangular bounds of each of the node's child nodes */
		final protected Point2D.Float[] origins;

		/** half the width of the rectangular bounds of this node */
		final protected int halfWidth;

		/** half the height of the rectangular bounds of this node */
		final protected int halfHeight;

		protected LinkedList<Geometry> geometry = new LinkedList<Geometry>();
		
		protected int numPoints;
		/**
		 * Constructs and initializes an internal PM Quadtree node.
		 * 
		 * @param origin
		 *            origin of the rectangular bounds of this node
		 * @param width
		 *            width of the rectangular bounds of this node
		 * @param height
		 *            height of the rectangular bounds of this node
		 */
		public Gray(final Point2D.Float origin, final int width,
				final int height) {
			super(GRAY);
			
			/* set this node's origin */
			this.origin = origin;

			/* initialize the children as white nodes */
			children = new Node[4];
			for (int i = 0; i < 4; i++) {
				children[i] = white;
			}

			/* get half the width and half the height */
			halfWidth = width >> 1;
			halfHeight = height >> 1;
			
			/* initialize the child origins */
			origins = new Point2D.Float[4];
			origins[0] = new Point2D.Float(origin.x, origin.y + halfHeight);
			origins[1] = new Point2D.Float(origin.x + halfWidth, origin.y
					+ halfHeight);
			origins[2] = new Point2D.Float(origin.x, origin.y);
			origins[3] = new Point2D.Float(origin.x + halfWidth, origin.y);

			/* initialize the child regions */
			regions = new Rectangle2D.Float[4];
			for (int i = 0; i < 4; i++) {
				regions[i] = new Rectangle2D.Float(origins[i].x, origins[i].y,
						halfWidth, halfHeight);
			}
			
			this.numPoints = 0;
		}
		
		public Gray(Gray node) {
			this(node.origin, node.halfWidth << 1, node.halfHeight << 1);
			
			for (int i = 0; i < 4; i++) {
				if (node.children[i].type == BLACK)
					children[i] = new Black((Black) node.children[i]);
				else if (node.children[i].type == GRAY)
					children[i] = new Gray((Gray) node.children[i]);
				else children[i] = white;
			}
			
			this.geometry = (LinkedList<Geometry>) node.geometry.clone();
			this.numPoints = node.numPoints;
		}

		public LinkedList<Geometry> getGeometry() {
			return this.geometry;
		}
		
		/**
		 * Adds a road to this PM Quadtree node.
		 * 
		 * @param g
		 *            road to be added
		 * @param origin
		 *            origin of the rectangular bounds of this node
		 * @param width
		 *            width of the rectangular bounds of this node
		 * @param height
		 *            height of the rectangular bounds of this node
		 * @return this node after the city has been added
		 * @throws InvalidPartitionThrowable
		 *             if the map if partitioned too deeply
		 * @throws RoadIntersectAnotherRoadThrowable 
		 * @throws IntersectingRoadsThrowable
		 *             if this road intersects with another road
		 */
		public Node add(final Geometry g, final Point2D.Float origin,
				final int width, final int height, Err e) {			
			
			
			for (int i = 0; i < 4; i++) {
				if (g.isRoad() && Inclusive2DIntersectionVerifier.intersects(
						((Road)g).toLine2D(),regions[i]) 
						|| g.isCity() && Inclusive2DIntersectionVerifier.intersects(
								((PointWise)g).getLocalPt(), regions[i])) {
					children[i] = children[i].add(g, origins[i], halfWidth,
							halfHeight, e);
				}
			}
			
			if (g.isCity()) this.numPoints++;
			this.geometry.add(g);
			
			return this; 
		}

		public Node delete(final Geometry g) {
			boolean allWhite = true;
			/*
			for (int i = 0; i < 4; i++) {
				this.children[i] = this.children[i].delete(g);
				if (this.children[i].type != Node.WHITE) allWhite = false;
			}*/
			
			Node newTemp = white;
			
			for (Geometry temp : root.getGeometry()) {
				if (!temp.equals(g)) {
					Err e = new Err();
					newTemp = newTemp.add(temp, spatialOrigin, spatialWidth, spatialHeight, e);
				}
			}
			
			return newTemp;
		}
		
		/**
		 * Returns if this node follows the rules of the PM Quadtree.
		 * 
		 * @return <code>true</code> if the node follows the rules of the PM
		 *         Quadtree; <code>false</code> otherwise
		 */
		public boolean isValid() {
			return children[0].isValid() && children[1].isValid()
					&& children[2].isValid() && children[3].isValid();
		}

		public String toString() {
			StringBuilder grayStringBuilder = new StringBuilder("gray:");
			for (Node child : children) {
				grayStringBuilder.append("\n\t");
				grayStringBuilder.append(child.toString());
			}
			return grayStringBuilder.toString();
		}

		/**
		 * Gets the child node of this node according to which quadrant it falls
		 * in.
		 * 
		 * @param quadrant
		 *            quadrant number (top left is 0, top right is 1, bottom
		 *            left is 2, bottom right is 3)
		 * @return child node
		 */
		public Node getChild(final int quadrant) {
			if (quadrant < 0 || quadrant > 3) {
				throw new IllegalArgumentException();
			} else {
				return children[quadrant];
			}
		}

		/**
		 * Gets the rectangular region for the specified child node of this
		 * internal node.
		 * 
		 * @param quadrant
		 *            quadrant that child lies within
		 * @return rectangular region for this child node
		 */
		public Rectangle2D.Float getChildRegion(int quadrant) {
			if (quadrant < 0 || quadrant > 3) {
				throw new IllegalArgumentException();
			} else {
				return regions[quadrant];
			}
		}

		/**
		 * Gets the center X coordinate of this node's rectangular bounds.
		 * 
		 * @return center X coordinate of this node's rectangular bounds
		 */
		public int getCenterX() {
			return (int) origin.x + halfWidth;
		}

		/**
		 * Gets the center Y coordinate of this node's rectangular bounds.
		 * 
		 * @return center Y coordinate of this node's rectangular bounds
		 */
		public int getCenterY() {
			return (int) origin.y + halfHeight;
		}

		/**
		 * Gets half the width of this internal node.
		 * 
		 * @return half the width of this internal node
		 */
		public int getHalfWidth() {
			return halfWidth;
		}

		/**
		 * Gets half the height of this internal node.
		 * 
		 * @return half the height of this internal node
		 */
		public int getHalfHeight() {
			return halfHeight;
		}
	}

	public PMQuadtree(final Validator validator, final int spatialWidth,
			final int spatialHeight, final int order) {
		if (order != 1 && order != 3) {
			throw new IllegalArgumentException("order must be one of: {1,3}");
		}

		root = white;
		this.validator = validator;
		this.spatialWidth = spatialWidth;
		this.spatialHeight = spatialHeight;
		spatialOrigin = new Point2D.Float(0.0f, 0.0f);
		allRoads = new TreeSet<Road>(new RoadNameComparator());
		numRoadsForCity = new HashMap<String, Integer>();
		citySet = new TreeSet<City>();
		this.order = order;
	}

	public Node getRoot() {
		return root;
	}
	
	public void addRoad(final Road g, Err e) {
		/*
		PointWise startCity = g.getStart();
		PointWise endCity = g.getEnd();
		*/
		
		Rectangle2D.Float world = new Rectangle2D.Float(spatialOrigin.x, spatialOrigin.y, 
				spatialWidth, spatialHeight);
			
		Node tempRoot = root.clone();
		root = root.add(g, spatialOrigin, spatialWidth, spatialHeight, e);
		if (!e.intersect && !e.pmrule) {
			allRoads.add(orderedRoad(g));
			roads.addRoad(g);
			
			if (Inclusive2DIntersectionVerifier.intersects(g.getStart().getLocalPt(), world)) {
				increaseNumRoadsMap(g.getStart().getName());
				if (g.getStart() instanceof City) citySet.add((City)g.getStart());
			}
			if (Inclusive2DIntersectionVerifier.intersects(g.getEnd().getLocalPt(), world)) {
				increaseNumRoadsMap(g.getEnd().getName());
				if (g.getEnd() instanceof City) citySet.add((City)g.getEnd());
			}
		} else root = tempRoot;
	}
	
	public void addIsolatedPoint(final PointWise c, Err e) {

		//numIsolatedCities++;
		Node tempRoot = root.clone();

		root = root.add(c, spatialOrigin, spatialWidth, spatialHeight, e);
		
		if (!e.intersect && !e.pmrule) {
			numRoadsForCity.put(c.getName(), 0);
		} else root = tempRoot;

	}

	/** Delete a passed in city from the pmtree */
	public void deletePoint(City city) {
		TreeSet<Road> cityRoadSet = (TreeSet<Road>) roads.getRoadSet(city).clone();
		
		for (Road r : cityRoadSet) {
			unmapRoad(r);
		}
	}
	
	/** When deleteAirport is called, we can assume that 
	 * all of the termianals associated to that airport are unmapped*/
	public void deleteAirport(Airport a) {
		root = root.delete(a);
	}
	
	
	/** Unmap a terminal road is equivalent to unmap the road and its associated terminal
	 * at the same time */
	public void unmapTerminalRoad(Road r) {
		Terminal start;
		City end;
		
		if (r.getStart() instanceof Terminal) {
			start = (Terminal) r.getStart();
			end = (City) r.getEnd();
		} else {
			start = (Terminal) r.getEnd();
			end = (City) r.getStart();
		}
		
		Integer numRoadsForEnd = numRoadsForCity.get(end.getName());
		
		root = root.delete(r);
		
		if (numRoadsForEnd == 1) {
			numRoadsForCity.remove(end.getName());
			citySet.remove(end);
			roads.deleteCity(end);
		} else {
			numRoadsForCity.put(end.getName(), --numRoadsForEnd);
		}
		
		roads.deleteCity(start);
		allRoads.remove(orderedRoad(r));
	}
	
	public Road orderedRoad(Road r) {
		if (r.getStart().getName().compareTo(r.getEnd().getName()) < 0) return r;
		else return new Road(r.getEnd(), r.getStart());
	}
	
	/** Unmap an undirected road from the tree*/
	public void unmapRoad(Road r) {
		PointWise start = r.getStart();
		PointWise end = r.getEnd();
		
		Integer numRoadsToEnd = numRoadsForCity.get(end.getName());
		Integer numRoadsToStart = numRoadsForCity.get(start.getName());
		
		Road orderedRoad = orderedRoad(r);

		root = root.delete(orderedRoad);
		allRoads.remove(orderedRoad);
		
		if (numRoadsToEnd == 1) {
			numRoadsForCity.remove(end.getName());
			citySet.remove(end);
			roads.deleteCity(end);
		} else {
			numRoadsForCity.put(end.getName(), --numRoadsToEnd);
		}
		
		if (numRoadsToStart == 1) {
			numRoadsForCity.remove(start.getName());
			citySet.remove(start);
			roads.deleteCity(start);
		} else {
			numRoadsForCity.put(start.getName(), --numRoadsToStart);
		}
		roads.removeRoad(orderedRoad);
	}
	
	private void increaseNumRoadsMap(final String name) {
		Integer numRoads = numRoadsForCity.get(name);
		if (numRoads != null) {
			numRoads++;
			numRoadsForCity.put(name, numRoads);
		} else {
			numRoadsForCity.put(name, 1);
		}
	}

	public void clear() {
		root = white;
		citySet.clear();
		allRoads.clear();
		numRoadsForCity.clear();
		roads.clear();
		//numIsolatedCities = 0;
	}

	public boolean isEmpty() {
		return (root == white);
	}

	public boolean containsPoint(final String name) {
		final Integer numRoads = numRoadsForCity.get(name);
		return (numRoads != null);
	}
	
	public boolean containsRoad(final Road road) {
		return allRoads.contains(road);
	}

	
	public boolean intersectsAnotherRoad(Road r) {
		for (Road temp : allRoads) {
			if (!temp.contains(r.getStart()) && !temp.contains(r.getEnd()) &&
				Inclusive2DIntersectionVerifier.intersects(r.toLine2D(), temp.toLine2D())) return true;
		}
		
		return false;
	}
	
	public int getOrder() {
		return order;
	}
	
	public int getNumCities() {
		return numRoadsForCity.keySet().size();
	}

	/*public int getNumIsolatedCities() {
		return numIsolatedCities;
	}*/
	
	public int getNumRoads() {
		return allRoads.size();
	}
	
	public boolean isAirport(Geometry g) {
		return g instanceof Airport;
	}
}
