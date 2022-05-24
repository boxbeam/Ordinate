package redempt.ordinate.processing;

import java.util.function.Supplier;

public class CachedSupplier<T> {

	public static <T> CachedSupplier<T> cached(Supplier<T> supplier) {
		return new CachedSupplier<>(supplier);
	}

	private T val;
	private Supplier<T> supplier;

	private CachedSupplier(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public T get() {
		if (val == null) {
			val = supplier.get();
		}
		return val;
	}

}
