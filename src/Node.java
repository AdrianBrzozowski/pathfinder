public class Node {
	
	private static final double DEFAULT_VALUE = Double.MAX_VALUE;
	
	public enum Type {
	    START,
	    END,
	    NORMAL,
	    OBSTACLE,
	    PATH;
	}
	
	public enum VisualType {
		NORMAL,
		PROCESSED,
	    FRONTIER;
	}
	
	private int x, y;
	private int cost;
	
	// can be modified in algorithms
	private Type type;
	private VisualType visualType;
	
	private int step; // step in which this node was processed
	
	private double distance;
	private double value;

	Node pathParent; //used in search algorithm for construct path

	public Node()
	{
		this.x = 0;
		this.y = 0;
		this.distance = 0f;
		this.step = 0;
		this.cost = 0;
		pathParent = null;
		type = Type.NORMAL;
		visualType = VisualType.NORMAL;
		value = DEFAULT_VALUE;
	}

	public Node(int position_y, int position_x) 
	{
		this();
		this.x = position_x;
		this.y = position_y;
	}
	
	public Node(int position_y, int position_x, Float d) 
	{
		this();
		this.x = position_x;
		this.y = position_y;
		this.distance = d;
	}
	
	public Node(int position_y, int position_x, Type type) 
	{
		this();
		this.x = position_x;
		this.y = position_y;
		this.type = type;
	}

	public Node(Node node)
	{
		this.distance = node.distance;
		this.x = node.x;
		this.y = node.y;
		this.step = node.step;
		this.cost = node.cost;
		this.pathParent = node.pathParent;
		this.type = node.type;
		this.visualType = node.visualType;
		this.value = node.value;
	}

	public int getPositionX()
	{
		return this.x;
	}

	public int getPositionY()
	{
		return this.y;
	}

	public double getDistance()
	{
		return this.distance;
	}
	
	public int getStep()
	{
		return this.step;
	}
	
	public int getCost()
	{
		return this.cost;
	}
	
	public void setValue(double value)
	{
		this.value = value;
	}

	public void setDistance(double distance)
	{
		this.distance = distance;
	}
	
	public void setStep(int step)
	{
		this.step = step;
	}
	
	public void setCost(int cost)
	{
		this.cost = cost;
	}

	public void setType(Node.Type type)
	{
		this.type = type;
	}

	public void setVisualType(Node.VisualType visualType)
	{
		this.visualType = visualType;
	}
	
	public void setParent(Node node)
	{
		this.pathParent = node;
	}
	
	public Node.Type getType()
	{
		return this.type;
	}
	
	public Node.VisualType getVisualType()
	{
		return this.visualType;
	}
	
	public double getValue()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		return this.getPositionX() == ((Node) obj).getPositionX() && this.getPositionY() == ((Node) obj).getPositionY();
	}
	

    public String toString() {
        return "x: " + getPositionX() + ", y: " + getPositionY() + ", type: " + getType();
    }
}
