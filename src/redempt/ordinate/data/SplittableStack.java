package redempt.ordinate.data;

public class SplittableStack<T> {

	private StackNode<T> head;
	private int size;

	public SplittableStack<T> split() {
		SplittableStack<T> clone = new SplittableStack<>();
		clone.head = head;
		clone.size = size;
		return clone;
	}

	public void push(T elem) {
		size++;
		StackNode<T> node = new StackNode<>(elem);
		if (head == null) {
			head = node;
			return;
		}
		node.next = head;
		head = node;
	}

	public T pop() {
		size--;
		T val = head.data;
		head = head.next;
		return val;
	}

	public int size() {
		return size;
	}

	private static class StackNode<T> {

		private T data;
		private StackNode<T> next;

		public StackNode(T data) {
			this.data = data;
		}

	}

}