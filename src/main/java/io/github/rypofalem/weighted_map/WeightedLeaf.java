package io.github.rypofalem.weighted_map;


public class WeightedLeaf<T> extends WeightedNode<T> {
    protected T t;

   protected WeightedLeaf(double weight, T t) {
        super(weight);
        this.t = t;
    }

    public T get(){
        return t;
    }
}
