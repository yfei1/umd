package cmsc420.meeshquest.part1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

public class MeeshQuest {

	private static void printQuad(Node n, Document d, Element quad) {
		if (n == null) {
			Element white = d.createElement("white");
			quad.appendChild(white);
		} else if (n.isLeaf()) {
			City c = ((LNode)n).c;
			Element black = d.createElement("black");
			black.setAttribute("name" ,c.name);
			black.setAttribute("x", c.x+"");
			black.setAttribute("y", c.y+"");
			quad.appendChild(black);
		} else {
			PNode p = (PNode)n;
			Element gray = d.createElement("gray");
			gray.setAttribute("x", p.getX()+"");
			gray.setAttribute("y", p.getY()+"");
			quad.appendChild(gray);
			
			printQuad(p.NW, d, gray);
			printQuad(p.NE, d, gray);
			printQuad(p.SW, d, gray);
			printQuad(p.SE, d, gray);

		}
	}
	
	private static void parSetter(Document res, Element err, String par_name, String par_val) {
		Element par = (Element)err.getElementsByTagName("parameters").item(0);
		
		Element param = res.createElement(par_name);
		param.setAttribute("value", par_val);
		
		par.appendChild(param);
	}
	
	public static Element printError(Document res, String err_type, String err_cmd) {
		Element err = res.createElement("error");
		err.setAttribute("type", err_type);
		
		Element cmd = res.createElement("command");
		cmd.setAttribute("name", err_cmd);
		
		Element par = res.createElement("parameters");
		
		err.appendChild(cmd);
		err.appendChild(par);
		
		return err;
	}
	
    public static void main(String[] args) {
    	
    	Document results = null;
    	
        try {
        	//Redirect StdIOStream
        	//System.setIn(new FileInputStream("part1.createCity1.input.xml"));
        	//System.setOut(new PrintStream(new File("test.xml")));
        	
        	//Create two maps: nameToCity and cityToName
        	CityCoordinateComparator comp = new CityCoordinateComparator();
        	TreeMap<String, City> ntc = new TreeMap<String, City>(new Comparator<String>() {
        		public int compare(String o1, String o2) {
					return -o1.compareTo(o2);
        		}
        	});
        	@SuppressWarnings("unchecked")
			TreeMap<City, String> ctn = new TreeMap<City, String>(comp);
        	AvlTree avl = new AvlTree();
        	
        	Document doc = XmlUtility.validateNoNamespace(System.in);
        	//Document doc = XmlUtility.parse(new File("actDelete.in.xml"));
        	results = XmlUtility.getDocumentBuilder().newDocument();
        	Element res = results.createElement("results");
			results.appendChild(res);

        	Element commandNode = doc.getDocumentElement();
        	
        	//Initialize the PR QuadTree
        	int width = Integer.parseInt(commandNode.getAttribute("spatialWidth"));
        	int height = Integer.parseInt(commandNode.getAttribute("spatialHeight"));
        	
    		CanvasPlus c = new CanvasPlus("MeeshQuest",width,height);
        	QuadTree pr_tree = new QuadTree(c, width, height);
        	Set<String> nameSet = new TreeSet<String>();
			PriorityQueue q = new PriorityQueue(10, new RadiusComparator(0,0));
			
        	
        	final NodeList nl = commandNode.getChildNodes();
        	for (int i = 0; i < nl.getLength(); i++) {
        		if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
        			commandNode = (Element) nl.item(i);
        			
        			String name = commandNode.getAttribute("name");
    				String x = commandNode.getAttribute("x");
    				String y = commandNode.getAttribute("y");
    				String radius = commandNode.getAttribute("radius");
    				String color = commandNode.getAttribute("color");
    				String savefile = commandNode.getAttribute("saveMap");
    				
        			switch(commandNode.getNodeName()){
        			
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
        					if (((City)e.getKey()).x == curr.x && ((City)e.getKey()).y == curr.y) {
            					Element err = printError(results, "duplicateCityCoordinates", "createCity");
            					parSetter(results, err, "name", curr.name);
            					parSetter(results, err, "x", curr.x+"");
            					parSetter(results, err, "y", curr.y+"");
            					parSetter(results, err, "radius", curr.radius+"");
            					parSetter(results, err, "color", curr.color);
            					res.appendChild(err);
            					dupCoordinates = true;
        					}
        				}
        				
        				if (!dupCoordinates) {
	        				if (ntc.containsKey(name)) {
	        					Element err = printError(results, "duplicateCityName", "createCity");
	        					parSetter(results, err, "name", curr.name);
	        					parSetter(results, err, "x", curr.x+"");
	        					parSetter(results, err, "y", curr.y+"");
	        					parSetter(results, err, "radius", curr.radius+"");
	        					parSetter(results, err, "color", curr.color);
	        					res.appendChild(err);
	        				}else  {
	        					ntc.put(name, curr);
	        					ctn.put(curr, name);
	        					
	        					Element elt = results.createElement("success");
	        					res.appendChild(elt);
	        					
	        					Element cmd = results.createElement("command");
	        					cmd.setAttribute("name", "createCity");
	        					elt.appendChild(cmd);
	        					
	        					Element par = results.createElement("parameters");
	        					elt.appendChild(par);
	        					
	        					Element city_name = results.createElement("name");
	        					city_name.setAttribute("value", curr.name);
	        					par.appendChild(city_name);
	        					
	        					Element city_x = results.createElement("x");
	        					city_x.setAttribute("value", curr.x+"");
	        					par.appendChild(city_x);
	        					
	        					Element city_y = results.createElement("y");
	        					city_y.setAttribute("value", curr.y+"");
	        					par.appendChild(city_y);
	        					
	        					Element city_rad = results.createElement("radius");
	        					city_rad.setAttribute("value", curr.radius+"");
	        					par.appendChild(city_rad);
	        					
	        					Element city_color = results.createElement("color");
	        					city_color.setAttribute("value", curr.color);
	        					par.appendChild(city_color);
	        					
	        					elt.appendChild(results.createElement("output"));
	        				}
        				}
        					break;
        			}
        			case "deleteCity":
        				if (ntc.containsKey(name)) {
        					City temp = ntc.get(name);
        					
        					Element out = results.createElement("output");
        					
        					if (nameSet.contains(name)) {
        						pr_tree.delete(temp);
        						q.delete(temp);
        						Element cityUnmapped = results.createElement("cityUnmapped");
        						cityUnmapped.setAttribute("name", temp.name);
        						cityUnmapped.setAttribute("x", temp.x+"");
        						cityUnmapped.setAttribute("y", temp.y+"");
        						cityUnmapped.setAttribute("radius", temp.radius+"");
        						cityUnmapped.setAttribute("color", temp.color);
        						out.appendChild(cityUnmapped);
        					}
        					ntc.remove(name);
        					ctn.remove(temp);
        					
        					Element elt = results.createElement("success");
        					res.appendChild(elt);
        					
        					Element cmd = results.createElement("command");
        					cmd.setAttribute("name", "deleteCity");
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
        			case "clearAll": {
        				ntc.clear();
        				ctn.clear();
        				pr_tree = new QuadTree(c, width, height);
        				q.makeEmpty();
        				avl.makeEmpty();
        				nameSet = new TreeSet<String>(); 
        				
        				Element elt = results.createElement("success");
    					res.appendChild(elt);
    					
    					Element cmd = results.createElement("command");
    					cmd.setAttribute("name", "clearAll");
    					elt.appendChild(cmd);
    					
    					Element par = results.createElement("parameters");
    					elt.appendChild(par);
    					
    					elt.appendChild(results.createElement("output"));
        				break;
        			}
        			case "listCities":
        				String sortBy = commandNode.getAttribute("sortBy");
        				if (ntc.isEmpty()){
        					Element err = printError(results, "noCitiesToList", "listCities");
        					parSetter(results, err, "sortBy", sortBy);
        					res.appendChild(err);
        				} else {
	        					        				
	        				Element elt = results.createElement("success");
	    					res.appendChild(elt);
	    					
	    					Element cmd = results.createElement("command");
	    					cmd.setAttribute("name", "listCities");
	    					elt.appendChild(cmd);
	    					
	    					Element par = results.createElement("parameters");
	    					elt.appendChild(par);
	    					
	    					Element sort = results.createElement("sortBy");
	    					sort.setAttribute("value", sortBy);
	    					par.appendChild(sort);
	    					
	    					Element output = results.createElement("output");
	    					elt.appendChild(output);
	    					
	    					Element cityList = results.createElement("cityList");
	    					output.appendChild(cityList);
	    					
	        				if (sortBy.equals("name")) {
	        					Iterator<Entry<String, City>> it = ntc.entrySet().iterator();
	        					while (it.hasNext()) {
	        						Map.Entry<String, City> pair = it.next();
	        						
	        						City curr = pair.getValue();
	        						Element city = results.createElement("city");
	        						city.setAttribute("name", curr.name);
	        						city.setAttribute("x", curr.x+"");
	        						city.setAttribute("y", curr.y+"");
	        						city.setAttribute("radius", curr.radius+"");
	        						city.setAttribute("color", curr.color);
	        						cityList.appendChild(city);
	        					
	        					}
	        					
	        				} else if (sortBy.equals("coordinate")){
	        					Iterator<Entry<City, String>> it = ctn.entrySet().iterator();
	        					
	        					while(it.hasNext()) {
		        					Map.Entry<City, String> pair = it.next();

	        						City curr = pair.getKey();
	        						
	        						Element city = results.createElement("city");
	        						city.setAttribute("name", curr.name);
	        						city.setAttribute("x", curr.x+"");
	        						city.setAttribute("y", curr.y+"");
	        						city.setAttribute("radius", curr.radius+"");
	        						city.setAttribute("color", curr.color);
	        						cityList.appendChild(city);
	        					}
	        				}
        				}
        				break;
        			case "mapCity":
        				if (!ntc.containsKey(name)) {
        					Element err = printError(results, "nameNotInDictionary", "mapCity");
        					parSetter(results, err, "name", name);
        					res.appendChild(err);
        				} else {
        					avl.insert(ntc.get(name));
        					if (nameSet.contains(name)) {
        						Element err = printError(results, "cityAlreadyMapped", "mapCity");
            					parSetter(results, err, "name", name);
            					res.appendChild(err);
        						
        					} else {
        						if (ntc.get(name).x >=width || ntc.get(name).y >= height) {
        							Element err = printError(results, "cityOutOfBounds", "mapCity");
                					parSetter(results, err, "name", name);
                					res.appendChild(err);
        							
        						} else {
        							nameSet.add(name);
        							q.push(ntc.get(name));
        							
        							pr_tree.insert(ntc.get(name));
        							
        							Element elt = results.createElement("success");
        	    					res.appendChild(elt);
        	    					
        	    					Element cmd = results.createElement("command");
        	    					cmd.setAttribute("name", "mapCity");
        	    					elt.appendChild(cmd);
        	    					
        	    					Element par = results.createElement("parameters");
        	    					elt.appendChild(par);
        	    					
        	    					Element sort = results.createElement("name");
        	    					sort.setAttribute("value", name);
        	    					par.appendChild(sort);
        	    					
        	    					Element output = results.createElement("output");
        	    					elt.appendChild(output);
        	    					
        						}
        					}
        				}
        				
        				break;
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
        			case "printPRQuadtree":
        				if (pr_tree.root.NE == null) {
        					Element err = printError(results, "mapIsEmpty", "printPRQuadtree");
        					res.appendChild(err);
        				} else {
        					Element elt = results.createElement("success");
	    					res.appendChild(elt);
	    					
	    					Element cmd = results.createElement("command");
	    					cmd.setAttribute("name", "printPRQuadtree");
	    					elt.appendChild(cmd);
	    					
	    					Element par = results.createElement("parameters");
	    					elt.appendChild(par);
	    					
	    					Element output = results.createElement("output");
	    					elt.appendChild(output);
	    					
	    					Element quad = results.createElement("quadtree");
	    					output.appendChild(quad);
	    					
	    					printQuad(pr_tree.root.NE, results, quad);
        				}
        				break;
        			case "saveMap": {   
        				c.draw();
        				Element elt = results.createElement("success");
    					res.appendChild(elt);
    					
    					Element cmd = results.createElement("command");
    					cmd.setAttribute("name", "saveMap");
    					elt.appendChild(cmd);
    					
    					Element par = results.createElement("parameters");
    					elt.appendChild(par);
    					
    					Element par_name = results.createElement("name");
    					par_name.setAttribute("value", name);
    					par.appendChild(par_name);
    					
    					Element output = results.createElement("output");
    					elt.appendChild(output);
        			}
        				break;
        			case "rangeCities": {
        				RadiusComparator rd_comp = new RadiusComparator(Integer.parseInt(x),Integer.parseInt(y));
        				q.setComp(rd_comp);
        				q.sort();
        				PriorityQueue temp = q.clone();
        				City temp_c = temp.pop();
        				if (temp_c == null || Math.pow((temp_c.x-Integer.parseInt(x)),2) + Math.pow((temp_c.y -Integer.parseInt(y)),2) > Math.pow((Integer.parseInt(radius)),2)) {
        					Element err = printError(results, "noCitiesExistInRange", "rangeCities");
        					parSetter(results, err, "x", x);
        					parSetter(results, err, "y", y);
        					parSetter(results, err, "radius", radius);
        					if (!savefile.equals("")) parSetter(results, err, "saveMap", savefile);

        					res.appendChild(err);
        					
          				} else {
	        				Element elt = results.createElement("success");
	    					res.appendChild(elt);
	    					
	    					Element cmd = results.createElement("command");
	    					cmd.setAttribute("name", "rangeCities");
	    					elt.appendChild(cmd);
	    					
	    					Element par = results.createElement("parameters");
	    					elt.appendChild(par);
	    					
	    					Element par_name1 = results.createElement("x");
	    					par_name1.setAttribute("value", x);
	    					par.appendChild(par_name1);
	    					
	    					Element par_name2 = results.createElement("y");
	    					par_name2.setAttribute("value", y);
	    					par.appendChild(par_name2);
	    					
	    					Element par_name3 = results.createElement("radius");
	    					par_name3.setAttribute("value", radius);
	    					par.appendChild(par_name3);
	    					
	    					if (!savefile.equals("")) {
        						Element efile = results.createElement("saveMap");
        						efile.setAttribute("value", savefile);
        						par.appendChild(efile);
        					}
	    					
	    					Element output = results.createElement("output");
	    					elt.appendChild(output);
	    					
	    					Element cityList = results.createElement("cityList");
	    					output.appendChild(cityList);
	    					
	        				while (temp_c != null && (Math.pow((temp_c.x-Integer.parseInt(x)),2) + Math.pow((temp_c.y -Integer.parseInt(y)),2)) <= Math.pow(Integer.parseInt(radius),2)) {
	        					Element city = results.createElement("city");
	        					city.setAttribute("name", temp_c.name);
	        					city.setAttribute("x", temp_c.x+"");
	        					city.setAttribute("y", temp_c.y+"");
	        					city.setAttribute("color", temp_c.color);
	        					city.setAttribute("radius", temp_c.radius+"");
	        					cityList.appendChild(city);
	        					temp_c = temp.pop();
	        				}
          				}
        			}
        				break;
        			case "nearestCity":
        				RadiusComparator rd_comp = new RadiusComparator(Integer.parseInt(x),Integer.parseInt(y));
        				q.setComp(rd_comp);
        				q.sort();
        				
        				if(nameSet.isEmpty()) {
        					Element err = printError(results, "mapIsEmpty", "nearestCity");
        					parSetter(results, err, "x", x);
        					parSetter(results, err, "y", y);

        					res.appendChild(err);
        					     					
        				} else {
        					Element elt = results.createElement("success");
	    					res.appendChild(elt);
	    					
	    					Element cmd = results.createElement("command");
	    					cmd.setAttribute("name", "nearestCity");
	    					elt.appendChild(cmd);
	    					
	    					Element par = results.createElement("parameters");
	    					elt.appendChild(par);
	    					
	    					Element par_name1 = results.createElement("x");
	    					par_name1.setAttribute("value", x);
	    					par.appendChild(par_name1);
	    					
	    					Element par_name2 = results.createElement("y");
	    					par_name2.setAttribute("value", y);
	    					par.appendChild(par_name2);
	    						
	    					Element output = results.createElement("output");
	    					elt.appendChild(output);
	    					
	    					Element city = results.createElement("city");
	    					City near_city = q.peek();
	    					city.setAttribute("name", near_city.name);
	    					city.setAttribute("x", near_city.x + "");
	    					city.setAttribute("y", near_city.y + "");
	    					city.setAttribute("color", near_city.color);
	    					city.setAttribute("radius", near_city.radius + "");

	    					output.appendChild(city);
	    					
        				}
        				
        				break;
        			case "printAvlTree":
        				if (avl.isEmpty()) {
        					Element err = printError(results, "emptyTree", "printAvlTree");
        					res.appendChild(err);
        				} else {
        					Element elt = results.createElement("success");
	    					res.appendChild(elt);
	    					
	    					Element cmd = results.createElement("command");
	    					cmd.setAttribute("name", "printAvlTree");
	    					elt.appendChild(cmd);
	    					
	    					Element par = results.createElement("parameters");
	    					elt.appendChild(par);
	    							
	    					Element output = results.createElement("output");
	    					elt.appendChild(output);
	    					
	    					Element avlGTree = results.createElement("AvlGTree");
	    					avlGTree.setAttribute("cardinality", avl.cardinality+"");
	    					avlGTree.setAttribute("height", avl.getHeight()+"");
	    					avlGTree.setAttribute("maxImbalance", "1");
	    					
	    					avlGTree.appendChild(avl.printAvlGTree(results, avlGTree));
	    					
	    					output.appendChild(avlGTree);
        				}
        				break;
        			default:
        				break;
        			}
        			/* TODO: Process your commandNode here */
        		}
        	}
        } catch (SAXException | IOException | ParserConfigurationException e) {
        	try {
				results = XmlUtility.getDocumentBuilder().newDocument();
				results.appendChild(results.createElement("fatalError"));
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
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
