package cmsc420.meeshquest.part2;

import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PriorityQueue<T> {
	private T[] cityArr;
	private int maxSize;
	private int size;
	private Comparator<T> comp;
	
	
	public PriorityQueue(Comparator<T> comp) {
		this(10, comp);
	}
	
	@SuppressWarnings("unchecked")
	public PriorityQueue(int m, Comparator<T> comp) {
		this.maxSize = m;
		this.size = 0;
		cityArr = (T[])new Object[m+1];
		this.comp = comp;
	}
	
	public Road getByName(String start, String end) {
		for (int i = 1; i <= size; i++) {
			Road r = (Road)cityArr[i];
			if ((r.getStart().equals(start) && r.getEnd().equals(end)) ||
				(r.getStart().equals(end) && r.getEnd().equals(start))) {
				return r;
			}
		}
		
		return null;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	@SuppressWarnings("unchecked")
	public void makeEmpty() {
		cityArr = (T[])new Object[11];
		size = 0;
		maxSize = 10;
	}
	
	public void setComp(Comparator<T> comp) {this.comp = comp;}
	
	public void push(T c) {
		if (size >= maxSize) resize();
		cityArr[++size] = c;
		int temp = size;
		
		while (temp != 1 && comp.compare(c, cityArr[temp/2]) > 0) {
			cityArr[temp] = cityArr[temp/2];
			temp >>= 1;
		}
		
		cityArr[temp] =c;
	}
	
	public void delete(T c) {
		delete(c, 1);
	}
	
	private void delete(T c, int i) {
		if (i > size) return;
		if (comp.compare(c, cityArr[i]) > 0) return;
		else if (comp.compare(c, cityArr[i]) < 0) {
			delete(c, 2*i);
			delete(c, 2*i+1);
		} else {
			if (cityArr[i].equals(c)) {
				T temp = cityArr[i];
				cityArr[i] = cityArr[size];
				cityArr[size--] = temp;
				
				while (i <= size/2) {i*=2;}
				for (i /= 2; i > 0; i/=2) {
					shift(i, size, cityArr[i]);
				}
			} else return;
		}
	}
	
	public T peek() {
		return cityArr[1];
	}
	
	public T pop() {
		if (size > 0) {
			T pop = cityArr[1];
			cityArr[1] = cityArr[size--];
			shift(1, size, cityArr[1]);
			return pop;
		} else return null;
	}
	//public int find(City c) {
		//if (comp.compare(c, city))
	//}
	
	@SuppressWarnings("unchecked")
	public void resize() {
			maxSize *= 2;
			T[] temp = cityArr;
			cityArr = (T[])new Object[maxSize+1];
			
			for (int i = 1;  i <= size; i++) {
				cityArr[i] = temp[i];
			}
	}
	
	public int getSize() {
		return size;
	}
	
	public void createHeap() {
		for (int r = size/2; r > 0; r--) {
			shift(r, size, cityArr[r]);
		}
	}
	
	public void sort() {
				
		for (int r = size/2; r > 0; r--) {
			shift(r, size, cityArr[r]);
		}
		
		for (int m = size; m > 1; m--){
			T temp = cityArr[m];
			cityArr[m] = cityArr[1];
			shift(1, m-1, temp);
		}
	}
	
	public void shift(int r, int n, T city) {
		int p = r;
		int c;
		
		while (2*p <= n) {
			if (2*p < n) {
				if (comp.compare(cityArr[2*p] ,(cityArr[2*p+1])) > 0) {
					c = 2*p;
				} else c = 2*p + 1;
			} else {
				c = 2*p;
			}
			
			if (comp.compare(cityArr[c] ,city) > 0) {
				
				cityArr[p] = cityArr[c];
				//cityArr[c] = cityArr[p];
				p = c;
			} else {
				break;
			}
		}
		cityArr[p] = city;
	}		
	
	public PriorityQueue<T> clone() {
		PriorityQueue<T> clone = new PriorityQueue<T>(this.maxSize, this.comp);
		clone.cityArr =this.cityArr.clone();
		clone.size = this.size;
		
		return clone;
	}
	
	public String toString() {
		String ret = "";
		
		for (int i = 1; i <= size; i++) {
			ret += "(" + cityArr[i].toString()+ "), ";		
		}
		
		return ret;
	}
	
	public static void main (String[] args) {
		
		RadiusComparator comp = new RadiusComparator(0,0);
		PriorityQueue<City> q = new PriorityQueue<City>(10, comp);

		
		City c1 = new City("A", 1,1,5,"black");
		q.push(c1);
		q.push(new City("A", 80,30,5,"black"));
		q.push(new City("Baltimore", 30, 30, 5, "black"));
		q.push(new City("Los Angles", 45, 45, 5, "BLACK"));
		q.push(new City("Chicago", 60, 60, 5, "black"));
		q.push(new City("Chicago", 15, 15, 5, "black"));
		q.push(new City("Chicago", 70, 60, 5, "black"));

		//q.size = 7;
		

		//q.sort();
		
		System.out.println(q.toString());
		System.out.println(q.pop());
		System.out.println(q.pop());
		System.out.println(q.pop());
		System.out.println(q.toString());
		/*
		Arc2D.Double a = new Arc2D.Double();
		Point p1 = new Point(0,0);
		Point p2 = new Point(0,10);
		Point p3 = new Point (-10,10);
		a.setArcByTangent(p1, p2, p3, 1);
		System.out.println( a.getAngleExtent() );
		 */
	}
}
