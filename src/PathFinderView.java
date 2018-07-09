import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PathFinderView extends JFrame {

	private static final long serialVersionUID = 1L;

	final static int INIT_ROW_COUNT_MAP = 16;
	final static int INIT_COL_COUNT_MAP = 16;

	// buttons
	final static String START_BUTTON_NAME = "Start";
	final static String STOP_BUTTON_NAME = "Stop";
	final static String CLEAR_BUTTON_NAME = "Clear";

	// spinners
	final static int ROW_COUNT_DEFAULT = INIT_ROW_COUNT_MAP;
	final static int ROW_COUNT_MIN = 1;
	final static int ROW_COUNT_MAX = 8192;
	final static int ROW_COUNT_STEP = 1;

	final static int COL_COUNT_DEFAULT = INIT_COL_COUNT_MAP;
	final static int COL_COUNT_MIN = 1;
	final static int COL_COUNT_MAX = 8192;
	final static int COL_COUNT_STEP = 1;
	
	final static int LETHAL_COST_DEFAULT = 250;
	final static int LETHAL_COST_MIN = 0;
	final static int LETHAL_COST_MAX = 256;
	final static int LETHAL_COST_STEP = 1;

	// mapView
	final static int PREFERED_SIZE_WIDTH = 640;
	final static int PREFERED_SIZE_HEIGHT = 640;

	// menu
	final static String MENU_MAP_NAME = "File";
	final static String MENU_NEW_MAP_NAME = "New map";
	final static String MENU_OPEN_MAP_NAME = "Open map";
	final static String MENU_SAVE_MAP_NAME = "Save map";

	// create objects
	private MapView mapView = new MapView(INIT_ROW_COUNT_MAP, INIT_COL_COUNT_MAP);
	private JButton startButton = new JButton(START_BUTTON_NAME);
	private JButton stopButton = new JButton(STOP_BUTTON_NAME);
	private JButton clearButton = new JButton(CLEAR_BUTTON_NAME);
	
	private SpinnerModel rowCountModelSpinner = new SpinnerNumberModel(ROW_COUNT_DEFAULT, ROW_COUNT_MIN, ROW_COUNT_MAX,
			ROW_COUNT_STEP);
	private JSpinner rowCountSpinner = new JSpinner(rowCountModelSpinner);
	
	private SpinnerModel colCountModelSpinner = new SpinnerNumberModel(COL_COUNT_DEFAULT, COL_COUNT_MIN, COL_COUNT_MAX,
			COL_COUNT_STEP);
	private JSpinner colCountSpinner = new JSpinner(colCountModelSpinner);
	
	private JComboBox<Object> algortihmsComboBox = new JComboBox<Object>(AlgorithmFactory.getAlgorithmList());
	
	String[] neightboursCountStrings = {"4", "8"};
	private JComboBox<Object> neighboursCountComboBox = new JComboBox<Object>(neightboursCountStrings);
	
	private SpinnerModel lethalCostModelSpinner = new SpinnerNumberModel(LETHAL_COST_DEFAULT, LETHAL_COST_MIN, LETHAL_COST_MAX,
			LETHAL_COST_STEP);
	protected JSpinner lethalCostSpinner = new JSpinner(lethalCostModelSpinner);
	
	JLabel timeAlgorithmExecution = new JLabel(" - ");
	JLabel pathLenghtAlgorithm = new JLabel(" - ");

	class SpinnerListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) 
		{
			mapView.resizeMap((Integer) rowCountModelSpinner.getValue(), (Integer) colCountModelSpinner.getValue());
			mapView.repaint();
		}
	}
	
	class LetahCostListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) 
		{
			mapView.updateObstacles((Integer) lethalCostModelSpinner.getValue());
			mapView.repaint();
		}
	}

	public PathFinderView() 
	{
		System.out.println("View()");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel pathFinderPanel = (JPanel) this.getContentPane();

		pathFinderPanel.setLayout(new BorderLayout());

		mapView.setPreferredSize(new Dimension(PREFERED_SIZE_WIDTH, PREFERED_SIZE_HEIGHT));
		pathFinderPanel.add(mapView, BorderLayout.CENTER);
		pathFinderPanel.add(getAppContolPanel(), BorderLayout.WEST);

		setJMenuBar(getAppMenuBar());

		this.pack();
		this.setVisible(true);
		this.revalidate();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected JComponent getAppContolPanel() 
	{
		JPanel controlPanel = new JPanel();
		controlPanel.setBackground(new Color(216, 224, 248));
		controlPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		rowCountSpinner.addChangeListener(new SpinnerListener());
		colCountSpinner.addChangeListener(new SpinnerListener());
		lethalCostModelSpinner.addChangeListener(new LetahCostListener());

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		controlPanel.add(startButton, gbc);

		gbc.gridx = 0;
		++gbc.gridy;
		controlPanel.add(stopButton, gbc);
		
		gbc.gridx = 0;
		++gbc.gridy;
		controlPanel.add(clearButton, gbc);

		JLabel columnLabel = new JLabel("Columns: ");
		columnLabel.setHorizontalAlignment(SwingConstants.LEFT);
		gbc.gridx = 0;
		++gbc.gridy;
		gbc.gridwidth = 1;
		controlPanel.add(columnLabel, gbc);

		++gbc.gridx;
		gbc.gridwidth = 1;
		controlPanel.add(colCountSpinner, gbc);

		JLabel rowLabel = new JLabel("Rows: ");
		rowLabel.setHorizontalAlignment(SwingConstants.LEFT);
		gbc.gridx = 0;
		++gbc.gridy;
		gbc.gridwidth = 1;
		controlPanel.add(rowLabel, gbc);

		++gbc.gridx;
		gbc.gridwidth = 1;
		controlPanel.add(rowCountSpinner, gbc);

		JLabel algorithmLabel = new JLabel("Algorithm: ");
		algorithmLabel.setHorizontalAlignment(SwingConstants.LEFT);
		gbc.gridx = 0;
		++gbc.gridy;
		controlPanel.add(algorithmLabel, gbc);

		++gbc.gridx;
		controlPanel.add(algortihmsComboBox, gbc);
		
		JLabel neighboursCountLabel = new JLabel("Neighbours: ");
		algorithmLabel.setHorizontalAlignment(SwingConstants.LEFT);
		gbc.gridx = 0;
		++gbc.gridy;
		controlPanel.add(neighboursCountLabel, gbc);

		++gbc.gridx;
		controlPanel.add(neighboursCountComboBox, gbc);
		
		JLabel lethalLabel = new JLabel("Lethal cost: ");
		lethalLabel.setHorizontalAlignment(SwingConstants.LEFT);
		gbc.gridx = 0;
		++gbc.gridy;
		controlPanel.add(lethalLabel, gbc);

		++gbc.gridx;
		controlPanel.add(lethalCostSpinner, gbc);
		
		JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
		separator.setPreferredSize(new Dimension(5,1));
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		++gbc.gridy;
		controlPanel.add(separator, gbc);

        JPanel algorithmInfoPanel = new JPanel();
        algorithmInfoPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbcAlgorithmInfo = new GridBagConstraints();
		gbcAlgorithmInfo.fill = GridBagConstraints.HORIZONTAL;
		gbcAlgorithmInfo.gridx = 0;
		gbcAlgorithmInfo.gridy = 0;
		gbcAlgorithmInfo.insets = new Insets(4, 4, 4, 4);
		gbcAlgorithmInfo.anchor = GridBagConstraints.NORTHWEST;
		
        algorithmInfoPanel.setBackground(new Color(216, 224, 248));
        algorithmInfoPanel.setBorder(BorderFactory.createTitledBorder("Algorithm"));
		
		JLabel algorithmDurationTimeLabel = new JLabel("Time: ");
		algorithmInfoPanel.add(algorithmDurationTimeLabel, gbcAlgorithmInfo);
		++gbcAlgorithmInfo.gridx;
		algorithmInfoPanel.add(timeAlgorithmExecution, gbcAlgorithmInfo);
		
		JLabel algorithmPathLengthLabel = new JLabel("Path lenght: ");
		gbcAlgorithmInfo.gridx = 0;
		++gbcAlgorithmInfo.gridy;
		algorithmInfoPanel.add(algorithmPathLengthLabel, gbcAlgorithmInfo);
		++gbcAlgorithmInfo.gridx;
		algorithmInfoPanel.add(pathLenghtAlgorithm, gbcAlgorithmInfo);
		
		controlPanel.add(algorithmInfoPanel, gbc);
		
		GridBagConstraints gbcFiller = new GridBagConstraints();
		gbcFiller.gridy = 33;
		gbcFiller.weightx = 1.0;
		gbcFiller.weighty = 1.0;
		gbcFiller.fill = GridBagConstraints.BOTH;
		controlPanel.add(Box.createGlue(), gbcFiller);

		controlPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		return controlPanel;
	}

	protected JMenuBar getAppMenuBar() 
	{
		PathFinderMenuHandler menuHandler = new PathFinderMenuHandler(this);

		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);

		// map menu
		JMenu fileMenu = new JMenu(MENU_MAP_NAME);
		menubar.add(fileMenu);

		//		JMenuItem newMapItem = new JMenuItem(MENU_NEW_MAP_NAME);
		//		newMapItem.setMnemonic(KeyEvent.VK_N);
		//		newMapItem.setActionCommand(MENU_NEW_MAP_NAME);
		//		fileMenu.add(newMapItem);
		//		newMapItem.addActionListener(menuHandler);

		JMenuItem openMapItem = new JMenuItem(MENU_OPEN_MAP_NAME);
		openMapItem.setMnemonic(KeyEvent.VK_N);
		fileMenu.add(openMapItem);

		JMenuItem saveMapItem = new JMenuItem(MENU_SAVE_MAP_NAME);
		fileMenu.add(saveMapItem);

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setActionCommand("Exit");

		fileMenu.addSeparator();

		fileMenu.add(exitMenuItem); 

		openMapItem.addActionListener(menuHandler);
		saveMapItem.addActionListener(menuHandler);
		exitMenuItem.addActionListener(menuHandler);

		// about menu
		//		final JMenu aboutMenu = new JMenu("About");
		//		menubar.add(aboutMenu);

		return menubar;
	}

	public void addStartListener(ActionListener al) 
	{
		startButton.addActionListener(al);
	}
	
	public void addStopListener(ActionListener al) 
	{
		stopButton.addActionListener(al);
	}

	public void addClearButtonListener(ActionListener al) 
	{
		clearButton.addActionListener(al);
	}
	
	public void addNeighborsCountListener(ActionListener al)
	{
		neighboursCountComboBox.addActionListener(al);
	}

	public void clearMap() 
	{
		this.mapView.SetDefaultValues();
		this.mapView.setDefaultColors();
		this.mapView.resizeMap((Integer) rowCountSpinner.getValue(), (Integer) colCountSpinner.getValue());
		this.mapView.updateObstacles((Integer) lethalCostModelSpinner.getValue());
	}

	public void clearValues() 
	{
		this.mapView.SetDefaultValues();
		this.mapView.setDefaultVisualColors();
	}

	public GridMap getMapView() 
	{
		return this.mapView.getMap();
	}

	public void setMapView(Node[][] map) 
	{
		this.mapView.setMap(map);
	}

	public int getCountRow() 
	{
		return this.mapView.getCountRow();
	}

	public int getCountColumn() 
	{
		return this.mapView.getCountColumn();
	}

	public Node getStartNode()
	{
		return this.mapView.getStartNode();
	}

	public Node getEndNode()
	{
		return this.mapView.getEndNode();
	}

	public void setPath(List<Node> path) 
	{
		this.mapView.setPath(path);
	}

	public void setStartNode(int row, int column)
	{
		this.mapView.setStartNode(row, column);
	}

	public void setEndNode(int row, int column)
	{
		this.mapView.setEndNode(row, column);
	}

	public void setRowCountSpinner(int value)
	{
		rowCountModelSpinner.setValue(value);
	}

	public void setColCountModelSpinner(int value)
	{
		colCountModelSpinner.setValue(value);
	}

	public void setCosts(int[][] costs, int height, int width) 
	{
		setRowCountSpinner(height);
		setColCountModelSpinner(width);
		this.mapView.setCosts(costs, height, width);
	}
	
	public void setTimeAlgorithmExecution(String duration)
	{
		this.timeAlgorithmExecution.setText(duration);
	}
	
	public void setPathLenghtAlgorithm(String pathLenght)
	{
		this.pathLenghtAlgorithm.setText(pathLenght);
	}

	public void updateObstacles(int lethal)
	{
		this.mapView.updateObstacles(lethal);
	}
	
//	public int[][] getObstacles()
//	{
//		return this.mapView.getObstacles();
//	}
	
	public String getAlgorithmName()
	{
		return algortihmsComboBox.getSelectedItem().toString();
	}
}

class PathFinderMenuHandler implements ActionListener, ItemListener {
	PathFinderView view;

	public PathFinderMenuHandler(PathFinderView view)
	{
		this.view = view;
	}

	public void actionPerformed(ActionEvent ae) 
	{
		String arg = (String) ae.getActionCommand();

		if (arg.equals("Open map")) {
			JFileChooser openFileChooser = new JFileChooser();
			openFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", ImageIO.getReaderFormatNames()));
			int option = openFileChooser.showOpenDialog(view);

			if (option != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File file = openFileChooser.getSelectedFile();
			String fileName = openFileChooser.getSelectedFile().getAbsolutePath();
			String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());

			int[][] cost; // lethal = 255, freespace = 0
			int height = 0;
			int width = 0;
			
			boolean isFormatSupported = false;
			
			String[] supportedInputExtensions = ImageIO.getReaderFormatNames();
			fileExtension = fileExtension.toLowerCase();
			
			for (String ext: supportedInputExtensions) {
			    if (ext.trim().toLowerCase().contains(fileExtension))
			    	isFormatSupported =  true;
			}
			
			if (isFormatSupported) {
				BufferedImage img = null;

				try {
					img = ImageIO.read(file);
				} catch (IOException e) {

				}

				height = img.getHeight();
				width = img.getWidth();

				int rgb;
				int red;
				int green;
				int blue;

				cost = new int[height][width];

				for (int i=0; i<height; ++i) {
					for (int j=0; j<width; ++j) {
						rgb = img.getRGB(j, i);
						red = (rgb >> 16) & 0x000000FF;
						green = (rgb >> 8) & 0x000000FF;
						blue = (rgb) & 0x000000FF;

						cost[i][j] = (red + green + blue) / 3;
					}
				}
			} else if (fileExtension.equals("pgm")) {

				try {
					BufferedReader reader = new BufferedReader(new BufferedReader(new FileReader(new File(fileName))));
					String magic = reader.readLine(); // "P2" - Plain PGM file, "P5" - Raw PGM file
					String line = reader.readLine();

					while (line.startsWith("#")) { // removing comments from PGM file
						line = reader.readLine();
					}

					Scanner scanner = new Scanner(line); // read canvas size
					width = scanner.nextInt();
					height = scanner.nextInt();

					line = reader.readLine();// read max value
					scanner = new Scanner(line);
					
					// int maxVal = scanner.nextInt();
					// TODO: handle size of gray bytes representation
					// int grayRepBytes = 0; 
					// if (maxVal < 256) {
					// grayRepBytes = 1;
					// } else {
					// grayRepBytes = 2;
					// }

					cost = new int[height][width];

					if (magic.equals("P2")) { // plain pgm

						scanner = new Scanner(reader);

						for (int i=0; i<height; ++i) {
							for (int j=0; j<width; ++j) {
								cost[i][j] = scanner.nextInt();
							}
						}
					} else if (magic.equals("P5")) { // raw pgm
						FileInputStream fileInputStream = new FileInputStream(fileName);
						DataInputStream dis = new DataInputStream(fileInputStream);

						// discard header
						int numLines = 4;
						while (numLines > 0) {
							char c;
							do {
								c = (char)(dis.readUnsignedByte());
							} while (c != '\n');
							numLines--;
						}

						for (int i=0; i < height; ++i) {
							for (int j = 0; j < width; ++j) {
								cost[i][j] = dis.readUnsignedByte();
							}
						}
					}
				}

				catch(Throwable t) {
					t.printStackTrace(System.err) ;
					return ;
				}

			} else {
				System.out.println("File format not supported.");
				return;
			}
			
			// inverse cost to: lethal = 255, freespace = 0
			for (int i = 0 ; i < height; ++i) {
				for (int j = 0 ; j < width; ++j) {
					cost[i][j] = 255 - cost[i][j];					
				}	
			}
			
			this.view.setCosts(cost, height, width);
			this.view.setStartNode(height/2, width/3);
			this.view.setEndNode(height/2, width*2/3);
			
			this.view.clearMap();
		}
		else if (arg.equals("Save map")) {
			JFileChooser chooser = new JFileChooser();
			int option = chooser.showSaveDialog(view);

			if (option != JFileChooser.APPROVE_OPTION) {
				return;
			}

			BufferedImage img = new BufferedImage(view.getCountColumn(), view.getCountRow(), BufferedImage.TYPE_INT_RGB);

			File file = chooser.getSelectedFile();			

			for(int x = 0; x < view.getCountColumn(); ++x) {
				for(int y = 0; y < view.getCountRow(); ++y) {
					Node node = view.getMapView().getNode(y, x);
					
					int colorFromCost = 0;
					
					if (node.getType() == Node.Type.NORMAL) {
						colorFromCost = 255 - node.getCost();
					}
					
					int r = colorFromCost;
					int g = colorFromCost; 
					int b = colorFromCost;
					
					int col = (r << 16) | (g << 8) | b;
					img.setRGB(x, y, col);
				}
			}
			try {
				ImageIO.write(img, "BMP", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (arg.equals("Exit")) {
			exitSystem();
		}

		this.view.repaint();
	}

	public void itemStateChanged(ItemEvent e) 
	{
		// TODO Auto-generated method stub
	}

	private void exitSystem() {
		System.exit(0);
	}
}