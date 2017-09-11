package cmsc420.geometry;

import java.awt.geom.Line2D;


/**
 * Road class provides an analogue to real-life roads on a map. A Road connects
 * one {@link cmsc420.geometry.PointWise} to another PointWise. The distance between the two
 * PointWise is calculated when the road is constructed to save time in distance
 * calculations. Note: roads are not interchangeable. That is, Road (A,B) is not
 * the same as Road (B,A).
 */
public class Road extends Geometry {
	/** start PointWise */
	protected PointWise start;

	/** end PointWise */
	protected PointWise end;

	/** distance from start PointWise to end PointWise */
	protected double distance;
	
	/**
	 * Constructs a new road based on start PointWise and end PointWise. Calculates and
	 * stores the distance between them.
	 * 
	 * @param start
	 *            start PointWise
	 * @param end
	 *            end PointWise
	 */
	public Road(final PointWise start, final PointWise end) {
		if (end.getName().compareTo(start.getName()) < 0) {
			this.start = end;
			this.end = start;
		} else {
			this.start = start;
			this.end = end;
		}
		distance = start.pt.distance(end.pt);
	}

	/**
	 * Gets the start PointWise.
	 * 
	 * @return start PointWise
	 */
	public PointWise getStart() {
		return start;
	}

	/**
	 * Gets the end PointWise.
	 * 
	 * @return end PointWise
	 */
	public PointWise getEnd() {
		return end;
	}

	/**
	 * Gets the distance between the start and end PointWises.
	 * 
	 * @return distance between the two PointWises
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Returns a string representing a road. For example, a road from city A to
	 * city B will print out as: (A,B).
	 * 
	 * @return road string
	 */
	public String getPointNameString() {
		return "(" + start.getName() + "," + end.getName() + ")";
	}

	/**
	 * If the name of the start city is passed in, returns the name of the end
	 * city. If the name of the end city is passed in, returns the name of the
	 * start city. Else throws an <code>IllegalArgumentException</code>.
	 * 
	 * @param cityName
	 *            name of city contained by the road
	 * @return name of the other city contained by the road
	 * @throws IllegalArgumentException
	 *             city name passed in was not contained by the road
	 */
	public PointWise getOtherPoint(final String cityName) {
		if (start.getName().equals(cityName)) {
			return end;
		} else if (end.getName().equals(cityName)) {
			return start;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Returns a line segment representation of the road which extends
	 * Line2D.Double.
	 * 
	 * @return line segment representation of road
	 */
	public Line2D toLine2D() {
		return new Line2D.Float(start.pt, end.pt);
	}

	/**
	 * Determines if one road is equal to another.
	 * 
	 * @param other
	 *            the other road
	 * @return <code>true</code> if the roads are equal, false otherwise
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && (obj.getClass().equals(this.getClass()))) {
			Road r = (Road) obj;
			return (start.equals(r.start) && end.equals(r.end) && distance == r.distance) ||
					(start.equals(r.end) && end.equals(r.start) && distance == r.distance);
		}
		return false;
	}

	/**
	 * Returns the hash code value of a road.
	 */
	public int hashCode() {
		final long dBits = Double.doubleToLongBits(distance);
		int hash = 35;
		hash = 37 * hash + start.hashCode();
		hash = 37 * hash + end.hashCode();
		hash = 37 * hash + (int) (dBits ^ (dBits >>> 32));
		return hash;
	}

	public boolean contains(PointWise point) {
		return (point.equals(start) || point.equals(end));
	}
	
	public String toString() {
		return getPointNameString();
	}

	@Override
	public int getType() {
		return SEGMENT;
	}
}
