package cmsc420.meeshquest.part2;

import java.util.Comparator;

public class CityCoordinateComparator implements Comparator<City> {

	@Override
	public int compare(City a, City b) {
		if (((City)a).y > ((City)b).y)
			return 1;
		else if (((City)a).y < ((City)b).y)
			return -1;
		else return ((City)a).x - ((City)b).x;
	}
	
}
