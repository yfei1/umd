package cmsc420.geometry ;

//Checked
import java.util.Comparator;

/**
 * Compares two cities based on location of x and y coordinates. First compares
 * the x values of each {@link City}. If the x values are the same, then the y values of
 * each City are compared.
 * 
 * @author Ben Zoller
 * @editor Ruofei Du
 * @version 1.0, 23 Jan 2007
 */
public class PointWiseLocationComparator implements Comparator<PointWise> {

	public int compare(final PointWise one, final PointWise two) {
		if (one.getRemoteY() < two.getRemoteY()) {
			return -1;
		} else if (one.getRemoteY() > two.getRemoteY()) {
			return 1;
		} else {
			/* one.getRemoteY() == two.getRemoteY() */
			if (one.getRemoteX() < two.getRemoteX()) {
				return -1;
			} else if (one.getRemoteX() > two.getRemoteX()) {
				return 1;
			} else {
				/* one.getX() == two.getX() */
				if (one.getLocalY() < two.getLocalY())  return -1;
				else if (one.getLocalY() > two.getLocalY()) return 1;
				else return one.getLocalX() - two.getLocalX();
			}
		}
	}
}