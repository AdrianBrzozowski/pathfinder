import java.util.List;

public class PathFinderModel {

	private GridMap map;
	private Algorithm algorithm;
	private AlgorithmProperty algorithmProp;
	
	private AlgorithmActionListener algorithmListener;

	public PathFinderModel() 
	{
		System.out.println("Model()");
		this.map = new GridMap();
		this.algorithmProp = new AlgorithmProperty();
	}

	public GridMap getMap() 
	{
		return map;
	}

	public void setMap(GridMap map) 
	{
		this.map.setMap(map);
	}
	
	public void setNeighboursCount(int neighboursCount)
	{
		algorithmProp.setDirsCount(neighboursCount);
	}

	public int getCountRow() 
	{
		return map.getCountRow();
	}

	public int getCountColumn() 
	{
		return map.getCountColumn();
	}

	public void addAlgorithmListener(AlgorithmActionListener algorithmListener) {
		this.algorithmListener = algorithmListener;
	}

	public void startAlgorithm(String algorithmName, Node start, Node goal) {

		this.algorithm = AlgorithmFactory.getInstance(algorithmName, getMap(), start, goal);
		
		if (this.algorithm != null) {
			this.algorithm.setProperty(algorithmProp);
			this.algorithm.setListener(algorithmListener);
			this.algorithm.start();
		}
	}

	public List<Node> getPath() 
	{
		return algorithm.getPath();
	}
}