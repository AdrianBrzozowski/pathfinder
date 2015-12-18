
public class AlgorithmProperty {
	final int DEFAULT_DIRS_COUNT = 4;
	
	private int dirsCount = DEFAULT_DIRS_COUNT;

	public AlgorithmProperty() 
	{
		this.dirsCount = DEFAULT_DIRS_COUNT;
	}
	
	public AlgorithmProperty(int dirsCount) 
	{
		this.dirsCount = dirsCount;
	}
	
	public int getDirsCount()
	{
		return this.dirsCount;
	}
	
	public void setDirsCount(int dirsCount)
	{
		this.dirsCount = dirsCount;
	}
}
