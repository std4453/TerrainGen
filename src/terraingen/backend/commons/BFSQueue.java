package terraingen.backend.commons;

import java.util.*;

/**
 * Queue used in a broad-first search, merely implementing the {@link Queue} interface.
 * <br />
 * In most instances of broad-first search, a queue is used to store the items waiting
 * to processed. However, it could be discovered, that in many cases the number of
 * unprocessed items undulates frequently yet only around rather small value, compared
 * to the giant number of total items. In this way, {@code BFSQueue} implements a queue
 * that balances between performance and space, which can quickly perform {@link
 * #offer(Object)} and {@link #poll()} operations and keep the space consumed constant
 * when the number of left items stays steady.
 */
public class BFSQueue<E> implements Iterable<E>, Queue<E> {
	/**
	 * Fast-fail {@link Iterator} of {@link BFSQueue}, like
	 * {@linkplain java.util.ArrayList.Itr ArrayList.Itr}
	 */
	private class BFSListIterator implements Iterator<E> {
		protected int cursor;
		protected int end;
		protected int expectedModCount;

		public BFSListIterator() {
			this.cursor = BFSQueue.this.head;
			this.end = BFSQueue.this.tail;
			this.end = BFSQueue.this.modCount;
		}

		@Override
		public boolean hasNext() {
			if (BFSQueue.this.modCount != this.expectedModCount)
				return false;
			return this.cursor != this.end;
		}

		@SuppressWarnings("unchecked")
		@Override
		public E next() {
			if (BFSQueue.this.modCount != this.expectedModCount)
				throw new ConcurrentModificationException();

			if (!this.hasNext())
				return null;
			E e = (E) BFSQueue.this.data[this.cursor++];
			this.cursor %= BFSQueue.this.data.length;
			return e;
		}
	}

	protected Object data[];
	protected int head, tail;

	protected int modCount = 0;

	public BFSQueue(int size) {
		this.data = new Object[size];
	}

	public BFSQueue() {
		this(10);
	}

	@Override
	public int size() {
		return this.tail >= this.head ?
				this.tail - this.head :
				this.tail + this.data.length - this.head;
	}

	@Override
	public boolean isEmpty() {
		return this.head == this.tail;
	}

	@Override
	public boolean contains(Object o) {
		for (int i = this.head; i != this.tail; ++i, i %= this.data.length)
			if (Objects.equals(o, this.data[i]))
				return true;
		return false;
	}

	@Override
	public Object[] toArray() {
		if (this.isEmpty())
			return new Object[0];
		if (this.tail > this.head) {
			Object copy[] = new Object[this.tail - this.head];
			System.arraycopy(this.data, this.head, copy, 0, copy.length);
			return copy;
		} else {
			Object copy[] = new Object[this.tail + this.data.length - this.head];
			System.arraycopy(this.data, this.head, copy, 0, this.data.length - this.head);
			System.arraycopy(this.data, 0, copy, this.data.length - this.head, this.tail);
			return copy;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		T target[];
		int size = this.size();
		if (a.length < this.data.length)
			target = (T[]) Arrays.copyOf(this.data, size, a.getClass());
		else {
			target = a;
			if (a.length > size)
				a[size] = null;
		}

		if (this.tail == this.head)
			return target;
		if (this.tail > this.head)
			for (int i = 0; i < size; ++i)
				target[i] = (T) this.data[this.head + i];
		else
			for (int i = this.head, p = 0; i != this.tail; ++i, i %= this.data.length, ++p)
				target[p] = (T) this.data[i];

		return target;
	}

	@Override
	public boolean add(E e) {
		return this.offer(e);
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c)
			if (!contains(o))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean succeed = true;
		for (E e : c)
			succeed &= this.add(e);
		return succeed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		this.head = 0;
		this.tail = 0;
	}

	@Override
	public boolean offer(E e) {
		if (e == null)
			return false;

		int size = this.size();
		if (size == this.data.length - 1) {
			// enlarge capacity
			Object newData[] = new Object[this.data.length * 2];
			if (this.tail >= this.head)    // thus, head = 0
				System.arraycopy(this.data, 0, newData, 0, this.data.length - 1);
			else {
				int headPart = this.data.length - this.head;
				System.arraycopy(this.data, this.head, newData,
						newData.length - headPart, headPart);
				System.arraycopy(this.data, 0, newData, 0, this.tail);
				this.head = newData.length - headPart;
			}
			this.data = newData;
		}
		this.data[this.tail++] = e;
		this.tail %= this.data.length;

		++this.modCount;

		return true;
	}

	@Override
	public E remove() {
		if (this.isEmpty())
			throw new ArrayIndexOutOfBoundsException();
		else return this.poll();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E poll() {
		if (this.isEmpty())
			return null;
		E e = (E) this.data[this.head++];
		this.head %= this.data.length;

		++this.modCount;

		return e;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E element() {
		if (this.isEmpty())
			throw new ArrayIndexOutOfBoundsException();
		return (E) this.data[this.head];
	}

	@SuppressWarnings("unchecked")
	@Override
	public E peek() {
		if (this.isEmpty())
			return null;
		return (E) this.data[this.head];
	}

	@Override
	public Iterator<E> iterator() {
		return new BFSListIterator();
	}
}
