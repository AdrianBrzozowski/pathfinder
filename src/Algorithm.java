import java.util.LinkedList;
import java.util.List;

public abstract class Algorithm extends Thread {

	// final def
	final static int COST_STEP = 1; // TODO: get from view
	public int DIRS[][] = {
			{1, 0, -1, 0, 1, -1, -1, 1},
			{0, -1, 0, 1, -1, -1, 1, 1}
	};

	protected GridMap map;
	protected Node start;
	protected Node goal;

	protected boolean finished = false;
	protected boolean failed = false;

	protected AlgorithmProperty property;

	private AlgorithmActionListener listener;

	LinkedList<Node> path;

	public Algorithm(GridMap map, Node start, Node goal)
	{		
		this.map = map;
		this.start = map.getNode(start.getPositionY(), start.getPositionX());;
		this.goal = map.getNode(goal.getPositionY(), goal.getPositionX());
	}

	@Override
	public void run() {
		super.run();

		long startTime = System.currentTimeMillis();
		work();
		long endTime   = System.currentTimeMillis();

		discoverPath();
		notifyFinished();
		
		if (finished) {
			notifyTimeExecution(Long.toString(endTime - startTime));
		}
	}

	public LinkedList<Node> getNeighbors(Node node) 
	{
		LinkedList<Node> neighbors = new LinkedList<Node>();

		for (int i = 0; i < getDirsCount(); ++i) {
			int posX = node.getPositionX() + DIRS[0][i];
			int posY = node.getPositionY() + DIRS[1][i];

			if (inBounds(posX, posY)) {
				Node neightbour = map.getNode(posY, posX);
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
		return !(node.getType() == Node.Type.OBSTACLE);
	}

	/// getters
	public int getWidth() 
	{
		return map.getCountColumn(); // TODO
	}

	public int getHeight() 
	{
		return map.getCountRow(); // TODO
	}

	public int getDirsCount()
	{
		return property.getDirsCount();
	}

	public List<Node> getPath()
	{
		return this.path;
	}

	/// setters
	public void setListener(AlgorithmActionListener listener)
	{
		this.listener = listener;
	}

	private void notifyFinished() 
	{
		if (listener != null) {
			listener.finished();
		}
	}

	private void notifyTimeExecution(String duration) 
	{
		if (listener != null) {
			listener.setTimeExecution(duration);
		}
	}
	
	private void notifyPathLenght(String pathLengt) 
	{
		if (listener != null) {
			listener.setPathLength(pathLengt);
		}
	}

	protected void notifyRefresh()
	{
		if (listener != null) {
			listener.refresh();
		}
	}

	public void setDirsCount(int dirsCount)
	{
		this.property.setDirsCount(dirsCount);
	}

	public void setProperty(AlgorithmProperty property)
	{
		this.property = property;
	}

	protected List<Node> constructPath(Node node) 
	{
		if (!finished) {
			return null;
		}

		List<Node> newPath = new LinkedList<Node>();
		int count = 0;

		while (node.pathParent != null) {
			newPath.add(node);

			node = node.pathParent;
			++count;
		}

		notifyPathLenght(Integer.toString(count));
		
		return newPath;
	}

	protected abstract void work();
	protected abstract void discoverPath();
}
