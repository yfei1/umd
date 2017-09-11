package cmsc420.pmquadtree;

public class PM1Quadtree extends PMQuadtree{
	
	public PM1Quadtree(final int spatialWidth, final int spatialHeight) {
		super(new PM1Validator(), spatialWidth, spatialHeight, 1);
	}
}
