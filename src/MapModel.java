import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MapModel {

	// final def
	final static int COST_STEP = 1; // TODO: get from view
	final static int DIRS_COUNT = 4;
	public int DIRS[][] = { { 1, 0, -1, 0 }, { 0, -1, 0, 1 } };
	// final static int DIRS_COUNT = 8;
	// public int DIRS[][] = {
	// {1, 1, 0, -1, -1, -1, 0, 1},
	// {0, -1, -1, -1, 0, 1, 1, 1}
	// };
	private int width;
	private int height;
	private Node[][] map;

	public MapModel(int height, int width) 
	{
		System.out.println("MapModel()");

		this.width = width;
		this.height = height;
		this.map = new Node[height][width];

		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				this.map[i][j] = new Node(i, j, 0f);
			}
		}
	}

	public MapModel(int height, int width, Integer[][] obstacles) 
	{
		this(height, width);

		for (int i = 0; i < obstacles.length; ++i) {
			for (int j = 0; j < obstacles[0].length; ++j)
			{
				map[i][j].setType(obstacles[i][j] == 1 ? Node.Type.OBSTACLE : Node.Type.FREE);
			}
		}
	}

	public void resize(int height, int width) 
	{

		Node[][] mapNew = new Node[height][width];

		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				mapNew[i][j] = new Node(i, j, 0f);
			}
		}

		this.width = width;
		this.height = height;
		this.map = mapNew;
	}

	public List<Node> breadthFirstSerach(Node startNode, Node goalNode) throws IOException 
	{
		LinkedList<Node> closedList = new LinkedList<Node>();
		LinkedList<Node> openList = new LinkedList<Node>();

		Node start = getNode(startNode.getPositionX(), startNode.getPositionY());
		Node end = getNode(goalNode.getPositionX(), goalNode.getPositionY());
		
		openList.add(start);
		closedList.add(start);
		start.pathParent = null;

		while (!openList.isEmpty()) {
			Node node = openList.removeFirst();
//			System.out.println("Checking node " + node.getPositionX() + " : " + node.getPositionY());

			if (node.equals(end)) {
//				System.out.println("######### znaleziono cel");
				return constructPath(end);
			} else {
				closedList.add(node);
				Iterator<Node> i = getNeighbors(node).iterator();

//				System.out.print("Neighbour list: ");
				while (i.hasNext()) {
//					Node neighbour = 
							i.next();
//					System.out.print(" (" + neighbour.getPositionX() + " : " + neighbour.getPositionY() + ")");
				}
//				System.out.println("");

				i = getNeighbors(node).iterator();

				while (i.hasNext()) {
					Node neighbour = i.next();

					if (!openList.contains(neighbour)) {
						if (!closedList.contains(neighbour)) {
							neighbour.pathParent = node;
							neighbour.setDistance(node.getDistance() + COST_STEP);
							openList.add(neighbour);
//							System.out.println("Do open list dodano (" + neighbour.getPositionX() + " : "
//									+ neighbour.getPositionY() + ")");
						} else {
//							System.out.println("present in closedList");
						}
					} else {
//						System.out.println("present in openList");
					}
				}
			}
		}

		System.out.println("KONIEC");

		return null;
	}

	protected List<Node> constructPath(Node node) 
	{
		List<Node> path = new LinkedList<Node>();

		System.out.print("Sciezka:");
		while (node.pathParent != null) {
			System.out.print(" (" + node.getPositionX() + " : " + node.getPositionY() + ")");
			path.add(node);

			node = node.pathParent;
		}
		return path;
	}

	public LinkedList<Node> getNeighbors(Node node) 
	{
		LinkedList<Node> neighbors = new LinkedList<Node>();

//		System.out.println("Generowanie listy sasiadow");

		for (int i = 0; i < DIRS_COUNT; ++i) {
			int posX = node.getPositionX() + DIRS[0][i];
			int posY = node.getPositionY() + DIRS[1][i];

			if (inBounds(posX, posY)) {
				Node neightbour = getNode(posX, posY);
				if (isPassable(neightbour)) {
					neighbors.add(neightbour);
				}

			}
		}
		return neighbors;
	}

	public boolean inBounds(int x, int y) 
	{
		return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
	}

	public boolean isPassable(Node node) 
	{
//		if (node.getType() == Node.Type.OBSTACLE)
//			System.out.println("# # # # # JEST TO PRZESZKODA: " + node.getPositionX() + " " + node.getPositionY());

		return !(node.getType() == Node.Type.OBSTACLE);
	}

	public Node getNode(int x, int y) 
	{
		return map[y][x];
	}

	/// getters
	public int getWidth() 
	{
		return width;
	}

	public int getHeight() 
	{
		return height;
	}

	public Node[][] getMap() 
	{
		return map;
	}

	public void setMap(Node[][] map) 
	{
		this.map = map;
	}
}
