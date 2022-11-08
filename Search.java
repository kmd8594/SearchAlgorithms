import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Author: Kaitlyn DeCola
 * Search class that holds all the search algorithms and the main method
 */
public class Search {
	
	/*
	 * returns the distance based on latitudes and longitudes
	 */
	private double dist(double lat1, double lat2, double lon1, double lon2) {
		return(Math.sqrt((lat1-lat2) * (lat1-lat2) + (lon1-lon2) * (lon1-lon2)) * 100);
	}
	
	/*
	 * initializes the cities with the edges based on the city.dat and edge.dat files
	 */
	public Map<String, City> initCities(){
		Map<String, City> cities = new HashMap<>();
		
		BufferedReader reader;
		try {
			//creates new cities for each city in the file 
			reader = new BufferedReader(new FileReader("./city.dat"));
			String line = reader.readLine();
			while(line != null) {
				String[] cityInfo = line.split("\\s+");
				City newCity = new City(cityInfo[0], Double.parseDouble(cityInfo[2]), Double.parseDouble(cityInfo[3]));
				cities.put(cityInfo[0], newCity);
				line = reader.readLine();
			}
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		BufferedReader reader2;
		try {
			// populates cities edges based on edge file
			reader2 = new BufferedReader(new FileReader("./edge.dat"));
			String line = reader2.readLine();
			while(line != null) {
				String[] edge = line.split("\\s+");
				City city1 = cities.get(edge[0]);
				City city2 = cities.get(edge[1]);
				city1.addEdge(city2);
				city2.addEdge(city1);
				line = reader2.readLine();
				
			}
			reader2.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		
		return cities;
	}
	
	/*
	 * returns a list with the shortest path using BFS
	 */
	public List<City> bfs(City start, City end){
		List<City> path = new ArrayList<>();
		LinkedList<City> open = new LinkedList<>();
		open.add(start);
		LinkedList<City> closed = new LinkedList<>();
		while(open.size() != 0) {
			City x = open.getFirst();
			if(x.equals(end)) {
				City child = x;
				while(child != null) {
					path.add(child);
					child = child.getParent();
				}
				Collections.reverse(path);
				return path;
			}
			else {
				List<City> children = x.getEdges();
				for(City c : children) {
					if(open.contains(c) || closed.contains(c)) {
						continue;
					}
					c.setParent(x);
					if(c.equals(end)) {
						City child = c;
						while(child != null) {
							path.add(child);
							child = child.getParent();
						}
						Collections.reverse(path);
						return path;
					}
					else {
						open.addLast(c);
						
					}
				}
				closed.add(x);
				open.remove(x);
			}
		}
		return null;
	}
	
	/*
	 * returns a list with the shortest path using DFS
	 */
	public List<City> dfs(City start, City end){
		List<City> path = new ArrayList<>();
		LinkedList<City> open = new LinkedList<>();
		open.add(start);
		LinkedList<City> closed = new LinkedList<>();
		while(open.size() != 0) {
			City x = open.getFirst();
			if(x.equals(end)) {
				City child = x;
				while(child != null) {
					path.add(child);
					child = child.getParent();
				}
				Collections.reverse(path);
				return path;
			}
			else {
				List<City> children = x.getEdges();
				Collections.reverse(children);
				for(City c : children) {
					if(open.contains(c) || closed.contains(c)) {
						continue;
					}
					c.setParent(x);
					if(c.equals(end)) {
						City child = c;
						while(child != null) {
							path.add(child);
							child = child.getParent();
						}
						Collections.reverse(path);
						return path;
					}
					else {
						open.addFirst(c);
						
					}
				}
				closed.add(x);
				open.remove(x);
			}
		}
		return null;
	}
	
	/*
	 * returns a list with the shortest path using A*
	 */
	public List<City> aStar(City start, City end){
		double endLon = end.getLon();
		double endLat = end.getLat();
		List<City> path = new ArrayList<>();
		LinkedHashMap<City, Double> open = new LinkedHashMap<>();
		open.put(start, 0.0);
		LinkedHashMap<City, Double> closed = new LinkedHashMap<>();
		while(open.size() != 0) {
			City x = null;
			double lowestF = (double) open.values().toArray()[0];
			for(City c : open.keySet()) {
				double f = open.get(c);
				if(f <= lowestF) {
					lowestF = f;
					x = c;
				}
			}
			open.remove(x);
			closed.put(x, lowestF);
			if(x.equals(end)) {
				City child = x;
				while(child != null) {
					path.add(child);
					child = child.getParent();
				}
				Collections.reverse(path);
				return path;
			}
			List<City> children = x.getEdges();
			if(children.contains(end)) {
				end.setParent(x);
				City child = end;
				while(child != null) {
					path.add(child);
					child = child.getParent();
				}
				Collections.reverse(path);
				return path;				
			}
			for(City c : children) {				
				double dist = dist(x.getLat(), c.getLat(), x.getLon(), c.getLon()) + x.getG();
				c.setG(dist);
				double distGoal = dist (c.getLat(), endLat, c.getLon(), endLon);
				c.setH(distGoal);
				double f = distGoal + dist;
				if(open.containsKey(c) || closed.containsKey(c)) {
						continue;
				}
				else {
					c.setParent(x);
					open.put(c, f);
				}
			}	
		}
		return null;		
	}
	
	/*
	 * returns a string with the output needed to print
	 */
	public String getOutput(String searchType, List<City> cityRes) {
		String res = "";
		res += searchType + " Results:\n";
		Double dist = 0.0;
		for(City city : cityRes) {
			res += city.getName() + "\n"; 
			City par = city.getParent();
			if(par != null) {
				dist += dist(par.getLat(), city.getLat(), par.getLon(), city.getLon());
			}
		}
		res += "That took " + (cityRes.size()-1) + " hops to find.\n";
		res += "Total distance = " + (int) Math.round(dist) + " miles.\n\n";
		
		return res;
	}

	public static void main(String[] args) throws IOException {
		Search search = new Search();
		Map<String, City> cities = search.initCities();
		City city1;
		City city2;
		// make sure resource files are present
		File f1 = new File("./city.dat");
		if(f1 == null) {
			System.err.println("File not found: city.dat");
			System.exit(0);
		}
		File f2 = new File("./edge.dat");
		if(f2 == null) {
			System.err.println("File not found: edge.dat");
			System.exit(0);
		}
		Scanner scanner = new Scanner(System.in);
		System.out.print("Input first city: ");
		String city1Name = scanner.next();
		System.out.print("Input second city: ");
		String city2Name = scanner.next();
		scanner.close();
		city1 = cities.get(city1Name);
		city2 = cities.get(city2Name);
		// make sure input cities are present
		if(city1 == null) {
			System.err.println("No such city: " + city1Name);
			System.exit(0);
		}
		if(city2 == null) {
			System.err.println("No such city: " + city2Name);
			System.exit(0);
		}
		// do searches
		List<City> bfsRes = search.bfs(city1, city2);
		String bfsSRes = "\n" + search.getOutput("Breadth-First Search", bfsRes) + "\n";
		for(City city : cities.values()) {
			city.resetCity();
		}
		
		List<City> dfsRes = search.dfs(city1, city2);
		String dfsSRes = search.getOutput("Depth-First Search", dfsRes) + "\n";
		for(City city : cities.values()) {
			city.resetCity();
		}
		
		List<City> aStarRes = search.aStar(city1, city2);
		String aSRes = search.getOutput("A* Search", aStarRes);
		for(City city : cities.values()) {
			city.resetCity();
		}
		
		// print results to stdout
		System.out.print(bfsSRes);
		System.out.print(dfsSRes);
		System.out.print(aSRes);
			

	}

}
