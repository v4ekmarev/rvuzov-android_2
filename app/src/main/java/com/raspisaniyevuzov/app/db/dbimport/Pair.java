package com.raspisaniyevuzov.app.db.dbimport;

import java.io.Serializable;

public class Pair<A, B> implements Serializable, Comparable<Pair> {
    private static final long serialVersionUID = -8257019350916169197L;

    public static final <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
        return new Pair<T1, T2>(first, second);
    }

    protected A first;

    protected B second;

    public Pair() {
    }

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair other = (Pair) obj;
        if (first == null) {
            if (other.first != null) {
                return false;
            }
        } else if (!first.equals(other.first)) {
            return false;
        }
        if (second == null) {
            if (other.second != null) {
                return false;
            }
        } else if (!second.equals(other.second)) {
            return false;
        }
        return true;
    }

    private Class getClass(Object o) {
        if (o != null) {
            return o.getClass();
        }

        return null;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "Pair<" + getClass(first) + ", " + getClass(second) + "> (" + first + ", " + second + ")";
    }

    @Override
    public int compareTo(Pair another) {
        if (first instanceof Comparable<?>) {
            return ((Comparable) first).compareTo(another.getFirst());
        } else {
            return 0;
        }
    }

}