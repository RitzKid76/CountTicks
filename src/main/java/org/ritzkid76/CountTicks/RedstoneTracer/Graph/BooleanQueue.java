package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;

public class BooleanQueue<T> {
	private final LinkedList<T> primary = new LinkedList<>();
	private final LinkedList<T> secondary = new LinkedList<>();
	private final Predicate<T> priority;

	public BooleanQueue(Predicate<T> p) {
	    priority = p;
	}

	public void add(T element) {
	    if(priority.test(element))
	        primary.add(element);
	    else secondary.add(element);
	}

	public void addAll(Collection<T> elements) {
	    for(T element : elements) {
	        add(element);
	    }
	}

	public boolean isEmpty() {
	    return primary.isEmpty() && secondary.isEmpty();
	}

	public T remove() {
	    if(!primary.isEmpty())
	        return primary.remove();
	    return secondary.remove();
	}
}
