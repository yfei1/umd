package cmsc420.meeshquest.part2;

import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;
import cmsc420.drawing.CanvasPlus;
import cmsc420.sortedmap.AvlGTree;

public class MeeshQuest {
	private static void checkNSwap(Road r) {
		String start = r.getStart(), end = r.getEnd();
		
		if (start.compareTo(end) > 0) {
			r.setStart(end);
			r.setEnd(start);
		}
	}
	
	private static void printQuad(Node n, Document d, Element quad) {
		if (n.isNull()) {
			Element white = d.createElement("white");
			quad.appendChild(white);
		} else if (n.isLeaf()) {
			City c = ((LeafNode)n).c;
			Element black = d.createElement("black");
			black.setAttribute("cardinality", ((LeafNode)n).arr.size() + (c == null? 0:1) + "");
			if (c != null) {
				Element city;
				
				if (c.isIsolated()) city = d.createElement("isolatedCity");	
				else city = d.createElement("city");	
				 
				city.setAttribute("name" ,c.getName());
				city.setAttribute("x", c.getX()+"");
				city.setAttribute("y", c.getY()+"");
				city.setAttribute("color", c.getColor());
				city.setAttribute("radius", c.getRadius()+"");
				black.appendChild(city);
			}
			
			Iterator<Road> iter = ((LeafNode)n).arr.iterator();
			
			while (iter.hasNext()) {
				
				Road e = iter.next();
				Element rd = d.createElement("road");
				rd.setAttribute("start", e.getStart());
				rd.setAttribute("end", e.getEnd());
				black.appendChild(rd);
			}
			quad.appendChild(black);
		} else {
			PointNode p = (PointNode)n;
			Element gray = d.createElement("gray");
			gray.setAttribute("x", p.getX()+"");
			gray.setAttribute("y", p.getY()+"");
			quad.appendChild(gray);
			
			printQuad(p.narr[0], d, gray);
			printQuad(p.narr[1], d, gray);
			printQuad(p.narr[2], d, gray);
			printQuad(p.narr[3], d, gray);

		}
	}
	
	private static void parSetter(Document res, Element err, String par_name, String par_val) {
		Element par = (Element)err.getElementsByTagName("parameters").item(0);
		
		Element param = res.createElement(par_name);
		param.setAttribute("value", par_val);
		
		par.appendChild(param);
	}
	
	public static Element printError(Document res, String err_type, String err_cmd, String id) {
		Element err = res.createElement("error");
		err.setAttribute("type", err_type);
		
		Element cmd = res.createElement("command");
		cmd.setAttribute("name", err_cmd);
		if (!id.equals("")) cmd.setAttribute("id", id);
		
		Element par = res.createElement("parameters");
		
		err.appendChild(cmd);
		err.appendChild(par);
		
		return err;
	}
	
	public static Element printSuccess(Document res, String cmd_name, String id) {
		Element suc = res.createElement("success");
		
		Element cmd = res.createElement("command");
		cmd.setAttribute("name", cmd_name);
		if (!id.equals("")) cmd.setAttribute("id", id);
		suc.appendChild(cmd);
		
		Element par = res.createElement("parameters");
		suc.appendChild(par);
		
		return suc;
	}
	
    public static void main(String[] args) {
    	
    	Document results = null;
    	
        try {
        	//Redirect StdIOStream
        	//System.setIn(new FileInputStream("act1.in.xml"));
        	//System.setOut(new PrintStream(new File("test.xml")));
        	
        	//Create two maps: nameToCity and cityToName
        	CityCoordinateComparator comp = new CityCoordinateComparator();
        	TreeMap<String, City> ntc = new TreeMap<String, City>(new Comparator<String>() {
        		public int compare(String o1, String o2) {
					return -o1.compareTo(o2);
        		}
        	});

        	TreeMap<City, String> ctn = new TreeMap<City, String>(comp);
        	
        	Document doc = XmlUtility.validateNoNamespace(System.in);
        	//Document doc = XmlUtility.parse(new File("short2.in.xml"));
        	results = XmlUtility.getDocumentBuilder().newDocument();
        	Element res = results.createElement("results");
			results.appendChild(res);

        	Element commandNode = doc.getDocumentElement();
        	
        	//Initialize the PM3 QuadTree
        	int width = Integer.parseInt(commandNode.getAttribute("spatialWidth"));
        	int height = Integer.parseInt(commandNode.getAttribute("spatialHeight"));
        	int pmOrder = Integer.parseInt(commandNode.getAttribute("pmOrder"));
        	int g = Integer.parseInt(commandNode.getAttribute("g"));
        	
    		CanvasPlus c = new CanvasPlus("MeeshQuest",width,height);
    		
        	AvlGTree<String, City> avl = new AvlGTree<String, City>(g);

    		PM3QuadTree pr_tree;
    		if (pmOrder == 1) {;}
    		else  ;
    		pr_tree = new PM3QuadTree(c, width, height);
    		
        	Set<City> citySet = new TreeSet<City>();
			PriorityQueue<City> q = new PriorityQueue<City>(10, new RadiusComparator(0,0));
			PriorityQueue<Road> road_q = new PriorityQueue<Road>(10, new RoadComparator(0,0));
        	
        	final NodeList nl = commandNode.getChildNodes();
        	for (int i = 0; i < nl.getLength(); i++) {
        		if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
        			commandNode = (Element) nl.item(i);
        			
        			String start = commandNode.getAttribute("start");
        			String end = commandNode.getAttribute("end");
        			String id = commandNode.getAttribute("id");
        			String name = commandNode.getAttribute("name");
    				String x = commandNode.getAttribute("x");
    				String y = commandNode.getAttribute("y");
    				String radius = commandNode.getAttribute("radius");
    				String color = commandNode.getAttribute("color");
    				String savefile = commandNode.getAttribute("saveMap");
    				
        			switch(commandNode.getNodeName()){
        			
        			//Part2 Done
        			case "createCity": {
        				//Document results = XmlUtility.getDocumentBuilder().newDocument();
        				//Element elt = results.createElement("elementName");
        				//elt.setAttribute("attributeName", "attributeValue");
        				//results.appendChild(elt);
        				//XmlUtility.print(results);
        				// getNodeName(), getNodeValue(), getAttribute().
        				City curr = new City(name, Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(radius), color);
        				Set<Map.Entry<City,String>> entrySet = ctn.entrySet();
        				boolean dupCoordinates = false;
        				
        				for (Entry<City, String> e : entrySet) {
        					if (((City)e.getKey()).getX() == curr.getX() && ((City)e.getKey()).getY() == curr.getY()) {
            					Element err = printError(results, "duplicateCityCoordinates", "createCity", id);
            					parSetter(results, err, "name", curr.getName());
            					parSetter(results, err, "x", curr.getX()+"");
            					parSetter(results, err, "y", curr.getY()+"");
            					parSetter(results, err, "radius", curr.getRadius()+"");
            					parSetter(results, err, "color", curr.getColor());
            					res.appendChild(err);
            					dupCoordinates = true;
        					}
        				}
        				
        				if (!dupCoordinates) {
	        				if (ntc.containsKey(name)) {
	        					Element err = printError(results, "duplicateCityName", "createCity", id);
	        					parSetter(results, err, "name", curr.getName());
	        					parSetter(results, err, "x", curr.getX()+"");
	        					parSetter(results, err, "y", curr.getY()+"");
	        					parSetter(results, err, "radius", curr.getRadius()+"");
	        					parSetter(results, err, "color", curr.getColor());
	        					res.appendChild(err);
	        				}else  {
	        					ntc.put(name, curr);
	        					ctn.put(curr, name);
	        					avl.insert(name, curr);
	        					Element suc = printSuccess(results, "createCity", id);
	        					parSetter(results, suc, "name", curr.getName());
	        					parSetter(results, suc, "x", curr.getX()+"");
	        					parSetter(results, suc, "y", curr.getY()+"");
	        					parSetter(results, suc, "radius", curr.getRadius()+"");
	        					parSetter(results, suc, "color", curr.getColor());
	        					suc.appendChild(results.createElement("output"));

	        					res.appendChild(suc);
	        				}
        				}
        					break;
        			}
        			
        			/* Obsolete in part 2
        			case "deleteCity":
        				if (ntc.containsKey(name)) {
        					City temp = ntc.get(name);
        					
        					Element out = results.createElement("output");
        					
        					if (nameSet.contains(name)) {
        						pr_tree.delete(temp);
        						q.delete(temp);
        						Element cityUnmapped = results.createElement("cityUnmapped");
        						cityUnmapped.setAttribute("name", temp.getName());
        						cityUnmapped.setAttribute("x", temp.getX()+"");
        						cityUnmapped.setAttribute("y", temp.getY()+"");
        						cityUnmapped.setAttribute("radius", temp.getRadius()+"");
        						cityUnmapped.setAttribute("color", temp.getColor());
        						out.appendChild(cityUnmapped);
        					}
        					ntc.remove(name);
        					ctn.remove(temp);
        					
        					Element elt = results.createElement("success");
        					res.appendChild(elt);
        					
        					Element cmd = results.createElement("command");
        					cmd.setAttribute("name", "deleteCity");
        					cmd.setAttribute("id", id);
        					elt.appendChild(cmd);
        					
        					Element par = results.createElement("parameters");
        					elt.appendChild(par);
        					
        					Element city_name = results.createElement("name");
        					city_name.setAttribute("value", name);
        					par.appendChild(city_name);
        					
        					elt.appendChild(out);
        					
        				} else {
        					Element err = printError(results, "cityDoesNotExist", "deleteCity");
        					parSetter(results, err, "name", name);
        					res.appendChild(err);
        				}
        					
        				break;
        				*/
        			//Part2 Done
        			case "clearAll": {
        				ntc.clear();
        				ctn.clear();
        				pr_tree = new PM3QuadTree(c, width, height);
        				q.makeEmpty();
        				road_q.makeEmpty();
        				avl.clear();
        				citySet = new TreeSet<City>(); 
        				
        				Element suc = printSuccess(results, "clearAll", id);
    					suc.appendChild(results.createElement("output"));
    					res.appendChild(suc);
    					
        				break;
        			}
        			//Part2 Done
        			case "listCities":
        				String sortBy = commandNode.getAttribute("sortBy");
        				if (ntc.isEmpty()){
        					Element err = printError(results, "noCitiesToList", "listCities", id);
        					parSetter(results, err, "sortBy", sortBy);
        					res.appendChild(err);
        				} else {
        					Element suc = printSuccess(results, "listCities", id);
        					parSetter(results, suc, "sortBy", sortBy);
        					
        					Element output = results.createElement("output");
        					suc.appendChild(output);	        				
	    					
	    					Element cityList = results.createElement("cityList");
	    					output.appendChild(cityList);
	    					
	        				if (sortBy.equals("name")) {
	        					Iterator<Entry<String, City>> it = ntc.entrySet().iterator();
	        					while (it.hasNext()) {
	        						Map.Entry<String, City> pair = it.next();
	        						
	        						City curr = pair.getValue();
	        						Element city = results.createElement("city");
	        						city.setAttribute("name", curr.getName());
	        						city.setAttribute("x", (int)curr.getX()+"");
	        						city.setAttribute("y", (int)curr.getY()+"");
	        						city.setAttribute("radius", (int)curr.getRadius()+"");
	        						city.setAttribute("color", curr.getColor());
	        						cityList.appendChild(city);
	        					}
	        					
	        				} else if (sortBy.equals("coordinate")){
	        					Iterator<Entry<City, String>> it = ctn.entrySet().iterator();
	        					
	        					while(it.hasNext()) {
		        					Map.Entry<City, String> pair = it.next();

	        						City curr = pair.getKey();
	        						
	        						Element city = results.createElement("city");
	        						city.setAttribute("name", curr.getName());
	        						city.setAttribute("x", (int)curr.getX()+"");
	        						city.setAttribute("y", (int)curr.getY()+"");
	        						city.setAttribute("radius", (int)curr.getRadius()+"");
	        						city.setAttribute("color", curr.getColor());
	        						cityList.appendChild(city);
	        					}
	        				}
	        				
	        				res.appendChild(suc);
        				}
        				break;
        			//Part2 Done
        			case "printAvlTree":
        				if (avl.isEmpty()) {
        					Element err = printError(results, "emptyTree", "printAvlTree", id);
        					res.appendChild(err);
        				} else {
        					Element suc = printSuccess(results, "printAvlTree", id);        					
        					Element output = results.createElement("output");
        					suc.appendChild(output);	        				
	    					
	    					Element avlGTree = results.createElement("AvlGTree");
	    					avlGTree.setAttribute("cardinality", avl.getCardinality()+"");
	    					avlGTree.setAttribute("height", avl.getHeight()+"");
	    					avlGTree.setAttribute("maxImbalance", avl.getG()+"");
	    					
	    					avlGTree.appendChild(avl.printAvlGTree(results, avlGTree));
	    					
	    					output.appendChild(avlGTree);
	    					res.appendChild(suc);
        				}
        				break;
        			//Part2 Done
        			case "mapRoad": {
        				Element err = null;
        				City start_city = ntc.get(start);
        				City end_city = ntc.get(end);
        				LeafNode start_leaf = pr_tree.findCity(start);
        				
        				if (start_city == null) err = printError(results, "startPointDoesNotExist", "mapRoad", id);
        				else if (end_city == null) err = printError(results, "endPointDoesNotExist", "mapRoad", id);
        				else if (start.equals(end)) err = printError(results, "startEqualsEnd", "mapRoad", id);
        				else if (ntc.get(start).isIsolated() || ntc.get(end).isIsolated()) err = printError(results, "startOrEndIsIsolated", "mapRoad", id);
        				else {//not isolated and exists
        					int sx = start_city.getX();
        					int sy = start_city.getY();
        					int ex = end_city.getX();
        					int ey = end_city.getY();
        					int xmax = Math.max(sx, ex), xmin = Math.min(sx, ex), ymax = Math.max(sy, ey), ymin = Math.min(sy, ey);
        					
        					Line2D.Double l = new Line2D.Double(sx, sy, ex, ey);
        					if (start_leaf != null && 
        						(start_leaf.arr.contains(new Road(new String[]{start, end}, l)) ||
        						start_leaf.arr.contains(new Road(new String[]{end, start}, l))	)) {
        						err = printError(results, "roadAlreadyMapped", "mapRoad", id);
        					} else if (!(Line2D.linesIntersect(0, 0, 0, height, sx, sy, ex, ey) || 
        							   Line2D.linesIntersect(0, 0, width, 0, sx, sy, ex, ey) ||
        							   Line2D.linesIntersect(width, 0, width, height, sx, sy, ex, ey) ||
        							   Line2D.linesIntersect(0, height, width, height, sx, sy, ex, ey) ||
        							   (xmax <= width && xmin >= 0 && ymin >= 0 && ymax <= height)) 
        							) {
        						err = printError(results, "roadOutOfBounds", "mapRoad", id);
        					} else {
        						road_q.push(new Road(new String[]{start, end}, new Line2D.Double(sx,sy,ex,ey)));
        						//avl.insert(start, start_city);
        						//avl.insert(end, end_city);
        						if (start_city.getX() >= 0 && start_city.getX() <= width &&
        							start_city.getY() >= 0 && start_city.getY() <= height &&
        							citySet.add(start_city)) q.push(start_city);
        						if (end_city.getX() >= 0 && end_city.getX() <= width &&
        							end_city.getY() >= 0 && end_city.getY() <= height &&
        							citySet.add(end_city)) q.push(end_city);
        						pr_tree.insertRoad(start_city, end_city);
        						Element suc = printSuccess(results, "mapRoad", id);
        						parSetter(results, suc, "start", start);
            					parSetter(results, suc, "end", end);
            					
            					Element output = results.createElement("output");
            					Element roadCreated = results.createElement("roadCreated");
            					roadCreated.setAttribute("start", start);
            					roadCreated.setAttribute("end", end);
            					output.appendChild(roadCreated);
            					suc.appendChild(output);
            					res.appendChild(suc);
        					}
        				}
        				
        				if (err != null) {
        					parSetter(results, err, "start", start);
        					parSetter(results, err, "end", end);
        					res.appendChild(err);
        				}

        			}
        				break;
        			//Part2 Done
        			case "mapCity":
        				if (!ntc.containsKey(name)) {
        					Element err = printError(results, "nameNotInDictionary", "mapCity", id);
        					parSetter(results, err, "name", name);
        					res.appendChild(err);
        				} else {
        					City xiti = ntc.get(name);
        					//avl.insert(name, xiti);
        					if (citySet.contains(xiti)) {
        						Element err = printError(results, "cityAlreadyMapped", "mapCity", id);
            					parSetter(results, err, "name", name);
            					res.appendChild(err);
        						
        					} else {
        						if (xiti.getX() > width || xiti.getY() > height || xiti.getX() < 0 || xiti.getY() < 0) {
        							Element err = printError(results, "cityOutOfBounds", "mapCity", id);
                					parSetter(results, err, "name", name);
                					res.appendChild(err);
        							
        						} else {
        							citySet.add(xiti);
        							q.push(xiti);
        							
        							xiti.setIsolated(true);
        							pr_tree.insert(ntc.get(name));
        							
        							Element suc = printSuccess(results, "mapCity", id);
        	    					parSetter(results, suc, "name", name);
        	    					
        	    					Element output = results.createElement("output");
        	    					suc.appendChild(output);
        	    					
        							res.appendChild(suc);
        						}
        					}
        				}
        				
        				break;
        				
        			//Part2 Done
        			case "printPMQuadtree":
        				if (pr_tree.root.narr[1].isNull()) {
        					Element err = printError(results, "mapIsEmpty", "printPMQuadtree", id);
        					res.appendChild(err);
        				} else {
        					Element suc = printSuccess(results, "printPMQuadtree", id);
	    					
	    					Element output = results.createElement("output");
	    					suc.appendChild(output);
	    					
	    					Element quad = results.createElement("quadtree");
	    					quad.setAttribute("order", pmOrder+"");
	    					output.appendChild(quad);
	    					
	    					printQuad(pr_tree.root.narr[1], results, quad);
	    					
	    					res.appendChild(suc);
        				}
        				break;
        			//Part2 Done
        			case "saveMap": {   
        				c.draw();
        				Element suc = printSuccess(results, "saveMap", id);
    					res.appendChild(suc);
    					
        				parSetter(results, suc, "name", name);
        				
    					Element output = results.createElement("output");
    					suc.appendChild(output);
        			}
        				break;
        			//Part2 Done
        			case "rangeCities": {
        				RadiusComparator rd_comp = new RadiusComparator(Integer.parseInt(x),Integer.parseInt(y));
        				q.setComp(rd_comp);
        				q.createHeap();;	
        				PriorityQueue<City> temp = q.clone();
        				City temp_c = temp.pop();
        				if (temp_c == null || Math.pow((temp_c.getX()-Integer.parseInt(x)),2) + Math.pow((temp_c.getY() -Integer.parseInt(y)),2) > Math.pow((Integer.parseInt(radius)),2)) {
        					Element err = printError(results, "noCitiesExistInRange", "rangeCities", id);
        					parSetter(results, err, "x", x);
        					parSetter(results, err, "y", y);
        					parSetter(results, err, "radius", radius);
        					if (!savefile.equals("")) parSetter(results, err, "saveMap", savefile);

        					res.appendChild(err);
          				} else {
	        				Element suc = printSuccess(results, "rangeCities", id);
	    					res.appendChild(suc);
	    					
	    					parSetter(results, suc, "x", x);
	    					parSetter(results, suc, "y", y);
	    					parSetter(results, suc, "radius", radius);
			
	    					if (!savefile.equals("")) {
		    					parSetter(results, suc, "saveMap", savefile);
        					}
	    					
	    					Element output = results.createElement("output");
	    					suc.appendChild(output);
	    					
	    					Element cityList = results.createElement("cityList");
	    					output.appendChild(cityList);
	    					
	    					PriorityQueue<City> c_set = new PriorityQueue<City>(
	    							new Comparator<City>() {
										@Override
										public int compare(City o1, City o2) {
											return o1.getName().compareTo(o2.getName());
										}
	    							}
	    					);
	    					
	        				while (temp_c != null && (Math.pow((temp_c.getX()-Integer.parseInt(x)),2) + Math.pow((temp_c.getY() -Integer.parseInt(y)),2)) <= Math.pow(Integer.parseInt(radius),2)) {
	        					c_set.push(temp_c);
	        					temp_c = temp.pop();
	        				}
	        				
	        				while(!c_set.isEmpty()) {
	        					City temp_c_set = c_set.pop();
	        					Element city = results.createElement("city");
	        					city.setAttribute("name", temp_c_set.getName());
	        					city.setAttribute("x", temp_c_set.getX()+"");
	        					city.setAttribute("y", temp_c_set.getY()+"");
	        					city.setAttribute("color", temp_c_set.getColor());
	        					city.setAttribute("radius", temp_c_set.getRadius()+"");
	        					cityList.appendChild(city);
	        					
	        				}
          				}
        			}
        				break;
        			//Part2 Done
        			case "rangeRoads": {
        				Comparator<Road> road_comp = new RoadComparator(java.lang.Double.parseDouble(x), java.lang.Double.parseDouble(y));
        				road_q.setComp(road_comp);
        				if(road_q.getSize() == 1) {
        					Road change = road_q.peek();
        					Line2D.Double l = change.getLine();
        					change.setDist(Line2D.ptSegDist(l.getX1(), l.getY1(), l.getX2(), l.getY2(), java.lang.Double.parseDouble(x), java.lang.Double.parseDouble(y)));
        				}
        				road_q.createHeap();
        				PriorityQueue<Road> temp = road_q.clone();
        				Road temp_r = temp.pop();
        				
        				if (temp_r == null || temp_r.getDist() > java.lang.Double.parseDouble(radius)) {
        					Element err = printError(results, "noRoadsExistInRange", "rangeRoads", id);
        					parSetter(results, err, "x", x);
        					parSetter(results, err, "y", y);
        					parSetter(results, err, "radius", radius);
        					if (!savefile.equals("")) parSetter(results, err, "saveMap", savefile);

        					res.appendChild(err);
          				} else {
	        				Element suc = printSuccess(results, "rangeRoads", id);
	    					res.appendChild(suc);
	    					
	    					parSetter(results, suc, "x", x);
	    					parSetter(results, suc, "y", y);
	    					parSetter(results, suc, "radius", radius);
			
	    					if (!savefile.equals("")) {
		    					parSetter(results, suc, "saveMap", savefile);
        					}
	    					
	    					Element output = results.createElement("output");
	    					suc.appendChild(output);
	    					
	    					Element roadList = results.createElement("roadList");
	    					output.appendChild(roadList);
	    					
	    					Comparator<Road> compRoadByName = new Comparator<Road>() {
								@Override
								public int compare(Road r1, Road r2) {
									int com = r1.getStart().compareTo(r2.getStart());
									
									if (com != 0) return com;
									else return r1.getEnd().compareTo(r2.getEnd());
								}
	 	    				};
	 	    				
	    					PriorityQueue<Road> r_set = new PriorityQueue<Road>(compRoadByName);
	    					
	        				while (temp_r != null && temp_r.getDist() <= java.lang.Double.parseDouble(radius)) {
	        					checkNSwap(temp_r);
	        					r_set.push(temp_r);
	        					
	        					temp_r = temp.pop();
	        				}
	        				
	        				while(!r_set.isEmpty()) {
	        					Road r_set_temp = r_set.pop();
	        					Element road = results.createElement("road");
	        					road.setAttribute("start", r_set_temp.getStart());
	        					road.setAttribute("end", r_set_temp.getEnd());
	        					roadList.appendChild(road);
	        				}
          				}
        			}
        				break;
        			//Part2 Done
        			case "nearestCity": {
        				RadiusComparator rd_comp = new RadiusComparator(Integer.parseInt(x),Integer.parseInt(y));
        				q.setComp(rd_comp);
        				q.createHeap();
        				
        				PriorityQueue<City> temp = q.clone();
        				
        				if(citySet.isEmpty()) {
        					Element err = printError(results, "cityNotFound", "nearestCity", id);
        					parSetter(results, err, "x", x);
        					parSetter(results, err, "y", y);

        					res.appendChild(err);
        					     					
        				} else {
        					
	    					City near_city = null;
	    					
	    					while (!temp.isEmpty() && (near_city = temp.peek()).isIsolated()) {temp.pop();}
	    					
	    					if(temp.isEmpty()) {
	    						Element err = printError(results, "cityNotFound", "nearestCity", id);
	        					parSetter(results, err, "x", x);
	        					parSetter(results, err, "y", y);

	        					res.appendChild(err);
	    					} else {
	    						Element suc = printSuccess(results, "nearestCity", id);
		    					res.appendChild(suc);
		    					
		    					parSetter(results, suc, "x", x);
		    					parSetter(results, suc, "y", y);
				
		    					Element output = results.createElement("output");
		    					suc.appendChild(output);
		    					
		    					Element city = results.createElement("city");
		    					city.setAttribute("name", near_city.getName());
		    					city.setAttribute("x", near_city.getX() + "");
		    					city.setAttribute("y", near_city.getY() + "");
		    					city.setAttribute("color", near_city.getColor());
		    					city.setAttribute("radius", near_city.getRadius() + "");
	
		    					output.appendChild(city);
	    					}	    					
        				}
        			}
        				break;
        				
        			case "nearestIsolatedCity": {
        				RadiusComparator rd_comp = new RadiusComparator(Integer.parseInt(x),Integer.parseInt(y));
        				q.setComp(rd_comp);
        				q.createHeap();
        				
        				PriorityQueue<City> temp = q.clone();
        				
        				if(citySet.isEmpty()) {
        					Element err = printError(results, "cityNotFound", "nearestIsolatedCity", id);
        					parSetter(results, err, "x", x);
        					parSetter(results, err, "y", y);

        					res.appendChild(err);
        					     					
        				} else {
	    					City near_city = null;
	    					
	    					while (!temp.isEmpty() && !(near_city = temp.peek()).isIsolated()) {temp.pop();}
	    					
	    					if(temp.isEmpty()) {
	    						Element err = printError(results, "cityNotFound", "nearestIsolatedCity", id);
	        					parSetter(results, err, "x", x);
	        					parSetter(results, err, "y", y);

	        					res.appendChild(err);
	    					} else {
	    						Element suc = printSuccess(results, "nearestIsolatedCity", id);
		    					res.appendChild(suc);
		    					
		    					parSetter(results, suc, "x", x);
		    					parSetter(results, suc, "y", y);
				
		    					Element output = results.createElement("output");
		    					suc.appendChild(output);
		    					
		    					Element city = results.createElement("isolatedCity");
		    					city.setAttribute("name", near_city.getName());
		    					city.setAttribute("x", near_city.getX() + "");
		    					city.setAttribute("y", near_city.getY() + "");
		    					city.setAttribute("color", near_city.getColor());
		    					city.setAttribute("radius", near_city.getRadius() + "");
	
		    					output.appendChild(city);
	    					}	    					
        				}
        			}
        				break;
        			//Part2 Done
        			case "nearestRoad":
        				if (road_q.isEmpty()) {
        					Element err = printError(results, "roadNotFound", "nearestRoad", id);
        					parSetter(results, err, "x", x);
        					parSetter(results, err, "y", y);

        					res.appendChild(err);
        				} else {
        					Comparator<Road> road_comp = new RoadComparator(java.lang.Double.parseDouble(x), java.lang.Double.parseDouble(y));
            				road_q.setComp(road_comp);
            				road_q.createHeap();
            				
            				Road rd = road_q.peek();
            				
            				Element suc = printSuccess(results, "nearestRoad", id);
	    					res.appendChild(suc);
	    					
	    					parSetter(results, suc, "x", x);
	    					parSetter(results, suc, "y", y);
			
	    					Element output = results.createElement("output");
	    					suc.appendChild(output);
	    					
	    					String rd_start = rd.getStart(), rd_end = rd.getEnd();
	    					if (rd_start.compareTo(rd_end) > 0) {
	    						String temp = rd_start; 
	    						rd_start = rd_end; 
	    						rd_end = temp;
	    					}
	    					Element road = results.createElement("road");
	    					road.setAttribute("start", rd_start);
	    					road.setAttribute("end", rd_end);
	    					output.appendChild(road);
        				}
        				break;
        			case "nearestCityToRoad": {
        				Road r = road_q.getByName(start, end);
        				
        				if (r == null) {
        					Element err = printError(results, "roadIsNotMapped", "nearestCityToRoad", id);
        					parSetter(results, err, "start", start);
        					parSetter(results, err, "end", end);
        					res.appendChild(err);
        				} else {
	        				Line2D.Double l = r.getLine();
	        				double dist = Math.max(height, width);
	        				City xiti = null;
	        				
	        				for (City o : citySet) {
	        					if (!o.getName().equals(start) && !o.getName().equals(end)) {
		        					double segDist = Line2D.ptSegDist(l.getX1(), l.getY1(), l.getX2(), l.getY2(), o.getX(), o.getY());
	        						
		        					if (xiti == null) xiti = o;

		        					if (segDist < dist) {
		        						dist = segDist;
		        						xiti = o;
		        					} else if (segDist == dist) {
		        						if (xiti.getName().compareTo(o.getName()) > 0) xiti = o;
		        					}
	        					}
	        				}
	        				
	        				if (xiti == null) {
	        					Element err = printError(results, "noOtherCitiesMapped", "nearestCityToRoad", id);
	        					parSetter(results, err, "start", start);
	        					parSetter(results, err, "end", end);
	        					res.appendChild(err);
	        				} else {
	        					Element suc = printSuccess(results, "nearestCityToRoad", id);
		    					res.appendChild(suc);
		    					
		    					parSetter(results, suc, "start", start);
		    					parSetter(results, suc, "end", end);
				
		    					Element output = results.createElement("output");
		    					suc.appendChild(output);
		    					
		    					Element road = results.createElement("city");
		    					road.setAttribute("name", xiti.getName());
		    					road.setAttribute("x", xiti.getX()+"");
		    					road.setAttribute("y", xiti.getY()+"");
		    					road.setAttribute("color", xiti.getColor());
		    					road.setAttribute("radius", xiti.getRadius()+"");

		    					output.appendChild(road);
	        				}
        				}
        			}
        				break;
        			case "shortestPath": {
    					String saveHTMLName = commandNode.getAttribute("saveHTML");
        				
        				if (pr_tree.findCity(start) == null) {
        					Element err = printError(results, "nonExistentStart", "shortestPath", id);
        					parSetter(results, err, "start", start);
        					parSetter(results, err, "end", end);
        					if (!savefile.equals("")) parSetter(results, err, "saveMap", savefile);
	    					if (!saveHTMLName.equals("")) parSetter(results, err, "saveHTML", saveHTMLName);

        					res.appendChild(err);
        				} else if (pr_tree.findCity(end) == null) {
        					Element err = printError(results, "nonExistentEnd", "shortestPath", id);
        					parSetter(results, err, "start", start);
        					parSetter(results, err, "end", end);
        					if (!savefile.equals("")) parSetter(results, err, "saveMap", savefile);
        					if (!saveHTMLName.equals("")) parSetter(results, err, "saveHTML", saveHTMLName);
        					res.appendChild(err);
        				} else {
	        				
	        				Entry<String, Stack<City>> e = pr_tree.shortestPath(start, end);
            				Stack<City> s = e.getValue();

	        				if (s == null) {
	        					Element err = printError(results, "noPathExists", "shortestPath", id);
	        					parSetter(results, err, "start", start);
	        					parSetter(results, err, "end", end);
	        					if (!savefile.equals("")) parSetter(results, err, "saveMap", savefile);
	        					if (!saveHTMLName.equals("")) parSetter(results, err, "saveHTML", saveHTMLName);
	        					res.appendChild(err);
	        				} else {
	
	        					Element suc = printSuccess(results, "shortestPath", id);
		    					res.appendChild(suc);
		    					
		    					parSetter(results, suc, "start", start);
		    					parSetter(results, suc, "end", end);
				
		    					Element output = results.createElement("output");
		    					suc.appendChild(output);
		    					Element path = results.createElement("path");
		    					path.setAttribute("hops", (s.size()-1)+"");
		    					path.setAttribute("length", e.getKey());
		    					output.appendChild(path);
		    							    					
		    					if(!savefile.equals("")) {
		    						c.draw();
		    						parSetter(results, suc, "saveMap", savefile);
		    						c.save(savefile);
		    					}
		    					
		    					if (s.size() > 1) {
		    						City path_pre = s.pop();
			    					City path_post = s.pop();
			    					Element road = results.createElement("road");
		    						road.setAttribute("start", path_pre.getName());
		    						road.setAttribute("end", path_post.getName());
		    						path.appendChild(road);
		    						
			    					Point p1 = new Point(path_pre.getX(), path_pre.getY());
			    					Point p2 = new Point(path_post.getX(), path_post.getY());
		
			    					while(!s.isEmpty()) {		
			    						path_pre = path_post;
			    						path_post = s.pop();
			    						Point p3 = new Point(path_post.getX(), path_post.getY());
			    						
			    						Arc2D.Double a = new Arc2D.Double();
			    						a.setArcByTangent(p1, p2, p3, 1);
			    						
			    						String dir = null;
			    						double a_digit = a.getAngleExtent();
			    						
			    						if (a_digit >= -45 && a_digit < 45) dir = "straight";
			    						else if (a_digit < -45 && a_digit > -180) dir = "left";
			    						else if (a_digit >= 45 && a_digit < 180) dir = "right";
			    						
			    						path.appendChild(results.createElement(dir));
			    						
			    						road = results.createElement("road");
			    						road.setAttribute("start", path_pre.getName());
			    						road.setAttribute("end", path_post.getName());
			    						path.appendChild(road);
			    						p1 = p2;
			    						p2 = p3;
			    					}
		    					}
		    					
		    					if (!saveHTMLName.equals("")) {
		    						parSetter(results, suc, "saveHTML", saveHTMLName);
		    						c.save(saveHTMLName);
			    					org.w3c.dom.Node successNode = suc.cloneNode(true);
			    					org.w3c.dom.Document shortestPathDoc = XmlUtility.getDocumentBuilder().newDocument();
			    					org.w3c.dom.Node spNode = shortestPathDoc.importNode(successNode, true);
			    					shortestPathDoc.appendChild(spNode);
			    					try {
										XmlUtility.transform(shortestPathDoc, new File("shortestPath.xsl"), new File(saveHTMLName + ".html"));
									} catch (TransformerException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
		    					}
        				}
	    					
	    					
        				}
        			}
        				break;
        				
        			/*Obsolete for part2
        			case "unmapCity":
        				if (!ntc.containsKey(name)) {
        					Element err = printError(results, "nameNotInDictionary", "unmapCity");
        					parSetter(results, err, "name", name);
        					res.appendChild(err);
        					
        				} else if (!nameSet.contains(name)){
        					Element err = printError(results, "cityNotMapped", "unmapCity");
        					parSetter(results, err, "name", name);
        					res.appendChild(err);
        					
        				} else {
        					City s = ntc.get(name);
        					pr_tree.delete(s);
        					q.delete(s);
        					nameSet.remove(name);
        					
        					Element elt = results.createElement("success");
	    					res.appendChild(elt);
	    					
	    					Element cmd = results.createElement("command");
	    					cmd.setAttribute("name", "unmapCity");
	    					elt.appendChild(cmd);
	    					
	    					Element par = results.createElement("parameters");
	    					elt.appendChild(par);
	    					
	    					Element sort = results.createElement("name");
	    					sort.setAttribute("value", name);
	    					par.appendChild(sort);
	    					
	    					Element output = results.createElement("output");
	    					elt.appendChild(output);
        				}
        				break;   
        				*/     				
        			default:
        				break;
        			}
        			/* TODO: Process your commandNode here */
        		}
        	}
        	c.dispose();
        } catch (SAXException | IOException | ParserConfigurationException e) {
        	try {
				results = XmlUtility.getDocumentBuilder().newDocument();
				results.appendChild(results.createElement("fatalError"));
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			}        	
		} finally {
            try {
				XmlUtility.print(results);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
            
        }
    }
}
