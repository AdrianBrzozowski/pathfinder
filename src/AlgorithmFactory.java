public class AlgorithmFactory {
	private static String[] algorithms = { 
			"BFS", 
			"Greedy BFS",
			"Dijkstra",
			"AStar"
	};

	public static Algorithm getInstance(String name, GridMap map, Node start, Node goal)
	{
		if(name == algorithms[0]) {
			return new BreadthFirstSearch(map, start, goal);
		} else if(name == algorithms[1]) {
			return new GreedyBestFirstSearch(map, start, goal);
		} else if(name == algorithms[2]) {
			return new Dijkstra(map, start, goal);
		} else if(name == algorithms[3]) {
			return new AStar(map, start, goal);
		}

		return null;
	}

	public static String[] getAlgorithmList(){
		return algorithms;
	}
}
