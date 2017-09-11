package cmsc420.meeshquest.part1;

public class City implements Comparable<City>{
	String name;
	int x;
	int y;
	int radius;
	String color;
	
	public City(String name, int x, int y, int radius, String c) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = c;
	}
	
	public String toString() {
		
		return "Name:" + name +
				";X:" + x + 
				";Y:" + y +
				";Radius:" + radius + 
				";Color:" + color;
		
	}

	@Override
	public int compareTo(City a) {
		if(this.name.compareTo(a.name) >0)
			return -1;
		else if (this.name.compareTo(a.name) < 0)
			return 1;
		else return 0;
	}
}
