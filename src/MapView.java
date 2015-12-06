import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.List;
import java.awt.event.MouseAdapter;

import javax.swing.JPanel;

public class MapView extends JPanel  {
	private static final long serialVersionUID = 1L;

	// values
	Color textColor = Color.BLUE;
	Color defaultGridBorderColor = Color.BLACK;
	Color defaultGridBorderActiveColor = Color.RED;


	private Color[][] gridColor;
	private Color[][] gridBorderColor;
	private Node[][] map;
	private Node startNode;
	private Node endNode;

	private List<Node> path;

	private int rows;
	private int columns;

	MyMouseAdapter mouseAdapter; // mouse actions

	public Color getNodeColor(Node.Type type) 
	{ 
		Color color = Color.DARK_GRAY;

		switch (type) {
		case START:
			color = Color.ORANGE;
			break;
		case END:
			color = Color.RED;
			break;
		case FREE:
			color = Color.WHITE;
			break;
		case OBSTACLE:
			color = Color.GRAY;
			break;
		case PATH:
			color = Color.GREEN;
			break;
		default:
			color = Color.MAGENTA;
			break;
		}

		return color;
	}

	public MapView(int rows, int columns)
	{
		System.out.println("MapView()");
		this.resizeMap(rows, columns);

		SetDefaultValues();
		setDefaultColors();

		mouseAdapter = new MyMouseAdapter(this);
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);

		setStartNode(getCountRow()/2, getCountColumn()/3);
		setEndNode(getCountRow()/2, getCountColumn()*2/3);

		repaint();
	}

	public void resizeMap(int rows, int columns)
	{
		Node[][] mapNew = new Node[rows][columns];
		Color[][] newGridColor = new Color[rows][columns];
		Color[][] newGridBorderColor = new Color[rows][columns];

		Node start = getStartNode();
		Node end = getEndNode();

		if (start== null)
		{
			System.out.println("mamy start null");
		}

		for (int i=0; i<rows; ++i)
		{
			for (int j=0; j<columns; ++j)
			{
				if (i < this.getCountRow() && j < this.getCountColumn())
				{
					mapNew[i][j] = new Node(this.getMap()[i][j]);
					newGridColor[i][j] = new Color(this.gridColor[i][j].getRGB());
					newGridBorderColor[i][j] = new Color(gridBorderColor[i][j].getRGB());
				}
				else
				{
					mapNew[i][j] = new Node(i, j, 0f);
					newGridColor[i][j] =  new Color(0, 0, 0);;
					newGridBorderColor[i][j] = new Color(defaultGridBorderColor.getRGB());
				}
			}
		}

		setCountRows(rows);
		setCountColumn(columns);

		if (start !=null && (start.getPositionX() >= getCountColumn() || start.getPositionY() >= getCountRow()))
		{
			setStartNode(getCountRow()/2, getCountColumn()/3);
			setPath(null);
		}

		if (end != null && (end.getPositionX() >= getCountColumn() || end.getPositionY() >= getCountRow()))
		{
			setEndNode(getCountRow()/2, getCountColumn()*2/3);
			setPath(null);
		}

		this.map = mapNew;
		this.gridColor = newGridColor;
		this.gridBorderColor = newGridBorderColor;
	}

	public void setDefaultColors()
	{
		for (int i=0; i<this.getCountRow(); ++i)
		{
			for (int j=0; j<this.getCountColumn(); ++j)
			{
				this.gridBorderColor[i][j] = defaultGridBorderColor;

				Node node = this.map[i][j]; 
				node.setType(Node.Type.FREE);
			}
		}
	}

	public void SetDefaultValues()
	{
		for (int i=0; i<this.getCountRow(); ++i)
		{
			for (int j=0; j<this.getCountColumn(); ++j)
			{
				Node node = this.map[i][j]; 
				node.setDistance(0f);
			}
		}

		path = null;
	}

	public int getCountRow()
	{
		return this.rows;
	}

	public int getCountColumn()
	{
		return this.columns;
	}

	public int findRow(int pixelY) 
	{
		return (int)(((double)pixelY)/getHeight()*getCountRow());
	}

	public int findColumn(int pixelX) 
	{
		return (int)(((double)pixelX)/getWidth()*getCountColumn());
	}

	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		// draw background
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(),getHeight());

		int row, col;

		// fill
		for (row = 0; row < getCountRow(); row++) {
			for (col = 0; col < getCountColumn(); col++) {
				if (gridColor[row][col] != null) {
					if (map[row][col] != null) {
						Node node = getNode(row, col);
						Node.Type nodeType = node.getType();

						Color nodeColor = getNodeColor(Node.Type.FREE);

						if (nodeType != Node.Type.START && nodeType != Node.Type.END)
						{
							nodeColor = getNodeColor(nodeType);
						}

						drawFillNode(g, node, nodeColor);
					}
				}
			}
		}

		// path
		if (this.path != null)
		{	
			Node node = path.get(0);

			Color nodeColor = getNodeColor(Node.Type.PATH);

			while (node != null) {

				int nodeRow = node.getPositionY();
				int nodeCol = node.getPositionX();

				if (nodeRow < getCountRow()-1 && nodeRow >= 0 && nodeCol < getCountColumn()-1 || nodeCol >= 0) // if resizing map
				{
					if (getNode(nodeRow, nodeCol) != null && getNode(nodeRow, nodeCol).getType() == Node.Type.FREE)
					{
						drawFillNode(g, node, nodeColor);
					}
				}

				node = node.pathParent;
			}
		}

		// start node
		drawFillNode(g, startNode, getNodeColor(Node.Type.START));

		// end node
		drawFillNode(g, endNode, getNodeColor(Node.Type.END));

		//		// text
		//		int x, y;
		//		for (row = 0; row < getCountRow(); row++) {
		//			for (col = 0; col < getCountColumn(); col++) {
		//				if (gridColor[row][col] != null) {
		//					double nodeWidth = (double)getWidth() / getCountColumn();
		//					double nodeHeight = (double)getHeight() / getCountRow();
		//					x = (int)(col*nodeWidth);
		//					y = (int)(row*nodeHeight);
		//
		//					Node.Type nodeType = map[row][col].getType();
		//
		//					if (nodeType != Node.Type.OBSTACLE)
		//					{
		//						String value = Float.toString(map[row][col].getDistance());
		//						g.setColor(textColor);
		//						g.drawString(value, x+5, y+15);
		//					}
		//				}
		//			}
		//		}

		// border
		if (getCountRow() < 150 && getCountColumn() < 150) {
			for (row = 0; row < getCountRow(); row++) {
				for (col = 0; col < getCountColumn(); col++) {
					if (gridBorderColor[row][col] != null)
					{					
						drawRectNode(g, getNode(row, col), gridBorderColor[row][col]);
					}
				}
			}
		}

		// draw selected square
		int selectedRow = this.mouseAdapter.getMousePositionOnRow();
		int selectedCol = this.mouseAdapter.getMousePositionOnColumn();

		if (selectedRow < getCountRow() && selectedCol < getCountColumn())
		{
			double thickness = 3;
			Graphics2D g2 = (Graphics2D) g;
			Stroke oldStroke = g2.getStroke();
			g2.setStroke(new BasicStroke((float) thickness));

			drawRectNode(g, getNode(selectedRow, selectedCol), gridBorderColor[selectedRow][selectedCol]);

			g2.setStroke(oldStroke);
		}
	}

	public Node[][] getMap()
	{
		return map;
	}

	private void setCountColumn(int columns)
	{
		this.columns = columns;
	}

	private void setCountRows(int rows)
	{
		this.rows = rows;
	}

	public void setMap(Node[][] map)
	{
		this.map = map;
	}

	public void setPath(List<Node> path)
	{
		this.path = path;
	}

	public void setObstacles(int[][] obstacles, int rows, int columns)
	{			
		resizeMap(rows, columns);

		for (int i=0; i<rows; ++i)
		{
			for (int j=0; j<columns; ++j)
			{
				map[i][j].setType(obstacles[i][j] < 240 ? Node.Type.OBSTACLE : Node.Type.FREE);
			}
		}
	}

	public int[][] getObstacles()
	{
		int obstacles[][] = new int[getCountRow()][getCountColumn()];
		for (int i=0; i<getCountRow(); ++i)
		{
			for (int j=0; j<getCountColumn(); ++j)
			{
				if (getNode(i, j).getType() == Node.Type.OBSTACLE) {
					obstacles[i][j] = 0;
				}
				else {
					obstacles[i][j] = 255;
				}
			}
		}

		return obstacles;
	}

	public void setStartNode(int row, int column)
	{
		if (row < 0 || row >= getCountRow() || column < 0 || column >= getCountColumn())
		{
			return;
		}

		startNode = new Node(row, column, 0.f);
		//		startNode.setType(Node.Type.START);
	}

	public void setEndNode(int row, int column)
	{
		if (row < 0 || row >= getCountRow() || column < 0 || column >= getCountColumn())
		{
			return;
		}

		endNode = new Node(row, column, 0.f);
		//		endNode.setType(Node.Type.END);
	}

	public Node getStartNode()
	{
		return startNode;
	}

	public Node getEndNode()
	{			
		return endNode;
	}

	public Node getNode(int row, int column)
	{
		if (row >= getCountRow() || row < 0 || column >= getCountColumn() || column < 0)
		{
			return null;
		}

		return this.map[row][column];
	}

	public void drawFillNode(Graphics g, Node node, Color nodeColor)
	{
		double nodeWidth = (double)getWidth() / getCountColumn();
		double nodeHeight = (double)getHeight() / getCountRow();

		int x = (int)(node.getPositionX()*nodeWidth);
		int y = (int)(node.getPositionY()*nodeHeight);

		g.setColor(nodeColor);
		g.fillRect(x, y, (int)nodeWidth+1, (int)nodeHeight+1);
	}

	public void drawRectNode(Graphics g, Node node, Color nodeColor)
	{
		double nodeWidth = (double)getWidth() / getCountColumn();
		double nodeHeight = (double)getHeight() / getCountRow();

		int x = (int)(node.getPositionX()*nodeWidth);
		int y = (int)(node.getPositionY()*nodeHeight);

		g.setColor(nodeColor);
		g.drawRect(x, y, (int)nodeWidth, (int)nodeHeight);
	}

	/// Mouse adapter
	class MyMouseAdapter extends MouseAdapter {
		MapView mapView;
		private int row;
		private int col;

		boolean isStartMove = false;
		boolean isEndMove = false;

		Node.Type clickedNodeType = Node.Type.FREE;

		public int getMousePositionOnRow()
		{
			return row;
		}

		public int getMousePositionOnColumn()
		{
			return col;
		}

		public MyMouseAdapter(MapView mapView)
		{
			this.mapView = mapView;
		}

		@Override
		public void mouseMoved(MouseEvent e) 
		{
			int newRow = mapView.findRow( e.getY() );
			int newCol = mapView.findColumn( e.getX() );

			if (this.row != newRow || this.col != newCol)
			{
				this.row = newRow;
				this.col = newCol;

				// draw borders
				for (int rowIdx = 0; rowIdx < this.mapView.getCountRow(); rowIdx++) {
					for (int colIdx = 0; colIdx < this.mapView.getCountColumn(); colIdx++) {
						if (this.mapView.gridBorderColor[rowIdx][colIdx] != null) 
						{
							this.mapView.gridBorderColor[rowIdx][colIdx] = defaultGridBorderColor;
						}
					}
				}

				// draw selected square
				if (this.mapView.gridBorderColor[row][col] != null) 
				{
					this.mapView.gridBorderColor[row][col] = defaultGridBorderActiveColor;
				}

				this.mapView.repaint();
			}
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			this.row = mapView.findRow( e.getY() );
			this.col = mapView.findColumn( e.getX() );

			Node node = this.mapView.map[row][col]; 

			if (node.equals(getStartNode()))
			{
				isStartMove = true;
			}
			else if (node.equals(getEndNode()))
			{
				isEndMove = true;
			}
			else if (node.getType() == Node.Type.OBSTACLE)
			{	
				node.setType(Node.Type.FREE);
				clickedNodeType = node.getType();
			}
			else if (node.getType() != Node.Type.OBSTACLE)
			{	

				node.setType(Node.Type.OBSTACLE);
				clickedNodeType = node.getType();
			}

			mapView.repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) 
		{
			int rowRel = mapView.findRow( e.getY() );
			int colRel = mapView.findColumn( e.getX() );

			Node node = this.mapView.map[rowRel][colRel]; 

			if (isStartMove)
			{	
				node.setType(Node.Type.FREE);
			}
			else if (isEndMove)
			{
				node.setType(Node.Type.FREE);
			}

			isStartMove = false;
			isEndMove = false;

			mapView.repaint();
		};

		@Override
		public void mouseDragged(MouseEvent e) 
		{
			int newRow = mapView.findRow( e.getY() );
			int newCol = mapView.findColumn( e.getX() );

			if (newRow >= getCountRow() || newRow < 0 || newCol >= getCountColumn() || newCol < 0)
			{
				return;
			}

			if (this.row != newRow || this.col != newCol)
			{

				this.row = newRow;
				this.col = newCol;

				// draw borders
				for (int rowIdx = 0; rowIdx < this.mapView.getCountRow(); rowIdx++) {
					for (int colIdx = 0; colIdx < this.mapView.getCountColumn(); colIdx++) {
						if (this.mapView.gridBorderColor[rowIdx][colIdx] != null) 
						{
							this.mapView.gridBorderColor[rowIdx][colIdx] = defaultGridBorderColor;
						}
					}
				}

				// draw selected square
				if (this.mapView.gridBorderColor[row][col] != null) 
				{
					this.mapView.gridBorderColor[row][col] = defaultGridBorderActiveColor;
				}

				Node node = this.mapView.map[row][col];

				if (isStartMove)
				{
					setStartNode(row, col);
					setPath(null);
				}
				else if (isEndMove)
				{
					setEndNode(row, col);
					setPath(null);
				}
				else if (e.getModifiers() == MouseEvent.BUTTON1_MASK) 
				{
					if (node.getType() != Node.Type.START && node.getType() != Node.Type.END)
						node.setType(clickedNodeType);
				}

				this.mapView.repaint();
			}
		}
	}
}