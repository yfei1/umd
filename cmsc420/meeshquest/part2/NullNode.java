package cmsc420.meeshquest.part2;

public class NullNode extends Node{
	
	private NullNode(PointNode p) { this.parent = p;}
	
	public static NullNode getInstance(PointNode p) {
		return new NullNode(p);
	}

	public String toString() {
		return "NULL";
	}
	
	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNull() {
		// TODO Auto-generated method stub
		return true;
	}
}
