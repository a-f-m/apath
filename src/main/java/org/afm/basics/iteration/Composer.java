package org.afm.basics.iteration;

import java.util.Iterator;

/**
 * Iterator composing.
 * 
 * @author afm
 *
 * @param <S> ground type of the iterator
 */
public class Composer<S> implements Iterator<S> {

	private Iterator<S> first;
	private Iterator<S> second;
	
	private Iterator<S> current;
	
	public Composer(Iterator<S> first, Iterator<S> second) {
		this.first = current = first;
		this.second = second;
	}

	@Override
	public boolean hasNext() {

		boolean b = handleNull();
		
		if (!b && current == first) {
			current = second;
			b = handleNull();
		}
		
		return b;
	}

	public boolean handleNull() {
		
		return current == null ? false : current.hasNext();
	}

	@Override
	public S next() {
		
		return current.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
