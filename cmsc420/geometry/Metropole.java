package cmsc420.geometry;

import cmsc420.pmquadtree.PM1Quadtree;
import cmsc420.pmquadtree.PM3Quadtree;
import cmsc420.pmquadtree.PMQuadtree;

public class Metropole implements Comparable<Metropole>{
	private int remoteX;
	private int remoteY;
	private PMQuadtree pmQuadtree;
	
	public Metropole(int remoteX, int remoteY, int localWidth, int localHeight, int pmOrder) {
		this.remoteX = remoteX;
		this.remoteY = remoteY;
		
		if (pmOrder == 1) setPMQuadtree(new PM1Quadtree(localWidth, localHeight));
		else setPMQuadtree(new PM3Quadtree(localWidth, localHeight));
	}
	
	public boolean metropoleOutOfBound(int remoteWidth, int remoteHeight) {
		if (remoteX >= remoteWidth || remoteX < 0 || remoteY >= remoteHeight || remoteY < 0) return true;
		
		return false;
	}
	
	public int getRemoteX() {
		return remoteX;
	}
	public void setRemoteX(int remoteX) {
		this.remoteX = remoteX;
	}
	public int getRemoteY() {
		return remoteY;
	}
	public void setRemoteY(int remoteY) {
		this.remoteY = remoteY;
	}

	public PMQuadtree getPMQuadtree() {
		return pmQuadtree;
	}

	public void setPMQuadtree(PMQuadtree quadtree) {
		this.pmQuadtree = quadtree;
	}
	
	@Override
	public int compareTo(Metropole m) {
		if (this.remoteY != m.remoteY) return this.remoteY - m.remoteY;
		else return this.remoteX - m.remoteX;
	}
}
