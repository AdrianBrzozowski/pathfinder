import java.util.Iterator;
import java.util.LinkedList;

public class Dijkstra extends Algorithm {

	public Dijkstra(GridMap map, Node start, Node goal) 
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
		
		LinkedList<Node> frontier = new LinkedList<>();
		frontier.add(start);
		start.setDistance(0.0f);
		
		while (!frontier.isEmpty()) {
			Node node = getNodeMinCost(frontier);
			frontier.remove(node);
			
			if (node.equals(goal)) {	
				finished = true;
				return;
			}
			
			node.setVisualType(Node.VisualType.PROCESSED);
			
			Iterator<Node> i = getNeighbors(node).iterator();
			
			while (i.hasNext()) {
				Node neighbour = i.next();
				double dist = node.getDistance() + neighbour.getCost();
				if (dist < neighbour.getDistance()) {
					neighbour.setDistance(dist);
					neighbour.setParent(node);
					frontier.add(neighbour);
					
					neighbour.setStep(node.getStep() + 1);
					neighbour.setVisualType(Node.VisualType.FRONTIER);
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
	
	public Node getNodeMinCost(LinkedList<Node> set)
	{
		Node bestNode = null;
		int bestCost = Integer.MAX_VALUE;
		
		for (Node node : set) {
			if (node.getCost() < bestCost) {
				bestCost = node.getCost();
				bestNode = node;
			}
		}
		
		return bestNode;
	}
}