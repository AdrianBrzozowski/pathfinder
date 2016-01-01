import java.util.Iterator;
import java.util.LinkedList;

public class BreadthFirstSearch extends Algorithm {

	public BreadthFirstSearch(GridMap map, Node start, Node goal) 
	{
		super(map, start, goal);
	}

	@Override
	protected void work() 		
	{
		LinkedList<Node> closedList = new LinkedList<Node>();
		LinkedList<Node> openList = new LinkedList<Node>();

		openList.add(start);
		closedList.add(start);
		start.pathParent = null;

		while (!openList.isEmpty()) {
			Node node = openList.removeFirst();

			if (node.equals(goal)) {	
				finished = true;
				return;
			} else {
				closedList.add(node);
				node.setVisualType(Node.VisualType.PROCESSED);
				Iterator<Node> i = getNeighbors(node).iterator();

				while (i.hasNext()) {
					Node neighbour = i.next();

					if (!openList.contains(neighbour)) {
						if (!closedList.contains(neighbour)) {
							neighbour.setParent(node);
							openList.add(neighbour);
							
							neighbour.setStep(node.getStep() + 1);
							neighbour.setVisualType(Node.VisualType.FRONTIER);
						}
					}
				}
				this.notifyRefresh();
			}
		}

		failed = true;
	}

	@Override
	protected void discoverPath() {
		this.path = (LinkedList<Node>) constructPath(goal);
	}

}
