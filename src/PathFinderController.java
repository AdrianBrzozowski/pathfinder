import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PathFinderController {

	PathFinderModel model;
	PathFinderView view;

	public PathFinderController(PathFinderModel model, PathFinderView view) 
	{
		this.model = model;
		this.view = view;

		this.model.resize(view.getCountRow(), view.getCountColumn()); // resize model size to view size

		this.view.addStartListener(new StartListener());
		this.view.addClearButtonListener(new ClearListener());

		this.view.setVisible(true);
	}

	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) 
		{

			view.clearValues();

			model.resize(view.getCountRow(), view.getCountColumn());
			model.setMap(view.getMapView());

			Node[][] imMap = new Node[view.getCountRow()][view.getCountColumn()]; // imported map
			for (int i=0; i<view.getCountRow(); ++i) {
				for (int j=0; j<view.getCountColumn(); ++j) {
					imMap[i][j] = new Node(view.getMapView()[i][j]);
				}
			}
			
			model.setMap(imMap);			

			List<Node> path = model.runBreadthFirstSerach(view.getStartNode(), view.getEndNode());

			Node[][] expMap = new Node[view.getCountRow()][view.getCountColumn()];

			for (int i=0; i<view.getCountRow(); ++i) {
				for (int j=0; j<view.getCountColumn(); ++j) {
					expMap[i][j] = new Node(model.getMap()[i][j]);
				}
			}

			view.setMapView(expMap);
			view.setPath(path);
			view.repaint();
		}
	}

	class ClearListener implements ActionListener {
		public void actionPerformed(ActionEvent e) 
		{
			System.out.println("ClearListener()");
			view.clearMap();
			view.repaint();
		}
	}
}
