package cmsc420.meeshquest.part1;

import java.util.Comparator;

public class CityCoordinateComparator implements Comparator {

	@Override
	public int compare(Object a, Object b) {
		if (((City)a).y > ((City)b).y)
			return 1;
		else if (((City)a).y < ((City)b).y)
			return -1;
		else return ((City)a).x - ((City)b).x;
	}
	
}
