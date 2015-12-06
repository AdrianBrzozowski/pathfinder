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
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
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
	private JButton clearButton = new JButton(CLEAR_BUTTON_NAME);
	private SpinnerModel rowCountModelSpinner = new SpinnerNumberModel(ROW_COUNT_DEFAULT, ROW_COUNT_MIN, ROW_COUNT_MAX,
			ROW_COUNT_STEP);
	private JSpinner rowCountSpinner = new JSpinner(rowCountModelSpinner);
	private SpinnerModel colCountModelSpinner = new SpinnerNumberModel(COL_COUNT_DEFAULT, COL_COUNT_MIN, COL_COUNT_MAX,
			COL_COUNT_STEP);
	private JSpinner colCountSpinner = new JSpinner(colCountModelSpinner);

	class SpinnerListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) 
		{
			mapView.resizeMap((Integer) rowCountModelSpinner.getValue(), (Integer) colCountModelSpinner.getValue());
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

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		controlPanel.add(startButton, gbc);
		startButton.setPreferredSize(new Dimension(150, 30));

		gbc.gridx = 0;
		gbc.gridy = 1;
		controlPanel.add(clearButton, gbc);
		clearButton.setPreferredSize(new Dimension(150, 30));

		JLabel columnLabel = new JLabel("Columns: ");
		columnLabel.setHorizontalAlignment(SwingConstants.LEFT);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		controlPanel.add(columnLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		controlPanel.add(colCountSpinner, gbc);

		JLabel rowLabel = new JLabel("Rows: ");
		rowLabel.setHorizontalAlignment(SwingConstants.LEFT);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		controlPanel.add(rowLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		controlPanel.add(rowCountSpinner, gbc);

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

	void addStartListener(ActionListener al) 
	{
		startButton.addActionListener(al);
	}

	void addClearButtonListener(ActionListener al) 
	{
		clearButton.addActionListener(al);
	}

	public void clearMap() 
	{
		this.mapView.SetDefaultValues();
		this.mapView.setDefaultColors();
		this.mapView.resizeMap((Integer) rowCountSpinner.getValue(), (Integer) colCountSpinner.getValue());
	}

	public void clearValues() 
	{
		this.mapView.SetDefaultValues();
	}

	public Node[][] getMapView() 
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

	public void setObstacles(int[][] obstacles, int height, int width) 
	{
		setRowCountSpinner(height);
		setColCountModelSpinner(width);
		this.mapView.setObstacles(obstacles, height, width);
	}

	public int[][] getObstacles()
	{
		return this.mapView.getObstacles();
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

			int[][] result;
			int height = 0;
			int width = 0;

			if (fileExtension.equals("bmp")) {

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

				result = new int[height][width];

				for (int i=0; i<height; ++i) {
					for (int j=0; j<width; ++j) {
						rgb = img.getRGB(j, i);
						red = (rgb >> 16) & 0x000000FF;
						green = (rgb >> 8) & 0x000000FF;
						blue = (rgb) & 0x000000FF;

						result[i][j] = (red + green + blue) / 3;
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

					result = new int[height][width];

					if (magic.equals("P2")) { // plain pgm

						scanner = new Scanner(reader);

						for (int i=0; i<height; ++i) {
							for (int j=0; j<width; ++j) {
								result[i][j] = scanner.nextInt();
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
								result[i][j] = dis.readUnsignedByte();
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

			this.view.setObstacles(result, height, width);
			this.view.setStartNode(height/2, width/3);
			this.view.setEndNode(height/2, width*2/3);
		}
		else if (arg.equals("Save map")) {
			JFileChooser chooser = new JFileChooser();
			int option = chooser.showSaveDialog(view);

			if (option != JFileChooser.APPROVE_OPTION) {
				return;
			}

			BufferedImage img = new BufferedImage(view.getCountColumn(), view.getCountRow(), BufferedImage.TYPE_INT_RGB);

			File file = chooser.getSelectedFile();

			int obstacles[][] = view.getObstacles();

			for(int x = 0; x < view.getCountColumn(); ++x) {
				for(int y = 0; y < view.getCountRow(); ++y) {
					int r = obstacles[y][x];
					int g = obstacles[y][x]; 
					int b = obstacles[y][x];
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