import java.util.Iterator;
import java.util.LinkedList;

public class AStar extends Algorithm {

	public AStar(GridMap map, Node start, Node goal) 
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
		
		while (!frontier.isEmpty() && failed == false) {
			Node node = getNodeTheBest(frontier);
			frontier.remove(node);
			
			if (node.equals(goal)) {	
				finished = true;
				return;
			}
			
			node.setVisualType(Node.VisualType.PROCESSED);
			
			Iterator<Node> i = getNeighbors(node).iterator();
			
			while (i.hasNext()) {
				Node neighbour = i.next();
				
				if (frontier.contains(neighbour)) continue;
				
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
	
	public Node getNodeTheBest(LinkedList<Node> set)
	{
		Node bestNode = null;
		double bestValue = Double.MAX_VALUE;
		
		for (Node node : set) {
			double nodeValue = node.getDistance() + calculateDistance(node, goal);
			if (nodeValue < bestValue) {
				bestValue = nodeValue;
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
