package io.github.rypofalem.weighted_table;


public final class WeightedLeaf<T> extends WeightedNode<T> {
    private T t;

    WeightedLeaf(double weight, T t) {
        super(weight);
        this.t = t;
    }

    public T get(){
        return t;
    }
}
