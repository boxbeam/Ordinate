package redempt.ordinate.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SplittableList<T> implements Iterable<T> {
	
	private T[] array;
	private boolean[] removed;
	private int removedCount = 0;
	private int start;
	
	public SplittableList(T[] array, int start) {
		this.array = array;
		this.start = start;
	}
	
	public SplittableList(T[] array) {
		this(array, 0);
	}
	
	public void skip(int amount) {
		int counter = 0;
		for (int i = start; i < array.length; i++) {
			if (!isRemoved(i) && counter++ == amount) {
				start = i;
				return;
			}
		}
	}
	
	private boolean isRemoved(int index) {
		return removed != null && removed[index];
	}
	
	public T peek() {
		if (hasNext()) {
			return array[start];
		}
		return null;
	}
	
	public T poll() {
		while (isRemoved(start)) start++;
		T val = peek();
		if (val != null) {
			start++;
		}
		return val;
	}
	
	public boolean hasNext() {
		return start < array.length - removedCount;
	}
	
	public int size() {
		return array.length - start - removedCount;
	}
	
	public T get(int index) {
		return array[trueIndex(index)];
	}
	
	private int trueIndex(int index) {
		int counter = 0;
		for (int i = start; i < array.length; i++) {
			if (!isRemoved(i) && counter++ == index) {
				return i;
			}
		}
		return array.length;
	}
	
	public void remove(int index) {
		if (removed == null) {
			removed = new boolean[array.length];
		}
		removedCount++;
		index = trueIndex(index);
		removed[index] = true;
	}
	
	public SplittableList<T> split(int newStart) {
		SplittableList<T> list = new SplittableList<>(array, start + newStart);
		if (removed != null) {
			list.removed = Arrays.copyOf(removed, removed.length);
		}
		return list;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			
			int index = start;
			
			private void advanceToNext() {
				while (isRemoved(index)) index++;
			}
			
			@Override
			public boolean hasNext() {
				advanceToNext();
				return index < array.length;
			}
			
			@Override
			public T next() {
				advanceToNext();
				return array[index++];
			}
			
		};
	}
	
	@Override
	public void forEach(Consumer<? super T> action) {
		for (int i = start; i < array.length; i++) {
			if (!isRemoved(i)) {
				action.accept(array[i]);
			}
		}
	}
	
	@Override
	public Spliterator<T> spliterator() {
		return Iterable.super.spliterator();
	}
	
}
