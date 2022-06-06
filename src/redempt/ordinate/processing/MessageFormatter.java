package redempt.ordinate.processing;

public interface MessageFormatter<T> {

	public String[] apply(T sender, String... context);

}
