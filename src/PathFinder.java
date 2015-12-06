import javax.swing.SwingUtilities;

public class PathFinder {
	public static void main(String[] args) 
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() 
			{
				PathFinderModel model = new PathFinderModel();
				PathFinderView view = new PathFinderView();
				@SuppressWarnings("unused")
				PathFinderController controller = new PathFinderController(model, view);
			}	
		});
	}
}
