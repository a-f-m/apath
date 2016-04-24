package org.afm.basics.iteration;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * Base for iterator processing.
 * 
 * @author afm
 *
 * @param <S> source ground type of the iterator
 * @param <T> target ground type of the iterator
 */
abstract class IterationProcessing<S, T> implements Iterator<T>, Iterable<T> {

	private Iterator<S> source;
	protected T current;

	public IterationProcessing() {
	}

	public IterationProcessing(Iterator<S> source) {
		this.source = source;
	}

	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public T next() {
		throw new RuntimeException("to be overwritten");
	}
	
	public S nextOfSource() {
		return source.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}

	public IterationProcessing<S, T> iterate() {
		while (hasNext()) next();
		return this;
	}

	protected T forward() {
		
		if (current != null) {
			T h = current;
			current = null;
			return h;
		} else {
			// to ensure usual non-idempotence of overwritten next()
			if (hasNext()) {
				return next();
			} else {
				throw new NoSuchElementException();
			}
		}
	}
}
