public class AlgorithmFactory {
	private static String[] algorithms = { 
			"BFS", 
	"nonono" };

	public static Algorithm getInstance(String name, GridMap map, Node start, Node goal)
	{
		if(name == algorithms[0]) {
			return new BreadthFirstSearch(map, start, goal);
			//		} else if(name == algorithms[1]) {
			//		}
		}

		return null;
	}
	
	public static String[] getAlgorithmList(){
		return algorithms;
	}
}
