package cmsc420.pmquadtree;

public class InvalidIsolatedCityThrowable extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidIsolatedCityThrowable() {
    }

    public InvalidIsolatedCityThrowable(String msg) {
    	super(msg);
    } 
}
