public class AlgorithmFactory {
	private static String[] algorithms = { 
			"BFS", 
			"Dijkstra" 
	};

	public static Algorithm getInstance(String name, GridMap map, Node start, Node goal)
	{
		if(name == algorithms[0]) {
			return new BreadthFirstSearch(map, start, goal);
		} else if(name == algorithms[1]) {
			return new Dijkstra(map, start, goal);
		}

		return null;
	}

	public static String[] getAlgorithmList(){
		return algorithms;
	}
}
