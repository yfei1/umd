package cmsc420.command;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.drawing.CanvasPlus;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.geometry.Airport;
import cmsc420.geometry.City;
import cmsc420.geometry.Geometry;
import cmsc420.geometry.Metropole;
import cmsc420.geometry.PointNameComparator;
import cmsc420.geometry.PointWise;
import cmsc420.geometry.PointWiseLocationComparator;
import cmsc420.geometry.Road;
import cmsc420.geometry.RoadNameComparator;
import cmsc420.geometry.Terminal;
import cmsc420.pmquadtree.InvalidIsolatedCityThrowable;
import cmsc420.pmquadtree.InvalidPartitionThrowable;
import cmsc420.pmquadtree.PMQuadtree;
import cmsc420.pmquadtree.PMQuadtree.Black;
import cmsc420.pmquadtree.PMQuadtree.Gray;
import cmsc420.pmquadtree.PMQuadtree.Node;
import cmsc420.pmquadtree.QuadTree;
import cmsc420.pmquadtree.RoadIntersectAnotherRoadThrowable;
import cmsc420.sortedmap.GuardedAvlGTree;

/**
 * Processes each command in the MeeshQuest program. Takes in an XML command
 * node, processes the node, and outputs the results.
 */
public class Command {
	/** output DOM Document tree */
	protected Document results;

	/** root node of results document */
	protected Element resultsNode;

	
	/** stores metropoles created*/
	TreeSet<Metropole> metropoleList;
	
	/**
	 * stores created cities sorted by their names (used with listCities
	 * command)
	 */
	protected GuardedAvlGTree<String, City> citiesByName;

	/** stores all of the airports and terminals currently being mapped */
	protected TreeMap<String, PointWise> pointsByName;
	
	/**
	 * stores created cities sorted by their locations (used with listCities
	 * command)
	 */
	protected final TreeSet<PointWise> pointsByLocation = new TreeSet<PointWise>(
			new PointWiseLocationComparator());

	//private final RoadAdjacencyList roads = new RoadAdjacencyList();

	private final TreeMap<Airport, TreeSet<Terminal>> airportToTerminal = new TreeMap<Airport, TreeSet<Terminal>>();
	/*
	    /** stores mapped cities in a spatial data structure 
	protected PMQuadtree pmQuadtree;
	*/
	
	/** PR Quadtree used as the metropole map*/
	protected QuadTree quadtree;
	
	/** order of the PM Quadtree */
	protected int pmOrder;

	
	protected int remoteSpatialWidth;
	protected int remoteSpatialHeight;
	
	/** spatial width of the PM Quadtree */
	protected int localSpatialWidth;

	/** spatial height of the PM Quadtree */
	protected int localSpatialHeight;

	public static class Err{
		public boolean intersect = false;
		public boolean pmrule = false;
	}
	
	/**
	 * Set the DOM Document tree to send the results of processed commands to.
	 * Creates the root results node.
	 * 
	 * @param results
	 *            DOM Document tree
	 */
	public void setResults(Document results) {
		this.results = results;
		resultsNode = results.createElement("results");
		results.appendChild(resultsNode);
	}

	/**
	 * Creates a command result element. Initializes the command name.
	 * 
	 * @param node
	 *            the command node to be processed
	 * @return the results node for the command
	 */
	private Element getCommandNode(final Element node) {
		final Element commandNode = results.createElement("command");
		commandNode.setAttribute("name", node.getNodeName());
		
		if (node.hasAttribute("id")) {
		    commandNode.setAttribute("id", node.getAttribute("id"));
		}
		return commandNode;
	}

	/**
	 * Processes an integer attribute for a command. Appends the parameter to
	 * the parameters node of the results. Should not throw a number format
	 * exception if the attribute has been defined to be an integer in the
	 * schema and the XML has been validated beforehand.
	 * 
	 * @param commandNode
	 *            node containing information about the command
	 * @param attributeName
	 *            integer attribute to be processed
	 * @param parametersNode
	 *            node to append parameter information to
	 * @return integer attribute value
	 */
	private int processIntegerAttribute(final Element commandNode,
			final String attributeName, final Element parametersNode) {
		final String value = commandNode.getAttribute(attributeName);

		if (parametersNode != null) {
			/* add the parameters to results */
			final Element attributeNode = results.createElement(attributeName);
			attributeNode.setAttribute("value", value);
			parametersNode.appendChild(attributeNode);
		}

		/* return the integer value */
		return Integer.parseInt(value);
	}

	/**
	 * Processes a string attribute for a command. Appends the parameter to the
	 * parameters node of the results.
	 * 
	 * @param commandNode
	 *            node containing information about the command
	 * @param attributeName
	 *            string attribute to be processed
	 * @param parametersNode
	 *            node to append parameter information to
	 * @return string attribute value
	 */
	private String processStringAttribute(final Element commandNode,
			final String attributeName, final Element parametersNode) {
		final String value = commandNode.getAttribute(attributeName);

		if (parametersNode != null) {
			/* add parameters to results */
			final Element attributeNode = results.createElement(attributeName);
			attributeNode.setAttribute("value", value);
			parametersNode.appendChild(attributeNode);
		}

		/* return the string value */
		return value;
	}

	/**
	 * Reports that the requested command could not be performed because of an
	 * error. Appends information about the error to the results.
	 * 
	 * @param type
	 *            type of error that occurred
	 * @param command
	 *            command node being processed
	 * @param parameters
	 *            parameters of command
	 */
	private void addErrorNode(final String type, final Element command,
			final Element parameters) {
		final Element error = results.createElement("error");
		error.setAttribute("type", type);
		error.appendChild(command);
		error.appendChild(parameters);
		resultsNode.appendChild(error);
	}

	/**
	 * Reports that a command was successfully performed. Appends the report to
	 * the results.
	 * 
	 * @param command
	 *            command not being processed
	 * @param parameters
	 *            parameters used by the command
	 * @param output
	 *            any details to be reported about the command processed
	 */
	private Element addSuccessNode(final Element command,
			final Element parameters, final Element output) {
		final Element success = results.createElement("success");
		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);
		resultsNode.appendChild(success);
		return success;
	}

	/**
	 * Processes the commands node (root of all commands). Gets the spatial
	 * width and height of the map and send the data to the appropriate data
	 * structures.
	 * 
	 * @param node
	 *            commands node to be processed
	 */
	public void processCommands(final Element node) {
		remoteSpatialWidth = Integer.parseInt(node.getAttribute("remoteSpatialWidth"));
		remoteSpatialHeight = Integer.parseInt(node.getAttribute("remoteSpatialHeight"));

		localSpatialWidth = Integer.parseInt(node.getAttribute("localSpatialWidth"));
		localSpatialHeight = Integer.parseInt(node.getAttribute("localSpatialHeight"));
		pmOrder = Integer.parseInt(node.getAttribute("pmOrder"));
		
		if (pmOrder == 3) {
			quadtree = new QuadTree(remoteSpatialWidth, remoteSpatialHeight, localSpatialWidth, localSpatialHeight, pmOrder);
		} else quadtree = new QuadTree(remoteSpatialWidth, remoteSpatialHeight, localSpatialWidth, localSpatialHeight, pmOrder);

		citiesByName = new GuardedAvlGTree<String, City>(new Comparator<String>() {			
    		@Override
    		public int compare(String o1, String o2) {
    			return o2.compareTo(o1);
    		}
    		
    	},
                Integer.parseInt(node.getAttribute("g")));
		
		pointsByName = new TreeMap<String, PointWise>();
		metropoleList = new TreeSet<Metropole>();

	}

	/**
	 * Processes a createCity command. Creates a city in the dictionary (Note:
	 * does not map the city). An error occurs if a city with that name or
	 * location is already in the dictionary.
	 * 
	 * @param node
	 *            createCity node to be processed
	 */
	public void processCreateCity(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final String name = processStringAttribute(node, "name", parametersNode);
		final int localX = processIntegerAttribute(node, "localX", parametersNode);
		final int localY = processIntegerAttribute(node, "localY", parametersNode);

		final int remoteX = processIntegerAttribute(node, "remoteX", parametersNode);
		final int remoteY = processIntegerAttribute(node, "remoteY", parametersNode);
		final int radius = processIntegerAttribute(node, "radius",
				parametersNode);
		final String color = processStringAttribute(node, "color",
				parametersNode);

		/* create the city */
		final City city = new City(name, localX, localY, remoteX, remoteY, radius, color);

		if (pointsByLocation.contains(city)) {
			addErrorNode("duplicateCityCoordinates", commandNode, parametersNode);
		} else if (citiesByName.containsKey(name) || pointsByName.containsKey(name)) {
			addErrorNode("duplicateCityName", commandNode, parametersNode);
		} else {
			final Element outputNode = results.createElement("output");

			/* add city to dictionary */
			citiesByName.put(name, city);
			pointsByLocation.add(city);

			/* add success node to results */
			addSuccessNode(commandNode, parametersNode, outputNode);
		}
	}

	/**
	 * Clears all the data structures do there are not cities or roads in
	 * existence in the dictionary or on the map.
	 * 
	 * @param node
	 *            clearAll node to be processed
	 */
	public void processClearAll(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		/* clear data structures */
		citiesByName.clear();
		pointsByName.clear();
		pointsByLocation.clear();
		quadtree.clear();

		metropoleList.clear();
		airportToTerminal.clear();
		/* clear canvas */
		// canvas.clear();
		/* add a rectangle to show where the bounds of the map are located */
		// canvas.addRectangle(0, 0, spatialWidth, spatialHeight, Color.BLACK,
		// false);
		/* add success node to results */
		addSuccessNode(commandNode, parametersNode, outputNode);
	}

	/**
	 * Lists all the cities, either by name or by location.
	 * 
	 * @param node
	 *            listCities node to be processed
	 */
	public void processListCities(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final String sortBy = processStringAttribute(node, "sortBy",
				parametersNode);

		if (citiesByName.isEmpty()) {
			addErrorNode("noCitiesToList", commandNode, parametersNode);
		} else {
			final Element outputNode = results.createElement("output");
			final Element cityListNode = results.createElement("cityList");
			
			if (sortBy.equals("name")) {
				for (City c : citiesByName.values()) {
					addCityNode(cityListNode, c);
				}
			} else if (sortBy.equals("coordinate")) {
				for (PointWise p : pointsByLocation) {
					if (p instanceof City) addCityNode(cityListNode, (City)p);
				}
			} else {
				/* XML validator failed */
				System.exit(-1);
			}

			outputNode.appendChild(cityListNode);

			/* add success node to results */
			addSuccessNode(commandNode, parametersNode, outputNode);
		}
	}

	/**
	 * Creates a city node containing information about a city. Appends the city
	 * node to the passed in node.
	 * 
	 * @param node
	 *            node which the city node will be appended to
	 * @param cityNodeName
	 *            name of city node
	 * @param city
	 *            city which the city node will describe
	 */
	private void addCityNode(final Element node, final String cityNodeName,
			final City city) {
		final Element cityNode = results.createElement(cityNodeName);
		cityNode.setAttribute("name", city.getName());
		cityNode.setAttribute("localX", Integer.toString((int) city.getLocalX()));
		cityNode.setAttribute("localY", Integer.toString((int) city.getLocalY()));
		cityNode.setAttribute("remoteX", Integer.toString((int) city.getRemoteX()));
		cityNode.setAttribute("remoteY", Integer.toString((int) city.getRemoteY()));

		cityNode.setAttribute("radius",
				Integer.toString((int) city.getRadius()));
		cityNode.setAttribute("color", city.getColor());
		node.appendChild(cityNode);
	}

	private void addCityNode(final Element node, final City city) {
		addCityNode(node, "city", city);
	}
	
	private void addRoadNode(final Element node, final Road road) {
		addRoadNode(node, "road", road);
	}

	private void addRoadNode(final Element node, final String roadNodeName,
			final Road road) {
		final Element roadNode = results.createElement(roadNodeName);
		roadNode.setAttribute("start", road.getStart().getName());
		roadNode.setAttribute("end", road.getEnd().getName());
		node.appendChild(roadNode);
	}
	
	public void processDeleteCity(Element node) {
		final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");
        final Element outputNode = results.createElement("output");
		final String name = processStringAttribute(node, "name", parametersNode);

		if (citiesByName.containsKey(name)) {
			City city = citiesByName.get(name);
			//clean up dictionaries
			citiesByName.remove(name);
			pointsByLocation.remove(city);
			
			int cityRemoteX = city.getRemoteX();
			int cityRemoteY = city.getRemoteY();
			
			Metropole m = quadtree.findMetropoleByLocation(cityRemoteX, cityRemoteY);
			
			if (m != null ) {
				PMQuadtree pmTree = m.getPMQuadtree();
				TreeSet<Road> roadsFromCity = (TreeSet<Road>) pmTree.roads.getRoadSet(city);
	
				if (roadsFromCity != null && !roadsFromCity.isEmpty()) {
					roadsFromCity = (TreeSet<Road>) roadsFromCity.clone();
					//deletions in the pmquadtree <- do it before modifying roads structure
					pmTree.deletePoint(city);
					
					//modify adjacency list
					pmTree.roads.deleteCity(city);
					
					addCityNode(outputNode, "cityUnmapped", city);
					
					for (Road r : roadsFromCity) {
						addRoadNode(outputNode, "roadUnmapped", r);
					}
				}
			}
			addSuccessNode(commandNode, parametersNode, outputNode);
		} else {
            addErrorNode("cityDoesNotExist", commandNode, parametersNode);
		}
	}
	
	public void processPrintAvlTree(Element node) {
        final Element commandNode = getCommandNode(node);
        final Element parametersNode = results.createElement("parameters");
        final Element outputNode = results.createElement("output");

        if (citiesByName.isEmpty()) {
            addErrorNode("emptyTree", commandNode, parametersNode);
        } else {
            outputNode.appendChild(citiesByName.createXml(outputNode));
            addSuccessNode(commandNode, parametersNode, outputNode);
        }
    }
	
	public boolean startEndTooClose(PointWise start, PointWise end) {
		return (Math.abs(start.getLocalX() - end.getLocalX()) <= 1) && 
				(Math.abs(start.getLocalY() - end.getLocalY()) <= 1);
	}
	
	public void processMapRoad(Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final String start = processStringAttribute(node, "start",
				parametersNode);
		final String end = processStringAttribute(node, "end", parametersNode);

		final Element outputNode = results.createElement("output");

		final City startCity = citiesByName.get(start);
		final City endCity = citiesByName.get(end);
		
		if (!citiesByName.containsKey(start)) {
			addErrorNode("startPointDoesNotExist", commandNode, parametersNode);
		} else if (!citiesByName.containsKey(end)) {
			addErrorNode("endPointDoesNotExist", commandNode, parametersNode);
		} else if (start.equals(end)) {
			addErrorNode("startEqualsEnd", commandNode, parametersNode);
		} else if ((startCity.getRemoteX() != endCity.getRemoteX()) || (startCity.getRemoteY() != endCity.getRemoteY())) {
			addErrorNode("roadNotInOneMetropole", commandNode, parametersNode);
	    } else {
			int metropoleX = startCity.getRemoteX();
			int metropoleY = startCity.getRemoteY();
			
			Metropole temp = quadtree.findMetropoleByLocation(metropoleX, metropoleY);

			if (temp == null) {
				temp = new Metropole(metropoleX, metropoleY, localSpatialWidth, localSpatialHeight, pmOrder);
				quadtree.insert(temp);
				metropoleList.add(temp);
			}
			PMQuadtree pmTree = temp.getPMQuadtree();

			Rectangle2D.Float world = new Rectangle2D.Float(0, 0, localSpatialWidth, localSpatialHeight);
			Road thisRoad = new Road(startCity, endCity);
			
			if (!Inclusive2DIntersectionVerifier.intersects(thisRoad.toLine2D(), world) ||
				this.outOfRemoteBounds(startCity.getRemoteX(), startCity.getRemoteY()))
					addErrorNode("roadOutOfBounds", commandNode, parametersNode);
			else if (pmTree.roads.containsRoad(thisRoad) || pmTree.roads.containsRoad(new Road(endCity, startCity)))
				addErrorNode("roadAlreadyMapped", commandNode, parametersNode);
			else {
				Err e = new Err();

				// add to spatial structure
				pmTree.addRoad(thisRoad, e);
				if (e.pmrule && e.intersect) {
					if (pmTree.intersectsAnotherRoad(thisRoad))
						addErrorNode("roadIntersectsAnotherRoad", commandNode, parametersNode);
					else addErrorNode("roadViolatesPMRules", commandNode, parametersNode);				
				} else {
					if (Inclusive2DIntersectionVerifier.intersects(startCity.toLocalPoint2D(), world)
						&& Inclusive2DIntersectionVerifier.intersects(endCity.toLocalPoint2D(), world)) {
						// add to adjacency list
						pmTree.roads.addRoad((City) citiesByName.get(start),
								(City) citiesByName.get(end));
					}
					// create roadCreated element
					final Element roadCreatedNode = results
							.createElement("roadCreated");
					roadCreatedNode.setAttribute("start", start);
					roadCreatedNode.setAttribute("end", end);
					outputNode.appendChild(roadCreatedNode);
					// add success node to results
					addSuccessNode(commandNode, parametersNode, outputNode);
				}						
			}
		}
	}
    
	public boolean outOfRemoteBounds(int remoteX, int remoteY) {
		if (remoteX >= remoteSpatialWidth || remoteX < 0 || remoteY >= remoteSpatialHeight || remoteY < 0) return true;
		return false;
	}
	
	public boolean outOfLocalBounds(int localX, int localY) {
		if (localX > localSpatialWidth || localX < 0 || localY > localSpatialHeight || localY < 0) return true;
		return false;
	}
	
	public void processMapAirport(Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final String name = processStringAttribute(node, "name", parametersNode);
		final int localX = processIntegerAttribute(node, "localX", parametersNode);
		final int localY = processIntegerAttribute(node, "localY", parametersNode);

		final int remoteX = processIntegerAttribute(node, "remoteX", parametersNode);
		final int remoteY = processIntegerAttribute(node, "remoteY", parametersNode);
		
		final String terminalName = processStringAttribute(node, "terminalName", parametersNode);
		final int terminalX = processIntegerAttribute(node, "terminalX", parametersNode);
		final int terminalY = processIntegerAttribute(node, "terminalY", parametersNode);
		final String terminalCity = processStringAttribute(node, "terminalCity", parametersNode);

		final Element outputNode = results.createElement("output");

		if (citiesByName.containsKey(name) || pointsByName.containsKey(name)) {
			addErrorNode("duplicateAirportName", commandNode, parametersNode);
		} else if (pointsByLocation.contains(new PointWise(localX, localY, remoteX, remoteY, name))) {
			addErrorNode("duplicateAirportCoordinates", commandNode, parametersNode);
		} else if (this.outOfLocalBounds(localX, localY) || this.outOfRemoteBounds(remoteX, remoteY)) {
			addErrorNode("airportOutOfBounds", commandNode, parametersNode);
		} else if (citiesByName.containsKey(terminalName) || pointsByName.containsKey(terminalName)) {
			addErrorNode("duplicateTerminalName", commandNode, parametersNode);
		} else if (pointsByLocation.contains(new PointWise(terminalX, terminalY, remoteX, remoteY, terminalName))) {
			addErrorNode("duplicateTerminalCoordinates", commandNode, parametersNode);
		} else if (this.outOfLocalBounds(terminalX, terminalY)) {
			addErrorNode("terminalOutOfBounds", commandNode, parametersNode);
		} else if (!citiesByName.containsKey(terminalCity)) {
			addErrorNode("connectingCityDoesNotExist", commandNode, parametersNode);
		} else if (!citiesByName.get(terminalCity).toRemotePoint2D().equals(new Point2D.Float(remoteX, remoteY))){
			addErrorNode("connectingCityNotInSameMetropole", commandNode, parametersNode);
		} else {

			Metropole m = quadtree.findMetropoleByLocation(remoteX, remoteY);
			PMQuadtree pmTree;
			
			if (m == null) {
				m = new Metropole(remoteX, remoteY, localSpatialWidth, localSpatialHeight, pmOrder);
				quadtree.insert(m);
				metropoleList.add(m);
			}
			
			pmTree = m.getPMQuadtree();
			Airport airport = new Airport(localX, localY, remoteX, remoteY, name);
			Terminal terminal = new Terminal(airport, terminalX, terminalY, remoteX, remoteY, terminalName);
			Road road = new Road(terminal, citiesByName.get(terminalCity));
			terminal.setTerminalToCity(road);
			
			Err e =  new Err();
			pmTree.addIsolatedPoint(airport, e);

			if (e.pmrule) {
				addErrorNode("airportViolatesPMRules", commandNode, parametersNode);
				
			} else if (!pmTree.containsPoint(terminalCity)) {
					pmTree.deleteAirport(airport);
					addErrorNode("connectingCityNotMapped", commandNode, parametersNode);
			} else {
			
				pmTree.addRoad(road, e);
				
				if (e.intersect) {
					if (pmTree.intersectsAnotherRoad(road))
						addErrorNode("roadIntersectsAnotherRoad", commandNode, parametersNode);
					else addErrorNode("terminalViolatesPMRules", commandNode, parametersNode);
					
					pmTree.deleteAirport(airport);
				} else {
					//Add to the adjacency list
					pmTree.roads.addRoad(road);
					
					//Add to the point structure
					pointsByName.put(name, airport);
					pointsByName.put(terminalName, terminal);
					pointsByLocation.add(terminal);
					pointsByLocation.add(airport);
					
					//Associate terminal with the airport
					if (airportToTerminal.get(airport) == null) airportToTerminal.put(airport, new TreeSet<Terminal>(new PointNameComparator()));
					airportToTerminal.get(airport).add(terminal);
					
					/* add success nxode to results */
					addSuccessNode(commandNode, parametersNode, outputNode);
				}
			}
		}
	}

	/*public void processShortestPath(final Element node) throws IOException,
			ParserConfigurationException, TransformerException {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final String start = processStringAttribute(node, "start",
				parametersNode);
		final String end = processStringAttribute(node, "end", parametersNode);

		String saveMapName = "";
		if (!node.getAttribute("saveMap").equals("")) {
			saveMapName = processStringAttribute(node, "saveMap",
					parametersNode);
		}

		String saveHTMLName = "";
		if (!node.getAttribute("saveHTML").equals("")) {
			saveHTMLName = processStringAttribute(node, "saveHTML",
					parametersNode);
		}

		if (!pmQuadtree.containsCity(start)) {
			addErrorNode("nonExistentStart", commandNode, parametersNode);
		} else if (!pmQuadtree.containsCity(end)) {
			addErrorNode("nonExistentEnd", commandNode, parametersNode);
		} else if (!roads.getCitySet().contains(citiesByName.get(start))
				|| !roads.getCitySet().contains(citiesByName.get(end))) {
			// start or end is isolated
			if (start.equals(end)) {
				final Element outputNode = results.createElement("output");
				final Element pathNode = results.createElement("path");
				pathNode.setAttribute("length", "0.000");
				pathNode.setAttribute("hops", "0");

				LinkedList<City> cityList = new LinkedList<City>();
				cityList.add(citiesByName.get(start));
				// if required, save the map to an image 
				if (!saveMapName.equals("")) {
					saveShortestPathMap(saveMapName, cityList);
				}
				if (!saveHTMLName.equals("")) {
					saveShortestPathMap(saveHTMLName, cityList);
				}

				outputNode.appendChild(pathNode);
				Element successNode = addSuccessNode(commandNode,
						parametersNode, outputNode);

				if (!saveHTMLName.equals("")) {
					// save shortest path to HTML 
					Document shortestPathDoc = XmlUtility.getDocumentBuilder()
							.newDocument();
					org.w3c.dom.Node spNode = shortestPathDoc.importNode(
							successNode, true);
					shortestPathDoc.appendChild(spNode);
					XmlUtility.transform(shortestPathDoc, new File(
							"shortestPath.xsl"), new File(saveHTMLName
							+ ".html"));
				}
			} else {
				addErrorNode("noPathExists", commandNode, parametersNode);
			}
		} else {
			final DecimalFormat decimalFormat = new DecimalFormat("#0.000");

			final Dijkstranator dijkstranator = new Dijkstranator(roads);

			final City startCity = (City) citiesByName.get(start);
			final City endCity = (City) citiesByName.get(end);

			final Path path = dijkstranator.getShortestPath(startCity, endCity);

			if (path == null) {
				addErrorNode("noPathExists", commandNode, parametersNode);
			} else {
				final Element outputNode = results.createElement("output");

				final Element pathNode = results.createElement("path");
				pathNode.setAttribute("length",
						decimalFormat.format(path.getDistance()));
				pathNode.setAttribute("hops", Integer.toString(path.getHops()));

				final LinkedList<City> cityList = path.getCityList();

				// if required, save the map to an image 
				if (!saveMapName.equals("")) {
					saveShortestPathMap(saveMapName, cityList);
				}
				if (!saveHTMLName.equals("")) {
					saveShortestPathMap(saveHTMLName, cityList);
				}

				if (cityList.size() > 1) {
					// add the first road 
					City city1 = cityList.remove();
					City city2 = cityList.remove();
					Element roadNode = results.createElement("road");
					roadNode.setAttribute("start", city1.getName());
					roadNode.setAttribute("end", city2.getName());
					pathNode.appendChild(roadNode);

					while (!cityList.isEmpty()) {
						City city3 = cityList.remove();

						// process the angle 
						Arc2D.Float arc = new Arc2D.Float();
						arc.setArcByTangent(city1.toLocalPoint2D(),
								city2.toLocalPoint2D(), city3.toLocalPoint2D(), 1);

						// print out the direction 
						double angle = arc.getAngleExtent();
						final String direction;
						while (angle < 0) {
							angle += 360;
						}
						while (angle > 360) {
							angle -= 360;
						}
						/* This forces boundary between left and straight to be "go straight"go
						 * and boundary between right and straight to be "go right"
						 * -- Eric
						 *
						if (angle > 180 && angle < 180 + 135) {
							direction = "left";
						} else if (angle >= 45 && angle <= 180 ) {
							direction = "right";
						} else {
							direction = "straight";
						}
						Element directionNode = results
								.createElement(direction);
						pathNode.appendChild(directionNode);

						// print out the next road 
						roadNode = results.createElement("road");
						roadNode.setAttribute("start", city2.getName());
						roadNode.setAttribute("end", city3.getName());
						pathNode.appendChild(roadNode);

						// increment city references 
						city1 = city2;
						city2 = city3;
					}
				}
				outputNode.appendChild(pathNode);
				Element successNode = addSuccessNode(commandNode,
						parametersNode, outputNode);

				if (!saveHTMLName.equals("")) {
					// save shortest path to HTML
					Document shortestPathDoc = XmlUtility.getDocumentBuilder()
							.newDocument();
					org.w3c.dom.Node spNode = shortestPathDoc.importNode(
							successNode, true);
					shortestPathDoc.appendChild(spNode);
					XmlUtility.transform(shortestPathDoc, new File(
							"shortestPath.xsl"), new File(saveHTMLName
							+ ".html"));
				}
			}
		}
	}

	private void saveShortestPathMap(final String mapName,
			final List<City> cityList) throws IOException {
		final CanvasPlus map = new CanvasPlus();
		// initialize map 
		map.setFrameSize(spatialWidth, spatialHeight);
		// add a rectangle to show where the bounds of the map are located 
		map.addRectangle(0, 0, spatialWidth, spatialHeight, Color.BLACK, false);

		final Iterator<City> it = cityList.iterator();
		City city1 = it.next();

		/ map green starting point
		map.addPoint(city1.getName(), city1.getX(), city1.getY(), Color.GREEN);

		if (it.hasNext()) {
			City city2 = it.next();
			// map blue road 
			map.addLine(city1.getX(), city1.getY(), city2.getX(), city2.getY(),
					Color.BLUE);

			while (it.hasNext()) {
				// increment cities
				city1 = city2;
				city2 = it.next();

				// map point 
				map.addPoint(city1.getName(), city1.getX(), city1.getY(),
						Color.BLUE);

				// map blue road 
				map.addLine(city1.getX(), city1.getY(), city2.getX(),
						city2.getY(), Color.BLUE);
			}

			/ map red end point
			map.addPoint(city2.getName(), city2.getX(), city2.getY(), Color.RED);

		}

		// save map to image file
		map.save(mapName);

		map.dispose();
	}*/

	public void processMapTerminal(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final String name = processStringAttribute(node, "name", parametersNode);
		final int localX = processIntegerAttribute(node, "localX", parametersNode);
		final int localY = processIntegerAttribute(node, "localY", parametersNode);

		final int remoteX = processIntegerAttribute(node, "remoteX", parametersNode);
		final int remoteY = processIntegerAttribute(node, "remoteY", parametersNode);
		
		final String cityName = processStringAttribute(node, "cityName", parametersNode);
		final String airportName = processStringAttribute(node, "airportName", parametersNode);

		final Element outputNode = results.createElement("output");

		if (citiesByName.containsKey(name) || pointsByName.containsKey(name)) {
			addErrorNode("duplicateTerminalName", commandNode, parametersNode);
		} else if (pointsByLocation.contains(new PointWise(localX, localY, remoteX, remoteY, name))) {
			addErrorNode("duplicateTerminalCoordinates", commandNode, parametersNode);
		} else if (this.outOfLocalBounds(localX, localY) || this.outOfRemoteBounds(remoteX, remoteY)) {
			addErrorNode("terminalOutOfBounds", commandNode, parametersNode);
		} else if (!pointsByName.containsKey(airportName)) {
			addErrorNode("airportDoesNotExist", commandNode, parametersNode);
		} else if (!pointsByName.get(airportName).getRemotePt().equals(new Point2D.Float(remoteX, remoteY))){
			addErrorNode("airportNotInSameMetropole", commandNode, parametersNode);
		} else if (!citiesByName.containsKey(cityName)) {
			addErrorNode("connectingCityDoesNotExist", commandNode, parametersNode);
		} else if (!citiesByName.get(cityName).getRemotePt().equals(new Point2D.Float(remoteX, remoteY))){
			addErrorNode("connectingCityNotInSameMetropole", commandNode, parametersNode);
		} else {
				Metropole m = quadtree.findMetropoleByLocation(remoteX, remoteY);
					
				Airport airport = (Airport) pointsByName.get(airportName);
				PMQuadtree pmTree = m.getPMQuadtree();
				Terminal terminal = new Terminal(airport, localX, localY, remoteX, remoteY, name);
				Road road = new Road(terminal, citiesByName.get(cityName));
				terminal.setTerminalToCity(road);
				
				if (!pmTree.containsPoint(cityName)) {
					addErrorNode("connectingCityNotMapped", commandNode, parametersNode);
				} else {
					Err e = new Err();	
					//Add to the pmquadtree structure
					pmTree.addRoad(road, e);
					
					if (e.intersect) {
						if (pmTree.intersectsAnotherRoad(road))
							addErrorNode("roadIntersectsAnotherRoad", commandNode, parametersNode);
						else addErrorNode("terminalViolatesPMRules", commandNode, parametersNode);
					} else {
						//Add to the adjacency list
						pmTree.roads.addRoad(road);
						
						//Adds to the point structure
						pointsByName.put(name, terminal);
						pointsByLocation.add(terminal);
						
						//Associate airport with the terminal
						airportToTerminal.get(airport).add(terminal);
						
						/* add success node to results */
						addSuccessNode(commandNode, parametersNode, outputNode);
					}
				}				
				
		}
	}
	
	public void processUnmapRoad(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final String start = processStringAttribute(node, "start", parametersNode);
		final String end = processStringAttribute(node, "end", parametersNode);

		final Element outputNode = results.createElement("output");
		
		Element roadDeleted = results.createElement("roadDeleted");
		roadDeleted.setAttribute("start", start);
		roadDeleted.setAttribute("end", end);
		outputNode.appendChild(roadDeleted);
		
		//Dealing with errors from here
		if (!citiesByName.containsKey(start))
			addErrorNode("startPointDoesNotExist", commandNode, parametersNode);
		else if (!citiesByName.containsKey(end))
			addErrorNode("endPointDoesNotExist", commandNode, parametersNode);
		else if (start.equals(end))
			addErrorNode("startEqualsEnd", commandNode, parametersNode);
		else {
			City startCity = citiesByName.get(start);
			City endCity = citiesByName.get(end);
			Road road = new Road(startCity, endCity);
			
			Metropole m = quadtree.findMetropoleByLocation(startCity.getRemoteX(), startCity.getRemoteY());
			PMQuadtree pmTree = m.getPMQuadtree();
			
			if (pmTree.roads.containsRoad(road)) {
				
				//also unmap the endcity from the map unless it is part of another mapped road
				
				//modify the pmquad tree
				pmTree.unmapRoad(road);
				//remove from the adjacency list
				pmTree.roads.removeRoad(road);
				
				
		        addSuccessNode(commandNode, parametersNode, outputNode);
			} else {
				addErrorNode("roadNotMapped", commandNode, parametersNode);
			}
		}
	}
	
	public void processUnmapAirport(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final String name = processStringAttribute(node, "name", parametersNode);
		
		final Element outputNode = results.createElement("output");
		
		if (pointsByName.containsKey(name)) {
			Airport a = (Airport) pointsByName.get(name);
			PMQuadtree pmTree = quadtree.findMetropoleByLocation(a.getRemoteX(), a.getRemoteY()).getPMQuadtree();
			
			for (Terminal t : airportToTerminal.get(a)) {
				Element ele = results.createElement("terminalUnmapped");
				outputNode.appendChild(ele);
				Road r = t.getTerminalToCity();
				
				ele.setAttribute("name", t.getName());
				ele.setAttribute("airportName", a.getName());
				ele.setAttribute("cityName", r.getOtherPoint(t.getName()).getName());
				ele.setAttribute("localX", t.getLocalX()+"");
				ele.setAttribute("localY", t.getLocalY()+"");
				ele.setAttribute("remoteX", t.getRemoteX()+"");
				ele.setAttribute("remoteY", t.getRemoteY()+"");
				
				//remove from the adjacency list
				pmTree.roads.removeRoad(r);
				//modify the pmquadtree
				pmTree.unmapTerminalRoad(r);
				pointsByLocation.remove(t);
				pointsByName.remove(t.getName());
			}
			//modify the pmquadtree
			pmTree.deleteAirport(a);
			pointsByLocation.remove(a);
			pointsByName.remove(a.getName());
			//wipe up the association
			airportToTerminal.remove(a);
			
	        addSuccessNode(commandNode, parametersNode, outputNode);
		} else {
			addErrorNode("airportDoesNotExist", commandNode, parametersNode);
		}
	}
	
	public void processUnmapTerminal(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final String name = processStringAttribute(node, "name", parametersNode);
		final Element outputNode = results.createElement("output");

		if (pointsByName.containsKey(name)) {
			Terminal t = (Terminal) pointsByName.get(name);
			PMQuadtree pmTree = quadtree.findMetropoleByLocation(t.getRemoteX(), t.getRemoteY()).getPMQuadtree();
			Road r = t.getTerminalToCity();
			pmTree.unmapTerminalRoad(r);
			pmTree.roads.removeRoad(r);
				
			
			Airport a = t.getAirport();
			
			TreeSet<Terminal> treeset = airportToTerminal.get(a);
			if (treeset.size() == 1) {
				Element airportUnmapped = results.createElement("airportUnmapped");
				airportUnmapped.setAttribute("name", a.getName());
				outputNode.appendChild(airportUnmapped);
				
				pmTree.deleteAirport(a);
				airportToTerminal.remove(a);
				pointsByName.remove(a.getName());
				pointsByLocation.remove(a);
			} else {
				treeset.remove(t);
			}
			pointsByName.remove(name);
			pointsByLocation.remove(t);
			
	        addSuccessNode(commandNode, parametersNode, outputNode);
		} else addErrorNode("terminalDoesNotExist", commandNode, parametersNode);
	}
	
	/**
	 * Prints out the structure of the PM Quadtree in an XML format.
	 * 
	 * @param node
	 *            printPMQuadtree command to be processed
	 */

	public void processPrintPMQuadtree(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		
		final int remoteX = processIntegerAttribute(node, "remoteX", parametersNode);
		final int remoteY = processIntegerAttribute(node, "remoteY", parametersNode);
		
		
		Metropole m = quadtree.findMetropoleByLocation(remoteX, remoteY);
		
		final Element outputNode = results.createElement("output");
		if (this.outOfRemoteBounds(remoteX, remoteY))
			addErrorNode("metropoleOutOfBounds", commandNode, parametersNode);
		else if (m == null) {
			/* empty PR Quadtree */
			addErrorNode("metropoleIsEmpty", commandNode, parametersNode);
		} else {
			/* print PR Quadtree */
			PMQuadtree pmQuadtree = m.getPMQuadtree();
			final Element quadtreeNode = results.createElement("quadtree");
			quadtreeNode.setAttribute("order", Integer.toString(pmOrder));
			printPMQuadtreeHelper(pmQuadtree.getRoot(), quadtreeNode);

			outputNode.appendChild(quadtreeNode);

			/* add success node to results */
			addSuccessNode(commandNode, parametersNode, outputNode);
		}
	}

	/**
	 * Traverses each node of the PR Quadtree.
	 * 
	 * @param currentNode
	 *            PR Quadtree node being printed
	 * @param xmlNode
	 *            XML node representing the current PR Quadtree node
	 */

	private void printPMQuadtreeHelper(final Node currentNode,
			final Element xmlNode) {
		if (currentNode.getType() == Node.WHITE) {
			Element white = results.createElement("white");
			xmlNode.appendChild(white);
		} else if (currentNode.getType() == Node.BLACK) {
			Black currentLeaf = (Black) currentNode;
			Element blackNode = results.createElement("black");
			blackNode.setAttribute("cardinality",
					Integer.toString(currentLeaf.getGeometry().size()));
			for (Geometry g : currentLeaf.getGeometry()) {
				if (g.isCity()) {
					if (g instanceof Airport) {
						Airport a = (Airport) g;
						Element city = results.createElement("airport");
						city.setAttribute("name", a.getName());
						city.setAttribute("remoteX", a.getRemoteX()+"");
						city.setAttribute("remoteY", a.getRemoteY()+"");
						city.setAttribute("localX", a.getLocalX()+"");
						city.setAttribute("localY", a.getLocalY()+"");
						blackNode.appendChild(city);
					} else if (g instanceof Terminal) {
						Terminal t = (Terminal) g;
						Element city = results.createElement("terminal");
						city.setAttribute("name", t.getName());
						Road roadOfTerminal = t.getTerminalToCity();
						PointWise cityConnected = roadOfTerminal.getOtherPoint(t.getName());
						
						city.setAttribute("cityName", cityConnected.getName());
						city.setAttribute("airportName", t.getAirport().getName());
						city.setAttribute("remoteX", t.getRemoteX()+"");
						city.setAttribute("remoteY", t.getRemoteY()+"");
						city.setAttribute("localX", t.getLocalX()+"");
						city.setAttribute("localY", t.getLocalY()+"");
						blackNode.appendChild(city);
					} else {
						City c = (City) g;
						Element city = results.createElement("city");
						city.setAttribute("name", c.getName());
						city.setAttribute("remoteX", c.getRemoteX()+"");
						city.setAttribute("remoteY", c.getRemoteY()+"");
						city.setAttribute("localX", c.getLocalX()+"");
						city.setAttribute("localY", c.getLocalY()+"");
						city.setAttribute("radius",
								Integer.toString((int) c.getRadius()));
						city.setAttribute("color", c.getColor());
						blackNode.appendChild(city);
					}
				} else {
					addRoadNode(blackNode, (Road)g);
				}
			}
			xmlNode.appendChild(blackNode);
		} else {
			final Gray currentInternal = (Gray) currentNode;
			final Element gray = results.createElement("gray");
			gray.setAttribute("x",
					Integer.toString((int) currentInternal.getCenterX()));
			gray.setAttribute("y",
					Integer.toString((int) currentInternal.getCenterY()));
			for (int i = 0; i < 4; i++) {
				printPMQuadtreeHelper(currentInternal.getChild(i), gray);
			}
			xmlNode.appendChild(gray);
		}
	}

	/**
	 * Processes a saveMap command. Saves the graphical map to a given file.
	 * 
	 * @param node
	 *            saveMap command to be processed
	 * @throws IOException
	 *             problem accessing the image file
	 */
	public void processSaveMap(final Element node) throws IOException {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");

		final int remoteX = processIntegerAttribute(node, "remoteX", parametersNode);
		final int remoteY = processIntegerAttribute(node, "remoteY", parametersNode);
		final String name = processStringAttribute(node, "name", parametersNode);

		final Element outputNode = results.createElement("output");

		CanvasPlus canvas = drawPMQuadtree(remoteX, remoteY);

		/* save canvas to '(name).png' */
		canvas.save(name);
		canvas.dispose();

		/* add success node to results */
		addSuccessNode(commandNode, parametersNode, outputNode);
	}

	private CanvasPlus drawPMQuadtree(int remoteX, int remoteY) {
		final CanvasPlus canvas = new CanvasPlus("MeeshQuest");
		PMQuadtree pmQuadtree = quadtree.findMetropoleByLocation(remoteX, remoteY).getPMQuadtree();

		/* initialize canvas */
		canvas.setFrameSize(localSpatialWidth, localSpatialHeight);

		/* add a rectangle to show where the bounds of the map are located */
		canvas.addRectangle(0, 0, localSpatialWidth, localSpatialHeight, Color.BLACK,
				false);

		/* draw PM Quadtree */
		drawPMQuadtreeHelper(pmQuadtree.getRoot(), canvas);

		return canvas;
	}

	private void drawPMQuadtreeHelper(Node node, CanvasPlus canvas) {
		if (node.getType() == Node.BLACK) {
			Black blackNode = (Black) node;
			for (Geometry g : blackNode.getGeometry()) {
				if (g.isCity()) {
					PointWise city = (PointWise)g;
					canvas.addPoint(city.getName(), city.getLocalX(), city.getLocalY(),
							Color.BLACK);
				} else {
					Road road = (Road) g;
					canvas.addLine(road.getStart().getLocalX(), road.getStart()
							.getLocalY(), road.getEnd().getLocalX(),
							road.getEnd().getLocalY(), Color.BLACK);
				}
			}
		} else if (node.getType() == Node.GRAY) {
			Gray grayNode = (Gray) node;
			canvas.addCross(grayNode.getCenterX(), grayNode.getCenterY(),
					grayNode.getHalfWidth(), Color.GRAY);
			for (int i = 0; i < 4; i++) {
				drawPMQuadtreeHelper(grayNode.getChild(i), canvas);
			}
		}
	}

	/**
	 * Finds the mapped cities within the range of a given point.
	 * 
	 * @param node
	 *            rangeCities command to be processed
	 * @throws IOException
	 */
	public void processGlobalRangeCities(final Element node) throws IOException {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		final int remoteX = processIntegerAttribute(node, "remoteX", parametersNode);
		final int remoteY = processIntegerAttribute(node, "remoteY", parametersNode);
		final int radius = processIntegerAttribute(node, "radius",
				parametersNode);

		final TreeSet<Metropole> metropolesInRange = new TreeSet<Metropole>();
		for (Metropole m : metropoleList) {
			if (Inclusive2DIntersectionVerifier.intersects(
					new Point2D.Float(m.getRemoteX(), m.getRemoteY()), 
					new Circle2D.Float(new Point2D.Float(remoteX, remoteY), radius))
				)
				metropolesInRange.add(m);
		}

		/* print out cities within range */
		if (metropolesInRange.isEmpty()) {
			addErrorNode("noCitiesExistInRange", commandNode, parametersNode);
		} else {
			
			PriorityQueue<City> q = new PriorityQueue<City>(
					new Comparator<City>() {

						@Override
						public int compare(City c1, City c2) {
							return -c1.getName().compareTo(c2.getName());
						}
						
					}
			);
			
			for (Metropole m : metropolesInRange) {
				for (City c : m.getPMQuadtree().citySet) {
					q.offer(c);
				}
			}
			/* get city list */
			final Element cityListNode = results.createElement("cityList");
			while(!q.isEmpty()) {
				addCityNode(cityListNode, q.remove());
			}
			outputNode.appendChild(cityListNode);

			/* add success node to results */
			addSuccessNode(commandNode, parametersNode, outputNode);
		}
	}

	public void processNearestCity(Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		/* extract attribute values from command */
		final int localX = processIntegerAttribute(node, "localX", parametersNode);
		final int localY = processIntegerAttribute(node, "localY", parametersNode);
		final int remoteX = processIntegerAttribute(node, "remoteX", parametersNode);
		final int remoteY = processIntegerAttribute(node, "remoteY", parametersNode);

		Metropole m = quadtree.findMetropoleByLocation(remoteX, remoteY);

		if (this.outOfRemoteBounds(remoteX, remoteY) || m == null || m.getPMQuadtree().citySet.isEmpty()) {
			addErrorNode("cityNotFound", commandNode, parametersNode);
		} else {
			PriorityQueue<City> q = new PriorityQueue<City>(
					new Comparator<City>() {

						@Override
						public int compare(City o1, City o2) {							
							double dist1 = Math.pow((localX - o1.getLocalX()), 2) + Math.pow(localY - o1.getLocalY(), 2);
							double dist2 = Math.pow((localX - o2.getLocalX()), 2) + Math.pow(localY - o2.getLocalY(), 2);
							
							if (dist1 == dist2) {
								return o1.getName().compareTo(o2.getName());
							} else return (int)-(dist2 - dist1);
						}
						
					}
			);
			
			for (City c : m.getPMQuadtree().citySet) {
				q.add(c);
			}

			addCityNode(outputNode, q.remove());
			addSuccessNode(commandNode, parametersNode, outputNode);
		}
	}
	
	public void processMst(final Element node) {
		final Element commandNode = getCommandNode(node);
		final Element parametersNode = results.createElement("parameters");
		final Element outputNode = results.createElement("output");

		final String start = processStringAttribute(node, "start", parametersNode);
		
		if (citiesByName.containsKey(start)) {
			City startCity = citiesByName.get(start);

			MST solveMst = new MST(startCity);
			solveMst.mstHelper(startCity);
			
			Element mst = results.createElement("mst");
			double dist = solveMst.distanceSpanned;
			
			if (dist == 0) {
				addErrorNode("cityNotMapped", commandNode, parametersNode);
			} else {
				DecimalFormat df = new DecimalFormat("#.000"); 
				mst.setAttribute("distanceSpanned", df.format(solveMst.distanceSpanned));
				LinkedList<PointWise> l = solveMst.currentList;
				
				Element mstNode = results.createElement("node");
				mstNode.setAttribute("name", l.poll().getName());
				mst.appendChild(mstNode);
								
				while (!l.isEmpty()) {
					Element curr = results.createElement("node");
					curr.setAttribute("name", l.poll().getName());
					mstNode.appendChild(curr);
					mstNode = curr;
				}
				
				outputNode.appendChild(mst);
		        addSuccessNode(commandNode, parametersNode, outputNode);
			}

		} else 	addErrorNode("cityNotMapped", commandNode, parametersNode);

		
		

	}
	
	
	private class MST {
		LinkedList<PointWise> currentList = new LinkedList<PointWise>();
		TreeMap<Airport, TreeSet<Terminal>> airportsLeft = new TreeMap<Airport, TreeSet<Terminal>>(new PointWiseLocationComparator());
		TreeSet<Road> roadsTraversed = new TreeSet<Road>(new RoadNameComparator());
		double distanceSpanned = 0;
		
		public MST(PointWise point) {
			currentList.add(point);
			airportsLeft.putAll(airportToTerminal);;
		}
		
		
		private void mstHelper(PointWise start) {
			PointWise prev = currentList.peekLast();
			double minDist = Double.MAX_VALUE;
			
			if (start instanceof City) {
				City city = (City)start;
				Metropole m = quadtree.findMetropoleByLocation(city.getRemoteX(), city.getRemoteY());
				PMQuadtree pmTree = m.getPMQuadtree();
				
				TreeSet<Road> adjRoadSet = pmTree.roads.getRoadSet(city);
				Road minDistRoad = null; 
				
				for (Road r : adjRoadSet) {					
					if (!roadsTraversed.contains(r)) {
						double currentDist = r.getDistance();

						if (currentDist < minDist) {
							minDist = currentDist;
							minDistRoad = r;
						} else if (currentDist == minDistRoad.getDistance()) {
							if (r.getOtherPoint(city.getName()).compareTo(minDistRoad.getOtherPoint(city.getName())) > 0) {
								minDistRoad = r;
							}
						}
					}
				}
				
				if (minDistRoad == null) return;
				else {
					distanceSpanned += minDist;
					roadsTraversed.add(minDistRoad);
					currentList.addLast(city);
					mstHelper(minDistRoad.getOtherPoint(city.getName()));
				}
				
			} else if (start instanceof Airport) {
				Airport airport = (Airport)start;
				
				if (prev instanceof Airport) {
					//xiajichang
					TreeSet<Terminal> terminalSet = airportToTerminal.get(airport);
					Terminal minDistTerminal = null;
					
					for (Terminal t : terminalSet) {
						double currentDist = t.getTerminalToCity().getDistance();
						
						if (currentDist < minDist) {
							minDist = currentDist;
							minDistTerminal = t;
						} else if (currentDist == minDist) {
							if (t.getName().compareTo(minDistTerminal.getName()) > 0)
								minDistTerminal = t;
						}
					}
					
					if (minDistTerminal == null) return;
					else {
						distanceSpanned += minDist;
						currentList.addLast(airport);
						mstHelper(minDistTerminal);
					}
				} else {
					//shangjichang
					airportsLeft.remove(airport);
					Airport minDistAirport = null;
					
					for (Airport a : airportsLeft.keySet()) {
						if (!a.getRemotePt().equals(start.getRemotePt()))	 {
							double currentDist = a.getRemotePt().distance(airport.getRemotePt());
							
							if (currentDist < minDist) {
								minDist = currentDist;
								minDistAirport = a;
							} else if (currentDist == minDist && a.getName().compareTo(minDistAirport.getName()) > 0)
								minDistAirport = a;
						}
					}
					
					if (minDistAirport == null) return;
					else {
						distanceSpanned += minDist;
						currentList.addLast(airport);
						mstHelper(minDistAirport);
					}
				}
			} else if (start instanceof Terminal) {
				if (prev instanceof Airport) {
					//xiajichang
					Road oneWayTerminalRoad = ((Terminal) start).getTerminalToCity();
					
					distanceSpanned += oneWayTerminalRoad.getDistance();
					roadsTraversed.add(oneWayTerminalRoad);
					mstHelper(oneWayTerminalRoad.getOtherPoint(start.getName()));
				} else {
					//shangjichang
					Airport oneWayAirport = ((Terminal) start).getAirport();
					
					minDist = oneWayAirport.getLocalPt().distance(((Terminal)start).getLocalPt());
					distanceSpanned += minDist;
					currentList.addLast(start);
					mstHelper(oneWayAirport);
				}
			}
		}
		
	}
	
}
