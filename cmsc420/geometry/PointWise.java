package cmsc420.geometry;

import java.awt.geom.Point2D;

public class PointWise extends Geometry{
	/** name of this city */
	protected String name;

	protected Point2D.Float remotept;
	
	/** 2D coordinates of this city */
	protected Point2D.Float pt;

	public PointWise(int localX, int localY, int remoteX, int remoteY, String name) {
		this.name = name;
		this.pt = new Point2D.Float(localX, localY);
		this.remotept = new Point2D.Float(remoteX, remoteY);
	}
	
	public PointWise(Point2D.Float local, Point2D.Float remote, String name) {
		this.name = name;
		this.pt = local;
		this.remotept = remote;
	}
	
	
	/** test if two objects are equal by location*/
	public boolean equals(Object o) {
		if (o == this) return true;
		else if (o != null && o instanceof PointWise) {
			PointWise temp = (PointWise)o;
			
			return temp.pt.equals(this.pt) && temp.remotept.equals(this.remotept);
		}
		
		return false;
	}
	
	public Point2D.Float getLocalPt() {
		return pt;
	}
	
	public Point2D.Float getRemotePt() {
		return this.remotept;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRemoteX() {
		return (int)remotept.x;
	}

	public void setRemoteX(int remotex) {
		this.remotept.x = remotex;
	}
	
	public int getRemoteY() {
		return (int)remotept.y;
	}

	public void setRemoteY(int remotey) {
		this.remotept.y = remotey;
	}

	public int getLocalX() {
		return (int)this.pt.x;
	}

	public void setLocalX(int localx) {
		this.pt.x = localx;
	}

	public int getLocalY() {
		return (int)this.pt.y;
	}

	public void setLocalY(int localy) {
		this.pt.y = localy;
	}
	
	@Override
	public int getType() {
		return POINT;
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
