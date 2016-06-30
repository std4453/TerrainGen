package terraingen.utils;

import java.util.Iterator;

public class IterableWrapper<T> implements Iterable<T> {
	protected Iterator<T>	iterator;

	public IterableWrapper(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	@Override
	public Iterator<T> iterator() {
		return this.iterator;
	}
}
