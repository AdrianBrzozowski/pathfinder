import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class PathFinderController {

	PathFinderModel model;
	PathFinderView view;
	
	AlgorithmActionListener algorithmListner;

	class AlgorithmListener implements AlgorithmActionListener {
		@Override
		public void finished() 
		{
			view.setPath(model.getPath());
			view.repaint();
		}

		@Override
		public void refresh() 
		{
			view.repaint();
		}

		@Override
		public void setTimeExecution(String duration) 
		{
			view.setTimeAlgorithmExecution(duration + " ms");
		}

		@Override
		public void setPathLength(String length) 
		{
			view.setPathLenghtAlgorithm(length);
		}
	}
	
	public PathFinderController(PathFinderModel model, PathFinderView view) 
	{
		this.model = model;
		this.view = view;

		// add listeners
		this.view.addStartListener(new StartListener());
		this.view.addStopListener(new StopListener());
		this.view.addClearButtonListener(new ClearListener());
		this.view.addNeighborsCountListener(new NeighboursCountChangeListener());
		
		algorithmListner = new AlgorithmListener();
		this.model.addAlgorithmListener(algorithmListner);

		this.view.setVisible(true);
	}

	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) 
		{
			view.clearValues();
			view.setTimeAlgorithmExecution(" - ");
			view.setPathLenghtAlgorithm(" - ");
			
			model.setMap(view.getMapView());
			model.startAlgorithm(view.getAlgorithmName(), view.getStartNode(), view.getEndNode());
		}
	}
	
	class StopListener implements ActionListener {
		public void actionPerformed(ActionEvent e) 
		{
			model.stopAlgorithm();
		}
	}

	class ClearListener implements ActionListener {
		public void actionPerformed(ActionEvent e) 
		{
			view.clearMap();
			view.repaint();
		}
	}
	
	class NeighboursCountChangeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		        JComboBox<?> cb = (JComboBox<?>)e.getSource();
		        model.setNeighboursCount(Integer.parseInt((String) cb.getSelectedItem()));
		}
	}
}
