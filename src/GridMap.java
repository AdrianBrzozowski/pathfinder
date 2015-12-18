
public class GridMap {
	private int rows;
	private int columns;
	private Node[][] data;

	public GridMap() 
	{
		this.rows = 0;
		this.columns = 0;
	}
	
	public GridMap(int rows, int columns) 
	{
		this.rows = rows;
		this.columns = columns;
		this.data = new Node[rows][columns];

		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				this.data[i][j] = new Node(j, i, 0f);
			}
		}
	}

	public GridMap(int rows, int columns, Integer[][] obstacles) 
	{
		this(rows, columns);

		for (int i = 0; i < obstacles.length; ++i) {
			for (int j = 0; j < obstacles[0].length; ++j)
			{
				data[i][j].setType(obstacles[i][j] == 1 ? Node.Type.OBSTACLE : Node.Type.FREE);
			}
		}
	}

	public void resize(int rows, int columns) 
	{
		Node[][] newData = new Node[rows][columns];

		// TODO: if new size smaller start from old size
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				newData[i][j] = new Node(j, i, 0f);
			}
		}

		this.rows = rows;
		this.columns = columns;
		this.data = newData;
	}

	/// getters
	public int getCountRow() 
	{
		return rows;
	}

	public int getCountColumn() 
	{
		return columns;
	}

	public Node[][] getData() 
	{
		return this.data;
	}
	
	public Node getNode(int row, int column)
	{
		if (row >= getCountRow() || row < 0 || column >= getCountColumn() || column < 0)
		{
			return null;
		}

		return this.getData()[row][column];
	}

	/// setters
	public void setMap(GridMap map) 
	{
		this.setData(map.getData(), map.getCountRow(), map.getCountColumn());
	}
	
	public void setData(Node[][] map, int rows, int columns) 
	{
		if (rows != this.getCountRow() || columns != this.getCountColumn()) {
		 	resize(rows, columns);
		}
		
		this.data = map;
	}
	
	public void setCountRow(int rows)
	{
		this.resize(rows, getCountColumn());
	}
	
	public void setCountColumn(int columns)
	{
		this.resize(getCountRow(), columns);
	}
}
