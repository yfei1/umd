package cmsc420.meeshquest.part1;

import java.util.Comparator;

public class PriorityQueue {
	private City[] cityArr;
	private int maxSize;
	private int size;
	private Comparator<City> comp;
	
	public PriorityQueue(int m, Comparator<City> comp) {
		this.maxSize = m;
		this.size = 0;
		cityArr = new City[m+1];
		this.comp = comp;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public void makeEmpty() {
		cityArr = new City[11];
		size = 0;
		maxSize = 10;
	}
	
	public void setComp(Comparator<City> comp) {this.comp = comp;}
	
	public void push(City c) {
		if (size >= maxSize) resize();
		cityArr[++size] = c;
		
		for(int i = size/2; i > 0; i/=2) {
			shift(i, size, cityArr[i]);
		}
	}
	
	public void delete(City c) {
		delete(c, 1);
	}
	
	private void delete(City c, int i) {
		if (i > size) return;
		if (comp.compare(c, cityArr[i]) > 0) return;
		else if (comp.compare(c, cityArr[i]) < 0) {
			delete(c, 2*i);
			delete(c, 2*i+1);
		} else {
			if (cityArr[i].name.equals(c.name)) {
				City temp = cityArr[i];
				cityArr[i] = cityArr[size];
				cityArr[size--] = temp;
				
				while (i <= size/2) {i*=2;}
				for (i /= 2; i > 0; i/=2) {
					shift(i, size, cityArr[i]);
				}
			} else return;
		}
	}
	
	public City peek() {
		return cityArr[size];
	}
	
	public City pop() {
		if (size > 0)
			return cityArr[size--];
		else return null;
	}
	//public int find(City c) {
		//if (comp.compare(c, city))
	//}
	
	public void resize() {
			maxSize *= 2;
			City[] temp = cityArr;
			cityArr = new City[maxSize+1];
			
			for (int i = 1;  i <= size; i++) {
				cityArr[i] = temp[i];
			}
	}
	
	public void sort() {
		for (int r = size/2; r > 0; r--) {
			shift(r, size, cityArr[r]);
		}
		
		for (int m = size; m > 1; m--){
			City temp = cityArr[m];
			cityArr[m] = cityArr[1];
			shift(1, m-1, temp);
		}
	}
	
	public void shift(int r, int n, City city) {
		int p = r;
		int c;
		
		while (2*p <= n) {
			if (2*p < n) {
				if (comp.compare(cityArr[2*p] ,(cityArr[2*p+1])) > 0) {
					c = 2*p;
				} else if (comp.compare(cityArr[2*p] ,(cityArr[2*p+1])) == 0){
					if (cityArr[2*p].name.compareTo(cityArr[2*p+1].name) > 0) {
						c = 2*p;
					} else {
						c = 2*p + 1;
					}
				} else c = 2*p + 1;
			} else {
				c = 2*p;
			}
			
			if (comp.compare(cityArr[c] ,city) > 0) {
				
				cityArr[p] = cityArr[c];
				cityArr[c] = city;
				p = c;
			} else {
				break;
			}
		}
		cityArr[p] = city;
	}		
	
	public PriorityQueue clone() {
		PriorityQueue clone = new PriorityQueue(this.maxSize, this.comp);
		clone.cityArr =this.cityArr.clone();
		clone.size = this.size;
		
		return clone;
	}
	
	public String toString() {
		String ret = "";
		
		for (int i = 1; i <= size; i++) {
			ret += "(" + cityArr[i].x + "," + cityArr[i].y + "), ";		
		}
		
		return ret;
	}
	
	public static void main (String[] args) {
		
		RadiusComparator comp = new RadiusComparator(0,0);
		PriorityQueue q = new PriorityQueue(10, comp);

		q.push(new City("A", 1,1,5,"black"));
		q.push(new City("A", 15,15,5,"black"));
		q.push(new City("Baltimore", 30, 30, 5, "black"));
		q.push(new City("Los Angles", 45, 45, 5, "BLACK"));
		q.push(new City("Chicago", 60, 60, 5, "black"));
		q.push(new City("Chicago", 70, 60, 5, "black"));
		q.push(new City("Chicago", 80, 30, 5, "black"));

		//q.size = 7;
		
		System.out.println(q.toString());


		q.sort();
		
		System.out.println(q.toString());
		System.out.println(q.pop());
		System.out.println(q.pop());
		System.out.println(q.pop());
		System.out.println(q.toString());

	}
}
