package redempt.ordinate.constraint;

public interface ConstraintParser<T, V> {

	public Constraint<T, V> parse(String val);

}
