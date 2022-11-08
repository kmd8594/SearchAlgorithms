import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Author: Kaitlyn DeCola
 * Holds the information about a city and the edges of the cities
 */
public class City {

	private String name;
	private double lat;
	private double lon;
	private List<City> edges;
	private City parent;
	// used for A* 
	private double g;
	private double h;
	
	
	public City(String name, double lat, double lon) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.edges = new ArrayList<>();
		this.parent = null;
		this.g = 0.0;
		this.h = 0.0;
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getLat() {
		return this.lat;
	}
	
	public double getLon() {
		return this.lon;
	}
	
	public List<City> getEdges(){
		return this.edges;
	}
	
	public void addEdge(City city) {
		edges.add(city);
		edges.sort(new SortByName());
	}
	
	public void setParent(City city) {
		this.parent = city;
	}
	
	public City getParent() {
		return this.parent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof City) {
			City city = (City) obj;
			if(city.name.equals(this.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public void setG(double g) {
		this.g = g;
	}
	
	public double getG() {
		return this.g;
	}
	
	public void setH(double h) {
		this.h = h;
	}
	
	public double getH() {
		return this.h;
	}
	
	/**
	 * resets information for A* and the parent city
	 */
	public void resetCity() {
		this.h = 0.0;
		this.g = 0.0;
		this.parent = null;
	}
	
}

/**
 * classes used to sort cities by name
 */
class SortByName implements Comparator<City>{
	public int compare(City city1, City city2) {
		return city1.getName().compareTo(city2.getName());
	}
}
