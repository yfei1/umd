package cmsc420.meeshquest.part2;

public class City implements Comparable<City>{
	String name;
	int x;
	int y;
	private int radius;
	private String color;
	private boolean isolated;
	
	public City(String name, int x, int y, int radius, String c) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.setRadius(radius);
		this.setColor(c);
	}
	
	
	public String toString() {
		
		return "Name:" + name +
				";X:" + x + 
				";Y:" + y +
				";Radius:" + getRadius() + 
				";Color:" + getColor();
		
	}

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public int compareTo(City a) {
		return this.getName().compareTo(a.getName());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		City temp = (City)o;
		
		if (this.name.equals(temp.name) &&
			this.getRadius() == temp.getRadius() &&
			this.getColor().equals(temp.getColor()) &&
			this.x == temp.x &&
			this.y == temp.y) return true;
		else return false;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isIsolated() {
		return isolated;
	}

	public void setIsolated(boolean isolated) {
		this.isolated = isolated;
	}
}
