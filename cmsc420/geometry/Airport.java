package cmsc420.geometry;

public class Airport extends PointWise{
	
	public Airport(int localX, int localY, int remoteX, int remoteY, String name) {
		super(localX, localY, remoteX, remoteY, name);
	}
	

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return POINT;
	}

}
