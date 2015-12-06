import java.io.IOException;
import java.util.List;

public class PathFinderModel {

	private MapModel mapModel;

	public PathFinderModel() 
	{
		System.out.println("Model()");
		mapModel = new MapModel(0, 0);
	}	

	public Node[][] getMap()
	{
		return mapModel.getMap();
	}

	public void setMap(Node[][] map)
	{
		this.mapModel.setMap(map);
	}

	public List<Node> runBreadthFirstSerach(Node startNode, Node goalNode)
	{
		try {
			return mapModel.breadthFirstSerach(startNode, goalNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public int getCountRow()
	{
		return mapModel.getHeight();
	}

	public int getCountColumn()
	{
		return mapModel.getWidth();
	}

	public void resize(int height, int width)
	{
		this.mapModel.resize(height, width);
	}
}