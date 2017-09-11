package cmsc420.meeshquest.part2;
import java.util.Comparator;
import java.util.TreeSet;

public class LeafNode extends Node{
	City c;
	TreeSet<Road> arr = new TreeSet<Road>(
			new Comparator<Road>() {
				@Override
				public int compare(Road r1, Road r2) {
					int com = r1.getStart().compareTo(r2.getStart());
					if (com != 0) return -com;
					else return -r1.getEnd().compareTo(r2.getEnd());
				}
			}
	);	
		
	public LeafNode() {
		
	}
	@Override
	public LeafNode clone() {
		return new LeafNode(c);
		
	}
	
	public LeafNode(City c) {
		this.c = c;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("City:");
		
		if (c == null) sb.append("()");
		else {
			sb.append(c.getName());
			sb.append("[(");
			sb.append(c.getX());
			sb.append(",");
			sb.append(c.getY());
			sb.append("),");
		}
		sb.append(arr.toString());
		
		return sb.toString();
	}
	
	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean isNull() {
		// TODO Auto-generated method stub
		return false;
	}
}
