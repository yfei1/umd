package cmsc420.pmquadtree;

import java.util.LinkedList;

import cmsc420.geometry.Geometry;
import cmsc420.geometry.PointWise;
import cmsc420.geometry.Road;
import cmsc420.pmquadtree.PMQuadtree.Black;

public class PM1Validator implements Validator {

	@Override
	public boolean valid(Black node) {
		if (node.numPoints > 1) return false;
		else if (node.numPoints == 1) {
			LinkedList<Geometry> rl = node.getGeometry();
			PointWise p = node.getPoint();
			
			for (int i = 1; i < rl.size(); i++) {
				Road r = (Road) rl.get(i);
				
				if (!r.contains(p)) return false;
			}
		} else if (node.getGeometry().size() != 1) {
			return false;
		}
		else {
			int size = node.getGeometry().size();
			if (size > 1 || size == 0) return false;
		}
		
		return true;
	}

}
