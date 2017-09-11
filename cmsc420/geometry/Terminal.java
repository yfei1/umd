package cmsc420.geometry;

import java.awt.geom.Point2D;

public class Terminal extends PointWise{
	
	Road terminalToCity;
	Airport airport;

	public Terminal(Airport a, int localX, int localY, int remoteX, int remoteY, String name) {
		super(localX, localY, remoteX, remoteY, name);
		airport = a;
	}
	
	public Terminal(Airport a, int localX, int localY, int remoteX, int remoteY, String name, Road rd) {
		super(localX, localY, remoteX, remoteY, name);
		terminalToCity = rd;
		airport = a;
	}

	public Terminal(Airport a, Point2D.Float local, Point2D.Float remote, String name, Road rd) {
		super(local, remote, name);
		this.terminalToCity = rd;
		airport = a;
	}
	
	public Airport getAirport() {
		return airport;
	}

	public void setAirport(Airport airport) {
		this.airport = airport;
	}

	public Road getTerminalToCity() {
		return terminalToCity;
	}

	public void setTerminalToCity(Road terminalToCity) {
		this.terminalToCity = terminalToCity;
	}
	
}
