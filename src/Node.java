public class Node {
	public enum Type {
	    START,
	    END,
	    FREE,
	    OBSTACLE,
	    PATH;
	}
	
	private int x, y;
	private Float distance;

	Type type;
	Node pathParent; //used in search algorithm for construct path

	public Node()
	{
		this.x = 0;
		this.y = 0;
		this.distance = 0f;
		pathParent = null;
		type = Type.FREE;
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
		this.distance = node.getDistance();
		this.x = node.getPositionX();
		this.y = node.getPositionY();
		this.pathParent = node.pathParent;
		this.type = node.type;
	}

	public int getPositionX()
	{
		return x;
	}

	public int getPositionY()
	{
		return y;
	}

	public Float getDistance()
	{
		return distance;
	}

	public void setDistance( Float distance)
	{
		this.distance = distance;
	}

	public void setType(Node.Type type)
	{
		this.type = type;
	}
	
	public Node.Type getType()
	{
		return this.type;
	}

	@Override
	public boolean equals(Object obj)
	{
		return this.getPositionX() == ((Node) obj).getPositionX() && this.getPositionY() == ((Node) obj).getPositionY();
	}
}
