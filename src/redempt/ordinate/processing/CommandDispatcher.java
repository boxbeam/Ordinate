package redempt.ordinate.processing;

public interface CommandDispatcher<T> {
	
	public void dispatch(T sender, String[] args);
	
}
