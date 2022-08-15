package redempt.ordinate.constraint;

/**
 * Represents a parser that can create constraints from strings specified in the command file
 * @param <T> The sender type
 * @param <V> The type the constraint is applied to
 * @author Redempt
 */
public interface ConstraintParser<T, V> {
	
	/**
	 * Parse a constraint
	 * @param val The string value of the constraint
	 * @return The created constraint
	 */
	public Constraint<T, V> parse(String val);

}
