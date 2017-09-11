package cmsc420.geometry ;

//Checked
import java.util.Comparator;

/**
 * Compares two cities based on their names.
 * 
 * @author Ben Zoller
 * @version 1.0, 23 Jan 2007
 */
public class PointNameComparator implements Comparator<PointWise> {
	public int compare(final PointWise c1, final PointWise c2) {
		return c2.getName().compareTo(c1.getName());
	}
}