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
	final static int STOP_DRAW_BORDERS_CELL_WIDTH = 3;
	final static int STOP_DRAW_BORDERS_CELL_HEIGHT = 3;

	final static int STOP_DRAW_TEXT_CELL_WIDTH = 20;
	final static int STOP_DRAW_TEXT_CELL_HEIGHT = 20;

	Color textColor = Color.BLUE;
	Color defaultGridBorderColor = Color.BLACK;
	Color defaultGridBorderActiveColor = Color.RED;
	Color defaultPathLineColor = Color.MAGENTA;

	private Color[][] gridColor;
	private Color[][] gridBorderColor;

	private GridMap map;
	private Node startNode;
	private Node endNode;

	private List<Node> path;

	MyMouseAdapter mouseAdapter; // mouse actions

	public Color getNodeColor(Node.Type type) 
	{ 
		Color color = Color.DARK_GRAY;

		switch (type) {
		case START:
			color = Color.GREEN;
			break;
		case END:
			color = Color.RED;
			break;
		case NORMAL:
			color = Color.WHITE;
			break;
		case OBSTACLE:
			color = new Color(85, 45, 0);
			break;
		case PATH:
			color = new Color(234, 250, 234);
			break;
		default:
			color = Color.MAGENTA;
			break;
		}

		return color;
	}

	public Color getVisualNodeColor(Node.VisualType visualType) 
	{ 
		Color color = Color.DARK_GRAY;

		switch (visualType) {
		case NORMAL:
			color = Color.DARK_GRAY;
			break;
		case PROCESSED:
			color = new Color(255, 248, 220);
			break;
		case FRONTIER:
			color = new Color(100, 149, 237);
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
		this.map = new GridMap();
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

		for (int i=0; i<rows; ++i)
		{
			for (int j=0; j<columns; ++j)
			{
				if (i < this.getCountRow() && j < this.getCountColumn())
				{
					mapNew[i][j] = new Node(this.getMapData()[i][j]);
					newGridColor[i][j] = new Color(this.gridColor[i][j].getRGB());
					newGridBorderColor[i][j] = new Color(gridBorderColor[i][j].getRGB());

					mapNew[i][j].setVisualType(Node.VisualType.NORMAL);
				}
				else
				{
					mapNew[i][j] = new Node(i, j, 0f);
					newGridColor[i][j] =  new Color(0, 0, 0);;
					newGridBorderColor[i][j] = new Color(defaultGridBorderColor.getRGB());
				}
			}
		}

		setCountRow(rows);
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

		this.map.setData(mapNew, getCountRow(), getCountColumn());
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

				Node node = this.map.getNode(i, j); 
				node.setType(Node.Type.NORMAL);
				node.setVisualType(Node.VisualType.NORMAL);
			}
		}
	}

	public void setDefaultVisualColors()
	{
		for (int i=0; i<this.getCountRow(); ++i)
		{
			for (int j=0; j<this.getCountColumn(); ++j)
			{
				Node node = this.map.getNode(i, j); 
				node.setVisualType(Node.VisualType.NORMAL);
			}
		}
	}

	public void SetDefaultValues()
	{
		for (int i=0; i<this.getCountRow(); ++i)
		{
			for (int j=0; j<this.getCountColumn(); ++j)
			{
				Node node = map.getNode(i, j);
				node.setDistance(0f);
				node.setStep(0);
				node.setParent(null);
			}
		}

		path = null;
	}

	public int getCountRow()
	{
		return this.map.getCountRow();
	}

	public int getCountColumn()
	{
		return this.map.getCountColumn();
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

		double nodeWidth = (double)getWidth() / getCountColumn();
		double nodeHeight = (double)getHeight() / getCountRow();

		int row, col;

		// fill
		for (row = 0; row < getCountRow(); row++) {
			for (col = 0; col < getCountColumn(); col++) {
				if (gridColor[row][col] != null) {
					if (map.getNode(row, col) != null) {
						Node node = map.getNode(row, col);
						Node.Type nodeType = node.getType();

						Color nodeColor = getNodeColor(Node.Type.NORMAL);

						if (nodeType != Node.Type.START && nodeType != Node.Type.END)
						{

							if (nodeType == Node.Type.NORMAL) {
								
								if (node.getVisualType() == Node.VisualType.PROCESSED) {
									Color tempNodeColor = getVisualNodeColor(node.getVisualType());
									int colorFromCost = 255 - node.getCost();
									float costWeight = 0.4f;
									int red = (int) (tempNodeColor.getRed() * (1-costWeight) + colorFromCost * costWeight);
									int green = (int) (tempNodeColor.getGreen() * (1-costWeight) + colorFromCost * costWeight);
									int blue = (int) (tempNodeColor.getBlue() * (1-costWeight) + colorFromCost * costWeight);
									
									red = (red > 255 ? 255 : red);
									green = (green > 255 ? 255 : green);
									blue = (blue > 255 ? 255 : blue);
									
									nodeColor = new Color(red, green, blue);
								}
								else if (node.getVisualType() == Node.VisualType.FRONTIER) {
									nodeColor = getVisualNodeColor(node.getVisualType());
								}
								else if (node.getVisualType() == Node.VisualType.NORMAL) {
									int colorFromCost = 255 - node.getCost();
									nodeColor = new Color(colorFromCost, colorFromCost, colorFromCost);									
								}								
							} 
							if (nodeType == Node.Type.OBSTACLE) { 
								nodeColor = getNodeColor(nodeType);
							}
						}

						drawFillNode(g, node, nodeColor);
					}
				}
			}
		}

		// path
		if (this.path != null && !this.path.isEmpty())
		{	
			Node node = path.get(0);
			Node nodePrev = null;

			Color nodeColor = getNodeColor(Node.Type.PATH);

			while (node != null) {

				int nodeRow = node.getPositionY();
				int nodeCol = node.getPositionX();

				if (nodeRow < getCountRow()-1 && nodeRow >= 0 && nodeCol < getCountColumn()-1 || nodeCol >= 0) // if resizing map
				{
					if (map.getNode(nodeRow, nodeCol) != null && map.getNode(nodeRow, nodeCol).getType() == Node.Type.NORMAL)
					{
						drawFillNode(g, node, nodeColor);
					}

					if (node.pathParent != null) {
						drawPath(g, node, node.pathParent, defaultPathLineColor);
					}

					if (nodePrev != null) {
						drawPath(g, node, nodePrev, defaultPathLineColor);
					}
				}

				nodePrev = node;
				node = node.pathParent;
			}
		}

		// start node
		drawFillNode(g, startNode, getNodeColor(Node.Type.START));

		// end node
		drawFillNode(g, endNode, getNodeColor(Node.Type.END));

		// draw step
		if (nodeWidth >= STOP_DRAW_TEXT_CELL_WIDTH && nodeHeight >= STOP_DRAW_TEXT_CELL_HEIGHT) {
			int x, y;
			for (row = 0; row < getCountRow(); row++) {
				for (col = 0; col < getCountColumn(); col++) {
					if (gridColor[row][col] != null) {
						x = (int)(col*nodeWidth);
						y = (int)(row*nodeHeight);

						Node.Type nodeType = map.getNode(row, col).getType();

						if (nodeType != Node.Type.OBSTACLE)	{
							String value = Integer.toString(map.getNode(row, col).getStep());
							g.setColor(textColor);
							g.drawString(value, x+5, y+15);
						}
					}
				}
			}
		}

		// border
		if (nodeWidth >= STOP_DRAW_BORDERS_CELL_WIDTH && nodeHeight >= STOP_DRAW_BORDERS_CELL_HEIGHT) {
			for (row = 0; row < getCountRow(); row++) {
				for (col = 0; col < getCountColumn(); col++) {
					if (gridBorderColor[row][col] != null)
					{					
						drawRectNode(g, map.getNode(row, col), gridBorderColor[row][col]);
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

			drawRectNode(g, map.getNode(selectedRow, selectedCol), gridBorderColor[selectedRow][selectedCol]);

			g2.setStroke(oldStroke);
		}
	}

	public GridMap getMap()
	{
		return this.map;
	}

	public Node[][] getMapData()
	{
		return this.map.getData();
	}

	private void setCountColumn(int columns)
	{
		this.map.setCountColumn(columns);
	}

	private void setCountRow(int rows)
	{
		this.map.setCountRow(rows);
	}

	public void setMap(Node[][] map)
	{
		this.map.setData(map, getCountRow(), getCountColumn());
	}

	public void setPath(List<Node> path)
	{
		this.path = path;
	}

	public void setCosts(int[][] costs, int rows, int columns)
	{			
		resizeMap(rows, columns);

		for (int i=0; i<rows; ++i)
		{
			for (int j=0; j<columns; ++j)
			{
				map.getNode(i, j).setCost(costs[i][j]);;
			}
		}		
	}

	public void updateObstacles(int lethal) 
	{
		for (int i=0; i<this.getCountRow(); ++i)
		{
			for (int j=0; j<this.getCountColumn(); ++j)
			{
				Node node = map.getNode(i, j);
				node.setType(node.getCost() >= lethal ? Node.Type.OBSTACLE : Node.Type.NORMAL);
			}
		}
	}
	
//	public int[][] getObstacles()
//	{
//		int obstacles[][] = new int[getCountRow()][getCountColumn()];
//		for (int i=0; i<getCountRow(); ++i)
//		{
//			for (int j=0; j<getCountColumn(); ++j)
//			{
//				if (map.getNode(i, j).getType() == Node.Type.OBSTACLE) {
//					obstacles[i][j] = 0;
//				}
//				else {
//					obstacles[i][j] = 255;
//				}
//			}
//		}
//
//		return obstacles;
//	}

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

	public void drawPath(Graphics g, Node center, Node from, Color pathColor)
	{
		final int drawOffset = 1; // observance overlapping lines on filled cells

		double nodeWidth = (double)getWidth() / getCountColumn();
		double nodeHeight = (double)getHeight() / getCountRow();	

		int centerRow = center.getPositionY();
		int centerCol = center.getPositionX();

		int fromRow = from.getPositionY();
		int fromCol = from.getPositionX();

		int x = (int)(centerCol*nodeWidth);
		int y = (int)(centerRow*nodeHeight);
		x += (int)nodeWidth/2;
		y += (int)nodeHeight/2;

		int x_f = x;
		int y_f = y;

		if (centerRow < getCountRow()-1 && centerRow >= 0 && centerCol < getCountColumn()-1 || centerCol >= 0) // if resizing map
		{
			if (map.getNode(centerRow, centerCol) != null && map.getNode(centerRow, centerCol).getType() == Node.Type.NORMAL)
			{					
				if (centerRow > fromRow && centerCol == fromCol) {		
					y_f -= (int)nodeHeight/2 - drawOffset;
				}
				else if (centerRow > fromRow && centerCol > fromCol) {
					x_f -= (int)nodeWidth/2;
					y_f -= (int)nodeHeight/2;
				}
				else if (centerRow == fromRow && centerCol > fromCol) {
					x_f -= (int)nodeWidth/2 - drawOffset;
				}
				else if (centerRow < fromRow && centerCol > fromCol) {
					x_f -= (int)nodeWidth/2;
					y_f += (int)nodeHeight/2;
				}
				else if (centerRow < fromRow && centerCol == fromCol) {
					y_f += (int)nodeHeight/2 - drawOffset;
				}
				else if (centerRow < fromRow && centerCol < fromCol) {
					x_f += (int)nodeWidth/2;
					y_f += (int)nodeHeight/2;
				}
				else if (centerRow == fromRow && centerCol < fromCol) {
					x_f += (int)nodeWidth/2 - drawOffset;
				}
				else if (centerRow > fromRow && centerCol < fromCol) {
					x_f += (int)nodeWidth/2;
					y_f -= (int)nodeHeight/2;
				}

				double thickness = 3;
				Graphics2D g2 = (Graphics2D) g;
				Stroke oldStroke = g2.getStroke();
				g2.setStroke(new BasicStroke((float) thickness));

				g.setColor(pathColor);	
				g.drawLine(x, y, x_f, y_f);

				g2.setStroke(oldStroke);
			}
		}
	}

	/// Mouse adapter
	class MyMouseAdapter extends MouseAdapter {
		MapView mapView;
		private int row;
		private int col;

		boolean isStartMove = false;
		boolean isEndMove = false;

		Node.Type clickedNodeType = Node.Type.NORMAL;

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

				// update selected square
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

			Node node = this.mapView.map.getNode(row, col); 

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
				node.setType(Node.Type.NORMAL);
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

			if (rowRel >= getCountRow() || rowRel < 0 || colRel >= getCountColumn() || colRel < 0)
			{
				return;
			}

			Node node = this.mapView.map.getNode(rowRel, colRel); 

			if (isStartMove)
			{	
				node.setType(Node.Type.NORMAL);
			}
			else if (isEndMove)
			{
				node.setType(Node.Type.NORMAL);
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

				// update selected square
				if (this.mapView.gridBorderColor[row][col] != null) 
				{
					this.mapView.gridBorderColor[row][col] = defaultGridBorderActiveColor;
				}

				Node node = this.mapView.map.getNode(row, col);

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