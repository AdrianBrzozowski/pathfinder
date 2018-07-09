
import java.util.Iterator;
import java.util.LinkedList;

public class GreedyBestFirstSearch extends Algorithm {

	public GreedyBestFirstSearch(GridMap map, Node start, Node goal) 
	{
		super(map, start, goal);
	}

	@Override
	protected void work() 		
	{
		for (int i=0; i<map.getCountColumn(); ++i) {
			for (int j=0; j<map.getCountRow(); ++j) {
				map.getNode(j, i).setDistance(Float.MAX_VALUE);
				map.getNode(j, i).setStep(0);
			}
		}

		LinkedList<Node> closedList = new LinkedList<Node>();
		LinkedList<Node> frontier = new LinkedList<>();

		frontier.add(start);
		closedList.add(start);
		start.setDistance(calculateDistance(start, goal));

		while (!frontier.isEmpty() && failed == false) {
			Node node = getNodeMinDistance(frontier);

			closedList.add(node);
			frontier.remove(node);

			if (node.equals(goal)) {	
				finished = true;
				return;
			}

			node.setVisualType(Node.VisualType.PROCESSED);

			Iterator<Node> i = getNeighbors(node).iterator();

			while (i.hasNext()) {
				Node neighbour = i.next();

				if (!frontier.contains(neighbour)) {

					if (!closedList.contains(neighbour)) {
						neighbour.setDistance(calculateDistance(neighbour, goal));
						neighbour.setParent(node);
						frontier.add(neighbour);

						neighbour.setStep(node.getStep() + 1);
						neighbour.setVisualType(Node.VisualType.FRONTIER);
					}
				}
			}
			this.notifyRefresh();
		}
		failed = true;
	}

	@Override
	protected void discoverPath() {
		this.path = (LinkedList<Node>) constructPath(goal);
	}

	public Node getNodeMinDistance(LinkedList<Node> set)
	{
		Node bestNode = null;
		double bestDistance = Double.MAX_VALUE;

		for (Node node : set) {
			if (node.getDistance() < bestDistance) {
				bestDistance = node.getDistance();
				bestNode = node;
			}
		}

		return bestNode;
	}

	protected double calculateDistance(Node n1, Node n2){
		int xVektor = n1.getPositionX() - n2.getPositionX();
		int yVektor = n1.getPositionY() - n2.getPositionY();

		if(xVektor == 0 && yVektor == 0) return 0;

		return Math.sqrt((xVektor*xVektor) + (yVektor*yVektor));
	}
}

