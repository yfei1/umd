package cmsc420.meeshquest.part2;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

public class Dijkstra {
	
	private class DijkstraComparator implements Comparator<City>{
		@Override
		public int compare(City o1, City o2) {
			int segDist = (int)(shortestDistances.get(o2) - shortestDistances.get(o1));
			
			if (segDist == 0) {
				return -o1.compareTo(o2);
			} else return segDist;
		}
	}
	
	private double dist;
	private final Set<City> settledNodes = new TreeSet<City>();
	private final TreeMap<City, Double> shortestDistances = new TreeMap<City, Double>();
	private final Map<City, City> predecessors = new TreeMap<City, City>();
	private PriorityQueue<City> ts = null;
	
	
	public Dijkstra(String start, Map<City, TreeSet<City>> m) {
		
		ts = new PriorityQueue<City>(new DijkstraComparator());
		Set<Entry<City, TreeSet<City>>> e = m.entrySet();		
		Iterator<Entry<City, TreeSet<City>>> iter = e.iterator();
		
		while(iter.hasNext()) {
			Entry<City, TreeSet<City>> entry = iter.next();
			City xiti = entry.getKey();
			
			if(!xiti.getName().equals(start)) shortestDistances.put(xiti, Double.MAX_VALUE*0.05);
			else {
				shortestDistances.put(xiti, 0.0);
				ts.push(xiti);
			}			
			predecessors.put(xiti, null);
		}
	}
	
	public String retrieveLastDist() {return new DecimalFormat("0.000").format(dist);}
	
	public static double length(City c1, City c2) {
		return Math.sqrt(Math.pow((c1.getX()-c2.getX()),2) + Math.pow((c2.getY()- c1.getY()),2));
	}
	
	public Stack<City> shortestPath(City start, City end, TreeMap<City, TreeSet<City>> m) {
		
		while (!ts.isEmpty()) {
			City currentCity = ts.pop();
			
			
			if (currentCity.getName().equals(end.getName())) {
				Stack<City> s = new Stack<City>();
				dist = shortestDistances.get(currentCity);
				City parent;
				
				
				while ((parent = predecessors.get(currentCity)) != null) {
					s.push(currentCity);	
					currentCity = parent;
				}
				s.push(currentCity);
				
				return s;
			}
			
			settledNodes.add(currentCity);

			
			for (City o : m.get(currentCity)) {
				if (!settledNodes.contains(o)) {
					Double possibleNull = shortestDistances.get(o);
					
					if (possibleNull != null) {
						double segDist = length(o, currentCity);
						double currLength = shortestDistances.get(currentCity) + segDist;
						
						if (currLength < possibleNull) {
							shortestDistances.put(o, currLength);
							predecessors.put(o, currentCity);
							ts.push(o);
						}
					}
				}
			}
		}
		
		return null;
	}
}
