package com.magenta.mc.client.android.util;

public class Triple<A, B, C> {

    public A first;
    public B second;
    public C third;

    public Triple() {
    }

    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return first != null ? first.equals(triple.first) : triple.first == null
                && (second != null ? second.equals(triple.second) : triple.second == null
                && (third != null ? third.equals(triple.third) : triple.third == null));

    }

    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + (third != null ? third.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Triple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}