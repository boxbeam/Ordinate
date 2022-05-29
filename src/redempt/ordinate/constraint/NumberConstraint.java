package redempt.ordinate.constraint;

import redempt.ordinate.processing.MessageFormatter;

import java.util.function.Function;
import java.util.function.Predicate;

public class NumberConstraint {

	public static <T, V extends Number & Comparable<V>> ConstraintParser<T, V> createParser(Function<String, V> parseNumber, MessageFormatter<T> error) {
		return s -> {
			s = s.replace(" ", "");
			String[] split = s.split(",", -1);
			if (split.length != 2) {
				throw new IllegalArgumentException("Invalid constraint syntax: " + s);
			}
			Predicate<V> lowerBound = n -> true;
			Predicate<V> upperBound = n -> true;
			String display = s.replaceAll("^,(-?\\d+)", "<=$1").replaceAll("(-?\\d+),$", ">=$1").replace(",", " - ");
			if (split[0].length() != 0) {
				V lower = parseNumber.apply(split[0]);
				lowerBound = n -> n.compareTo(lower) >= 0;
			}
			if (split[1].length() != 0) {
				V upper = parseNumber.apply(split[1]);
				upperBound = n -> n.compareTo(upper) <= 0;
			}
			Predicate<V> range = lowerBound.and(upperBound);
			return (ctx, val) -> range.test(val) ? null : error.apply(ctx.sender(), val.toString(), display);
		};
	}

}
